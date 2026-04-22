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
 * no modo SAX streaming (XSSF Event API).
 *
 * Também trata planilhas em que cada linha veio inteira em uma única célula,
 * com campos separados por ';' (caso observado no arquivo EXP_2017.xlsx).
 */
public class ComexDataLoader {

    private static final int BATCH_SIZE = 5000;

    /** Formato detectado do XLSX */
    private enum Formato { NCM, MUN }

    /** Resultado consolidado da carga para uso pelo chamador */
    public record CargaResultado(
            String arquivo,
            String tipo,
            String formato,
            int totalLinhas,
            int inseridos,
            int ignorados,
            int erros
    ) {}

    public static CargaResultado carregarExportacao(Path xlsxPath, Connection conn, logJava log) throws Exception {
        return carregarDados(xlsxPath, conn, "base_exportacao", "EXPORTAÇÃO", log);
    }

    public static CargaResultado carregarImportacao(Path xlsxPath, Connection conn, logJava log) throws Exception {
        return carregarDados(xlsxPath, conn, "base_importacao", "IMPORTAÇÃO", log);
    }

    // Compatibilidade com chamadas antigas
    public static CargaResultado carregarExportacao(Path xlsxPath, Connection conn) throws Exception {
        return carregarExportacao(xlsxPath, conn, new logJava(xlsxPath.getFileName().toString()));
    }

    public static CargaResultado carregarImportacao(Path xlsxPath, Connection conn) throws Exception {
        return carregarImportacao(xlsxPath, conn, new logJava(xlsxPath.getFileName().toString()));
    }

    private static CargaResultado carregarDados(Path xlsxPath, Connection conn, String tabela, String tipo, logJava log) throws Exception {
        System.out.println("\n========================================");
        System.out.printf("  CARREGANDO %s (SAX Streaming XLSX)%n", tipo);
        System.out.println("  Arquivo: " + xlsxPath.getFileName());
        System.out.println("========================================\n");

        if (!Files.exists(xlsxPath)) {
            throw new IllegalArgumentException("Arquivo não encontrado: " + xlsxPath);
        }

        Set<String> sh4Conhecidos = carregarCodigosExistentes(conn, "codigo_sh4", "CO_SH4");
        Set<String> paisConhecidos = carregarCodigosExistentes(conn, "codigo_pais", "CO_PAIS");
        System.out.printf("[INFO] FKs já existentes no banco: %d SH4, %d países%n",
                sh4Conhecidos.size(), paisConhecidos.size());
        System.out.println("[INFO] Novos códigos serão inseridos automaticamente.");

        try (OPCPackage pkg = OPCPackage.open(xlsxPath.toFile())) {
            ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(pkg);
            XSSFReader xssfReader = new XSSFReader(pkg);
            StylesTable styles = xssfReader.getStylesTable();

            Iterator<InputStream> sheetsData = xssfReader.getSheetsData();
            if (!sheetsData.hasNext()) {
                throw new IllegalStateException("XLSX sem planilhas.");
            }

            try (InputStream sheetStream = sheetsData.next()) {
                RowInsertHandler handler = new RowInsertHandler(
                        conn, tabela, tipo, log, sh4Conhecidos, paisConhecidos);

                SAXParserFactory spf = SAXParserFactory.newInstance();
                spf.setNamespaceAware(true);

                XMLReader xmlReader = spf.newSAXParser().getXMLReader();
                xmlReader.setContentHandler(
                        new XSSFSheetXMLHandler(styles, strings, handler, false));

                System.out.println("[INFO] Iniciando leitura SAX streaming...");
                long startTime = System.currentTimeMillis();

                xmlReader.parse(new InputSource(sheetStream));
                System.out.println("[INFO] Parsing SAX concluído com sucesso.");

                handler.flushBatch();

                long elapsed = System.currentTimeMillis() - startTime;
                String formatoDesc = handler.getFormato() != null ? handler.getFormato().name() : "DESCONHECIDO";

                System.out.println();
                System.out.println("\n========================================");
                System.out.printf("  RESULTADO %s (SAX Streaming — %s)%n", tipo, formatoDesc);
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

                return new CargaResultado(
                        xlsxPath.getFileName().toString(),
                        tipo,
                        formatoDesc,
                        handler.getTotalLinhas(),
                        handler.getInseridos(),
                        handler.getIgnorados(),
                        handler.getErros()
                );
            }
        }
    }

    private static class RowInsertHandler implements XSSFSheetXMLHandler.SheetContentsHandler {

        private final Connection conn;
        private final String tabela;
        private final String tipo;
        private final logJava log;
        private final Set<String> sh4Conhecidos;
        private final Set<String> paisConhecidos;

        private final Map<Integer, String> headerMap = new LinkedHashMap<>();
        private final Map<Integer, String> currentRow = new HashMap<>();

        private Formato formato;
        private PreparedStatement ps;
        private boolean autoCommitOriginal;

        private int totalLinhas = 0;
        private int inseridos = 0;
        private int ignorados = 0;
        private int erros = 0;

        RowInsertHandler(Connection conn, String tabela, String tipo, logJava log,
                         Set<String> sh4Conhecidos, Set<String> paisConhecidos) {
            this.conn = conn;
            this.tabela = tabela;
            this.tipo = tipo;
            this.log = log;
            this.sh4Conhecidos = sh4Conhecidos;
            this.paisConhecidos = paisConhecidos;
        }

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
                parseHeader();
                return;
            }

            if (formato == null) {
                ignorados++;
                return;
            }

            totalLinhas++;
            try {
                Map<Integer, String> rowNormalizada = normalizarLinhaSeNecessario(currentRow);
                DadosLinha dados = extrairDadosStreaming(rowNormalizada, headerMap, formato);
                if (dados == null) {
                    ignorados++;
                    log.erro(totalLinhas, "Dados inválidos ou campos obrigatórios vazios.");
                    return;
                }

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
                ignorados++;
                log.erro(totalLinhas, "Erro de formatação numérica: " + e.getMessage());
            } catch (Exception e) {
                erros++;
                ignorados++;
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
            // Ignorar headers/footers
        }

        private void parseHeader() {
            if (currentRow.isEmpty()) {
                System.err.println("[ERRO] Header vazio! A primeira linha do XLSX não tem dados.");
                return;
            }

            Map<Integer, String> linhaHeader = normalizarLinhaSeNecessario(currentRow);
            for (Map.Entry<Integer, String> e : linhaHeader.entrySet()) {
                headerMap.put(e.getKey(), limparCampo(e.getValue()).toUpperCase());
            }

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

            String sql = montarSql(tabela, formato);
            System.out.println("[INFO] SQL: " + sql);

            try {
                autoCommitOriginal = conn.getAutoCommit();
                conn.setAutoCommit(false);
                ps = conn.prepareStatement(sql);
            } catch (Exception ex) {
                throw new RuntimeException("Falha ao preparar SQL: " + ex.getMessage(), ex);
            }
        }

        void flushBatch() {
            if (ps == null) return;
            try {
                ps.executeBatch();
                conn.commit();
            } catch (Exception e) {
                System.err.println("[ERRO] Falha no flush final: " + e.getMessage());
            } finally {
                try {
                    conn.setAutoCommit(autoCommitOriginal);
                } catch (Exception ignored) {
                }
                try {
                    ps.close();
                } catch (Exception ignored) {
                }
            }
        }

        /**
         * Se a linha veio toda em uma única célula com separador ';',
         * expande para múltiplas colunas.
         */
        private Map<Integer, String> normalizarLinhaSeNecessario(Map<Integer, String> row) {
            if (row.size() != 1) {
                return new LinkedHashMap<>(row);
            }

            String unicoValor = row.values().iterator().next();
            if (unicoValor == null) {
                return new LinkedHashMap<>(row);
            }

            String texto = unicoValor.trim();
            if (!texto.contains(";")) {
                return new LinkedHashMap<>(row);
            }

            String[] partes = texto.split(";", -1);
            Map<Integer, String> expandida = new LinkedHashMap<>();
            for (int i = 0; i < partes.length; i++) {
                expandida.put(i, limparCampo(partes[i]));
            }

            return expandida;
        }
    }

    private static class DadosLinha {
        int ano, mes;
        String ncm;
        String sh4;
        String pais;
        String uf;
        String mun;
        String via;
        String urf;
        double qtEstat;
        double kgLiquido;
        double vlFob;
    }

    private static DadosLinha extrairDadosStreaming(
            Map<Integer, String> row, Map<Integer, String> headerMap, Formato formato) {

        DadosLinha d = new DadosLinha();

        String anoStr = getVal(row, headerMap, "CO_ANO");
        String mesStr = getVal(row, headerMap, "CO_MES");

        if (anoStr.isEmpty() || mesStr.isEmpty()) return null;

        d.ano = parseInteiroSeguro(anoStr);
        d.mes = parseInteiroSeguro(mesStr);

        if (formato == Formato.NCM) {
            String ncm = limparCodigoNumerico(getVal(row, headerMap, "CO_NCM"));
            if (ncm.isEmpty() || ncm.length() < 4) return null;

            d.ncm = ncm;
            d.sh4 = ncm.substring(0, 4);

            d.pais = limparCodigoNumerico(getVal(row, headerMap, "CO_PAIS"));
            d.uf   = getVal(row, headerMap, "SG_UF_NCM");

            d.via     = limparCodigoNumerico(getVal(row, headerMap, "CO_VIA"));
            d.urf     = limparCodigoNumerico(getVal(row, headerMap, "CO_URF"));
            d.qtEstat = parseDouble(getVal(row, headerMap, "QT_ESTAT"));
            d.mun     = null;
        } else {
            d.sh4  = limparCodigoNumerico(getVal(row, headerMap, "SH4"));
            if (d.sh4.isEmpty()) return null;

            d.pais = limparCodigoNumerico(getVal(row, headerMap, "CO_PAIS"));
            d.uf   = getVal(row, headerMap, "SG_UF_MUN");
            d.mun  = limparCodigoNumerico(getVal(row, headerMap, "CO_MUN"));
            d.ncm  = null;
        }

        d.sh4 = padLeft(d.sh4, 4, '0');

        if (d.pais.length() > 4) d.pais = d.pais.substring(0, 4);
        if (d.uf.length() > 2) d.uf = d.uf.substring(0, 2);

        if (d.sh4.isEmpty() || d.pais.isEmpty()) return null;

        d.kgLiquido = parseDouble(getVal(row, headerMap, "KG_LIQUIDO"));
        d.vlFob     = parseDouble(getVal(row, headerMap, "VL_FOB"));

        return d;
    }

    private static String getVal(Map<Integer, String> row, Map<Integer, String> headerMap, String headerName) {
        for (Map.Entry<Integer, String> e : headerMap.entrySet()) {
            if (e.getValue().equals(headerName)) {
                String v = row.get(e.getKey());
                return limparCampo(v != null ? v : "");
            }
        }
        return "";
    }

    private static int parseInteiroSeguro(String s) {
        return (int) Math.round(Double.parseDouble(normalizarNumero(s)));
    }

    private static double parseDouble(String s) {
        if (s == null || s.isBlank()) return 0.0;
        return Double.parseDouble(normalizarNumero(s));
    }

    private static String normalizarNumero(String s) {
        String v = limparCampo(s);
        if (v.isEmpty()) return "0";

        if (v.contains(",") && v.contains(".")) {
            v = v.replace(".", "").replace(",", ".");
        } else if (v.contains(",")) {
            v = v.replace(",", ".");
        }

        return v;
    }

    private static String limparCampo(String s) {
        if (s == null) return "";
        String v = s.trim();
        if (v.startsWith("\"") && v.endsWith("\"") && v.length() >= 2) {
            v = v.substring(1, v.length() - 1);
        }
        return v.trim();
    }

    private static String limparCodigoNumerico(String s) {
        String v = limparCampo(s);
        if (v.endsWith(".0")) {
            v = v.substring(0, v.length() - 2);
        }
        return v;
    }

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
        String valor = s == null ? "" : s;
        while (valor.length() < length) valor = padChar + valor;
        return valor;
    }
}