package school.sptech;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.StylesTable;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

/**
 * Lê os arquivos XLSX de importação/exportação do MDIC usando Apache POI
 * no modo **SAX streaming** (XSSF Event API).
 *
 * Diferente do XSSFWorkbook (que carrega tudo na RAM), este modo lê uma
 * linha por vez, consumindo ~10-30 MB de heap em vez de 500+ MB.
 *
 * Detecta automaticamente o formato do XLSX:
 *
 *   ► FORMATO NCM (detalhado por NCM):
 *     CO_ANO | CO_MES | CO_NCM | CO_UNID | CO_PAIS | SG_UF_NCM | CO_VIA | CO_URF | QT_ESTAT | KG_LIQUIDO | VL_FOB
 *     → SH4 é derivado dos 4 primeiros dígitos do CO_NCM
 *     → CO_MUN fica NULL (não existe neste layout)
 *
 *   ► FORMATO MUN (por município e SH4):
 *     CO_ANO | CO_MES | SH4 | CO_PAIS | SG_UF_MUN | CO_MUN | KG_LIQUIDO | VL_FOB
 *     → SH4 vem direto do XLSX
 *     → CO_MUN vem direto do XLSX
 *
 * Pipeline de dados:
 *   XLSX (disco) → OPCPackage → SAX stream → SheetContentsHandler → JDBC batch insert
 */
public class ComexDataLoader {

    private static final int BATCH_SIZE = 5000;

    /** Formato detectado do XLSX */
    private enum Formato { NCM, MUN }

    // ============================================================
    // API PÚBLICA
    // ============================================================

    public static void carregarExportacao(Path xlsxPath, Connection conn) throws Exception {
        carregarDados(xlsxPath, conn, "base_exportacao", "EXPORTAÇÃO");
    }

    public static void carregarImportacao(Path xlsxPath, Connection conn) throws Exception {
        carregarDados(xlsxPath, conn, "base_importacao", "IMPORTAÇÃO");
    }

    // ============================================================
    // CARREGAMENTO COM BANCO — SAX STREAMING (baixo consumo de RAM)
    // ============================================================

    private static void carregarDados(Path xlsxPath, Connection conn, String tabela, String tipo) throws Exception {
        System.out.println("\n========================================");
        System.out.printf("  CARREGANDO %s (SAX Streaming XLSX)%n", tipo);
        System.out.println("  Arquivo: " + xlsxPath.getFileName());
        System.out.println("========================================\n");

        if (!Files.exists(xlsxPath)) {
            System.err.println("[ERRO] Arquivo não encontrado: " + xlsxPath);
            return;
        }

        // ► Logger de inserção/erros por arquivo
        logJava log = new logJava(xlsxPath.getFileName().toString());

        // ► Pré-carregar FKs existentes
        Set<String> sh4Conhecidos = carregarCodigosExistentes(conn, "codigo_sh4", "CO_SH4");
        Set<String> paisConhecidos = carregarCodigosExistentes(conn, "codigo_pais", "CO_PAIS");
        System.out.printf("[INFO] FKs já existentes no banco: %d SH4, %d países%n",
                sh4Conhecidos.size(), paisConhecidos.size());
        System.out.println("[INFO] Novos códigos serão inseridos automaticamente.");

        // ► Abrir XLSX via OPCPackage (NÃO carrega tudo na RAM)
        try (OPCPackage pkg = OPCPackage.open(xlsxPath.toFile())) {

            ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(pkg);
            XSSFReader xssfReader = new XSSFReader(pkg);
            StylesTable styles = xssfReader.getStylesTable();

            // ► Ler a primeira planilha (sheet1)
            Iterator<InputStream> sheetsData = xssfReader.getSheetsData();
            if (!sheetsData.hasNext()) {
                System.err.println("[ERRO] XLSX sem planilhas!");
                return;
            }

            try (InputStream sheetStream = sheetsData.next()) {

                // ► Criar handler SAX que processa linha a linha
                RowInsertHandler handler = new RowInsertHandler(
                        conn, tabela, tipo, log, sh4Conhecidos, paisConhecidos);

                // ► Configurar parser SAX (namespace-aware é CRÍTICO para XLSX)
                SAXParserFactory spf = SAXParserFactory.newInstance();
                spf.setNamespaceAware(true);  // ← FIX: permite processar namespaces XML do XLSX
                XMLReader xmlReader = spf.newSAXParser().getXMLReader();
                xmlReader.setContentHandler(
                        new XSSFSheetXMLHandler(styles, strings, handler, false));

                System.out.println("[INFO] Iniciando leitura SAX streaming...");
                long startTime = System.currentTimeMillis();

                // ► PROCESSAR — isso itera todo o XLSX sem carregar na RAM
                try {
                    xmlReader.parse(new InputSource(sheetStream));
                    System.out.println("[INFO] Parsing SAX concluído com sucesso.");
                } catch (Exception e) {
                    System.err.printf("[ERRO] Falha durante parsing SAX: %s%n", e.getMessage());
                    e.printStackTrace();
                    return;
                }

                // ► Flush do batch restante
                handler.flushBatch();

                long elapsed = System.currentTimeMillis() - startTime;

                // ► Resultados
                System.out.println();
                System.out.println("\n========================================");
                System.out.printf("  RESULTADO %s (SAX Streaming — %s)%n", tipo, handler.getFormato());
                System.out.println("========================================");
                System.out.printf("  Total de linhas lidas  : %,d%n", handler.getTotalLinhas());
                System.out.printf("  Linhas inseridas       : %,d%n", handler.getInseridos());
                System.out.printf("  Linhas ignoradas       : %,d%n", handler.getIgnorados());
                System.out.printf("  Erros de parsing       : %,d%n", handler.getErros());
                System.out.printf("  Tempo total            : %.1f segundos%n", elapsed / 1000.0);
                System.out.printf("  Velocidade             : %,d linhas/seg%n",
                        elapsed > 0 ? (handler.getTotalLinhas() * 1000L / elapsed) : 0);
                System.out.println("========================================");

                log.sucesso(handler.getInseridos(), handler.getIgnorados());
                log.imprimirResumo();
            }
        }
    }

    // ============================================================
    // SAX HANDLER — processa uma linha por vez (memória constante)
    // ============================================================

    /**
     * Implementação de SheetContentsHandler que recebe os eventos SAX do POI.
     * Acumula as células de cada linha em um Map, e ao final da linha
     * faz o INSERT via JDBC batch.
     */
    private static class RowInsertHandler implements XSSFSheetXMLHandler.SheetContentsHandler {

        private final Connection conn;
        private final String tabela;
        private final String tipo;
        private final logJava log;
        private final Set<String> sh4Conhecidos;
        private final Set<String> paisConhecidos;

        // Mapeamento: índice da coluna → nome do header
        private final Map<Integer, String> headerMap = new LinkedHashMap<>();
        // Células da linha atual: índice da coluna → valor string
        private final Map<Integer, String> currentRow = new HashMap<>();

        private Formato formato;
        private PreparedStatement ps;
        private boolean autoCommitOriginal;

        private int totalLinhas = 0;
        private int inseridos = 0;
        private int ignorados = 0;
        private int erros = 0;
        private boolean headerParsed = false;

        RowInsertHandler(Connection conn, String tabela, String tipo, logJava log,
                         Set<String> sh4Conhecidos, Set<String> paisConhecidos) {
            this.conn = conn;
            this.tabela = tabela;
            this.tipo = tipo;
            this.log = log;
            this.sh4Conhecidos = sh4Conhecidos;
            this.paisConhecidos = paisConhecidos;
        }

        // --- Getters para resultados ---
        int getTotalLinhas() { return totalLinhas; }
        int getInseridos()   { return inseridos; }
        int getIgnorados()   { return ignorados; }
        int getErros()       { return erros; }
        Formato getFormato() { return formato; }

        @Override
        public void startRow(int rowNum) {
            currentRow.clear();
        }

        @Override
        public void endRow(int rowNum) {
            if (rowNum == 0) {
                // ► Primeira linha = header
                parseHeader();
                return;
            }

            // Se formato não foi detectado, pular processamento
            if (formato == null) {
                ignorados++;
                return;
            }

            // ► Linha de dados
            totalLinhas++;
            try {
                DadosLinha dados = extrairDadosStreaming(currentRow, headerMap, formato);
                if (dados == null) {
                    ignorados++;
                    log.erro(totalLinhas, "Dados inválidos ou campos obrigatórios vazios.");
                    return;
                }

                // Auto-inserir códigos SH4 e País desconhecidos
                if (!sh4Conhecidos.contains(dados.sh4)) {
                    inserirCodigoSh4(conn, dados.sh4);
                    sh4Conhecidos.add(dados.sh4);
                }
                if (!paisConhecidos.contains(dados.pais)) {
                    inserirCodigoPais(conn, dados.pais);
                    paisConhecidos.add(dados.pais);
                }

                setarParametros(ps, dados, formato);
                ps.addBatch();
                inseridos++;

                if (inseridos % BATCH_SIZE == 0) {
                    ps.executeBatch();
                    conn.commit();
                    System.out.printf("\r[%s] Progresso: %,d inseridos | %,d ignorados | %,d total",
                            tipo, inseridos, ignorados, totalLinhas);
                }

            } catch (NumberFormatException e) {
                erros++;
                log.erro(totalLinhas, "Erro de formatação numérica: " + e.getMessage());
            } catch (Exception e) {
                erros++;
                log.erro(totalLinhas, e.getMessage());
            }
        }

        @Override
        public void cell(String cellReference, String formattedValue,
                         org.apache.poi.xssf.usermodel.XSSFComment comment) {
            if (cellReference == null) return;
            int col = new CellReference(cellReference).getCol();
            currentRow.put(col, formattedValue != null ? formattedValue : "");
        }

        @Override
        public void headerFooter(String text, boolean isHeader, String tagName) {
            // Ignorar headers/footers do XLSX
        }

        /** Inicializa o header, detecta formato, prepara PreparedStatement */
        private void parseHeader() {
            if (currentRow.isEmpty()) {
                System.err.println("[ERRO] Header vazio! A primeira linha do XLSX não tem dados.");
                return;
            }

            for (Map.Entry<Integer, String> e : currentRow.entrySet()) {
                headerMap.put(e.getKey(), e.getValue().trim().toUpperCase());
            }

            // Detectar formato
            boolean temNCM = headerMap.containsValue("CO_NCM");
            boolean temSH4 = headerMap.containsValue("SH4");

            if (temNCM) {
                formato = Formato.NCM;
            } else if (temSH4) {
                formato = Formato.MUN;
            } else {
                System.err.println("[ERRO] Formato desconhecido! Headers encontrados: " + headerMap.values());
                formato = null;
                return;
            }

            System.out.println("[INFO] Colunas encontradas: " + headerMap.size());
            System.out.println("[INFO] Headers: " + headerMap.values());
            System.out.println("[INFO] Formato detectado: " + formato);

            // Montar SQL e PreparedStatement
            String sql = montarSql(tabela, formato);
            System.out.println("[INFO] SQL: " + sql);

            try {
                autoCommitOriginal = conn.getAutoCommit();
                conn.setAutoCommit(false);
                ps = conn.prepareStatement(sql);
            } catch (Exception ex) {
                throw new RuntimeException("Falha ao preparar SQL: " + ex.getMessage(), ex);
            }

            headerParsed = true;
        }

        /** Flush do batch restante + fechar PreparedStatement */
        void flushBatch() {
            if (ps == null) return;
            try {
                ps.executeBatch();
                conn.commit();
                conn.setAutoCommit(autoCommitOriginal);
                ps.close();
            } catch (Exception e) {
                System.err.println("[ERRO] Falha no flush final: " + e.getMessage());
            }
        }
    }

    // ============================================================
    // EXTRAÇÃO DE DADOS (a partir de Map<colIndex, value>)
    // ============================================================

    /** Estrutura intermediária para dados de uma linha */
    private static class DadosLinha {
        int ano, mes;
        String ncm;       // Apenas formato NCM (8 dígitos)
        String sh4;       // Derivado do NCM ou direto do XLSX
        String pais;
        String uf;
        String mun;       // Apenas formato MUN
        String via;       // Apenas formato NCM
        String urf;       // Apenas formato NCM
        double qtEstat;   // Apenas formato NCM
        double kgLiquido;
        double vlFob;
    }

    /**
     * Extrai dados de uma linha SAX — recebe Map de valores indexados por coluna.
     */
    private static DadosLinha extrairDadosStreaming(
            Map<Integer, String> row, Map<Integer, String> headerMap, Formato formato) {

        DadosLinha d = new DadosLinha();

        String anoStr = getVal(row, headerMap, "CO_ANO");
        String mesStr = getVal(row, headerMap, "CO_MES");

        if (anoStr.isEmpty() || mesStr.isEmpty()) return null;

        d.ano = (int) Double.parseDouble(anoStr);
        d.mes = (int) Double.parseDouble(mesStr);

        if (formato == Formato.NCM) {
            String ncm = getVal(row, headerMap, "CO_NCM");
            if (ncm.isEmpty() || ncm.length() < 4) return null;

            d.ncm = ncm;
            d.sh4 = ncm.substring(0, 4);

            d.pais = getVal(row, headerMap, "CO_PAIS");
            d.uf   = getVal(row, headerMap, "SG_UF_NCM");

            d.via     = getVal(row, headerMap, "CO_VIA");
            d.urf     = getVal(row, headerMap, "CO_URF");
            d.qtEstat = parseDouble(getVal(row, headerMap, "QT_ESTAT"));
            d.mun     = null;
        } else {
            d.sh4  = getVal(row, headerMap, "SH4");
            if (d.sh4.isEmpty()) return null;

            d.pais = getVal(row, headerMap, "CO_PAIS");
            d.uf   = getVal(row, headerMap, "SG_UF_MUN");
            d.mun  = getVal(row, headerMap, "CO_MUN");
            d.ncm  = null;
        }

        // Padronizar SH4 para 4 dígitos
        d.sh4 = padLeft(d.sh4, 4, '0');

        // Padronizar CO_PAIS e UF
        if (d.pais.length() > 4) d.pais = d.pais.substring(0, 4);
        if (d.uf.length() > 2) d.uf = d.uf.substring(0, 2);

        if (d.sh4.isEmpty() || d.pais.isEmpty()) return null;

        d.kgLiquido = parseDouble(getVal(row, headerMap, "KG_LIQUIDO"));
        d.vlFob     = parseDouble(getVal(row, headerMap, "VL_FOB"));

        return d;
    }

    /** Busca o valor de uma coluna pelo nome do header */
    private static String getVal(Map<Integer, String> row, Map<Integer, String> headerMap, String headerName) {
        for (Map.Entry<Integer, String> e : headerMap.entrySet()) {
            if (e.getValue().equals(headerName)) {
                String v = row.get(e.getKey());
                return v != null ? v.trim() : "";
            }
        }
        return "";
    }

    private static double parseDouble(String s) {
        if (s == null || s.isEmpty()) return 0.0;
        try {
            return Double.parseDouble(s.replace(",", "."));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    // ============================================================
    // SQL DINÂMICO POR FORMATO
    // ============================================================

    private static String montarSql(String tabela, Formato formato) {
        if (formato == Formato.NCM) {
            return String.format(
                "INSERT INTO %s (CO_ANO, CO_MES, CO_NCM, SH4, CO_PAIS, SG_UF_MUN, CO_VIA, CO_URF, QT_ESTAT, KG_LIQUIDO, VL_FOB) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", tabela);
        } else {
            return String.format(
                "INSERT INTO %s (CO_ANO, CO_MES, SH4, CO_PAIS, CO_MUN, SG_UF_MUN, KG_LIQUIDO, VL_FOB) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)", tabela);
        }
    }

    private static void setarParametros(PreparedStatement ps, DadosLinha d, Formato formato) throws Exception {
        if (formato == Formato.NCM) {
            ps.setInt(1, d.ano);
            ps.setInt(2, d.mes);
            ps.setString(3, d.ncm);
            ps.setString(4, d.sh4);
            ps.setString(5, d.pais);
            ps.setString(6, d.uf);
            ps.setString(7, d.via);
            ps.setString(8, d.urf);
            ps.setBigDecimal(9, BigDecimal.valueOf(d.qtEstat));
            ps.setBigDecimal(10, BigDecimal.valueOf(d.kgLiquido));
            ps.setBigDecimal(11, BigDecimal.valueOf(d.vlFob));
        } else {
            ps.setInt(1, d.ano);
            ps.setInt(2, d.mes);
            ps.setString(3, d.sh4);
            ps.setString(4, d.pais);
            ps.setString(5, d.mun);
            ps.setString(6, d.uf);
            ps.setBigDecimal(7, BigDecimal.valueOf(d.kgLiquido));
            ps.setBigDecimal(8, BigDecimal.valueOf(d.vlFob));
        }
    }

    // ============================================================
    // FK AUTO-INSERT
    // ============================================================

    private static Set<String> carregarCodigosExistentes(Connection conn, String tabela, String coluna) throws Exception {
        Set<String> codigos = new HashSet<>();
        String sql = "SELECT " + coluna + " FROM " + tabela;
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                codigos.add(rs.getString(1));
            }
        }
        return codigos;
    }

    private static void inserirCodigoSh4(Connection conn, String sh4) {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT IGNORE INTO codigo_sh4 (CO_SH4, NO_SH4_POR) VALUES (?, '')")) {
            ps.setString(1, sh4);
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("[AVISO] Erro ao auto-inserir SH4 " + sh4 + ": " + e.getMessage());
        }
    }

    private static void inserirCodigoPais(Connection conn, String pais) {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT IGNORE INTO codigo_pais (CO_PAIS, NO_PAIS) VALUES (?, '')")) {
            ps.setString(1, pais);
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("[AVISO] Erro ao auto-inserir país " + pais + ": " + e.getMessage());
        }
    }

    private static String padLeft(String s, int length, char padChar) {
        while (s.length() < length) s = padChar + s;
        return s;
    }
}
