package school.sptech;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

/**
 * Lê os arquivos XLSX de importação/exportação do MDIC usando 100% Apache POI.
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
 *   XLSX (disco) → Apache POI XSSFWorkbook → Sheet/Row/Cell → JDBC batch insert
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

    /**
     * Modo DRY-RUN: apenas parseia o XLSX via Apache POI e imprime estatísticas,
     * sem conexão com banco de dados. Útil para validação/testes.
     */
    public static void dryRun(Path xlsxPath) throws Exception {
        System.out.println("\n========================================");
        System.out.println("  DRY-RUN (Apache POI XLSX) — Sem banco");
        System.out.println("  Arquivo: " + xlsxPath.getFileName());
        System.out.println("========================================\n");

        if (!Files.exists(xlsxPath)) {
            System.err.println("[ERRO] Arquivo não encontrado: " + xlsxPath);
            return;
        }

        // ► Logger de inserção/erros por arquivo
        logJava log = new logJava(xlsxPath.getFileName().toString());

        try (InputStream is = new FileInputStream(xlsxPath.toFile());
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            if (headerRow == null) {
                log.erro(0, "Arquivo sem header — impossível processar.");
                log.imprimirResumo();
                return;
            }

            // ► Detectar formato
            Formato formato = detectarFormato(headerRow);
            System.out.println("[INFO] Formato detectado: " + formato);
            System.out.println("[INFO] Headers (POI): " + headerToString(headerRow));

            int totalLinhas = 0;
            int validas = 0;
            int invalidas = 0;
            Set<String> sh4Encontrados = new HashSet<>();
            Set<String> paisesEncontrados = new HashSet<>();
            Set<String> ufsEncontradas = new HashSet<>();
            Set<String> munsEncontrados = new HashSet<>();

            long startTime = System.currentTimeMillis();

            // ► Iterar linha a linha via Apache POI
            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;
                totalLinhas++;

                try {
                    DadosLinha dados = extrairDados(row, headerRow, formato);
                    if (dados == null) {
                        invalidas++;
                        log.erro(totalLinhas, "Dados inválidos ou campos obrigatórios vazios.");
                        continue;
                    }

                    validas++;
                    sh4Encontrados.add(dados.sh4);
                    paisesEncontrados.add(dados.pais);
                    ufsEncontradas.add(dados.uf);
                    if (dados.mun != null) munsEncontrados.add(dados.mun);

                    // Imprimir amostra das primeiras 5 linhas
                    if (validas <= 5) {
                        System.out.printf("  [AMOSTRA %d] ANO=%d MES=%d NCM=%s SH4=%s PAIS=%s UF=%s MUN=%s KG=%.3f FOB=%.2f%n",
                                validas, dados.ano, dados.mes,
                                dados.ncm != null ? dados.ncm : "N/A",
                                dados.sh4, dados.pais, dados.uf,
                                dados.mun != null ? dados.mun : "N/A",
                                dados.kgLiquido, dados.vlFob);
                    }

                    if (totalLinhas % 200_000 == 0) {
                        System.out.printf("\r[DRY-RUN] Progresso: %,d linhas...", totalLinhas);
                    }

                } catch (Exception e) {
                    invalidas++;
                    log.erro(totalLinhas, e.getMessage());
                }
            }

            long elapsed = System.currentTimeMillis() - startTime;

            // ► Registrar sucesso no log
            log.sucesso(validas, invalidas);

            System.out.println("\n\n========================================");
            System.out.println("  RESULTADO DRY-RUN (Apache POI XLSX)");
            System.out.println("========================================");
            System.out.printf("  Formato detectado      : %s%n", formato);
            System.out.printf("  Total de linhas        : %,d%n", totalLinhas);
            System.out.printf("  Linhas válidas         : %,d%n", validas);
            System.out.printf("  Linhas inválidas       : %,d%n", invalidas);
            System.out.printf("  SH4 únicos             : %,d%n", sh4Encontrados.size());
            System.out.printf("  Países únicos          : %,d%n", paisesEncontrados.size());
            System.out.printf("  UFs encontradas        : %s%n", ufsEncontradas);
            if (!munsEncontrados.isEmpty()) {
                System.out.printf("  Municípios únicos      : %,d%n", munsEncontrados.size());
            }
            System.out.printf("  Tempo total            : %.1f segundos%n", elapsed / 1000.0);
            System.out.printf("  Velocidade             : %,d linhas/seg%n",
                    elapsed > 0 ? (totalLinhas * 1000L / elapsed) : 0);
            System.out.println("========================================");

            // ► Imprimir resumo do log (erros detalhados)
            log.imprimirResumo();
        }
    }

    // ============================================================
    // CARREGAMENTO COM BANCO — 100% APACHE POI (XLSX)
    // ============================================================

    private static void carregarDados(Path xlsxPath, Connection conn, String tabela, String tipo) throws Exception {
        System.out.println("\n========================================");
        System.out.printf("  CARREGANDO %s (via Apache POI XLSX)%n", tipo);
        System.out.println("  Arquivo: " + xlsxPath.getFileName());
        System.out.println("========================================\n");

        if (!Files.exists(xlsxPath)) {
            System.err.println("[ERRO] Arquivo não encontrado: " + xlsxPath);
            return;
        }

        // ► Logger de inserção/erros por arquivo
        logJava log = new logJava(xlsxPath.getFileName().toString());

        try (InputStream is = new FileInputStream(xlsxPath.toFile());
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            if (headerRow == null) {
                log.erro(0, "Arquivo sem header — impossível processar.");
                log.imprimirResumo();
                return;
            }

            // ► Detectar formato automaticamente
            Formato formato = detectarFormato(headerRow);
            System.out.println("[INFO] Formato detectado: " + formato);
            System.out.println("[INFO] Headers (POI): " + headerToString(headerRow));

            // ► Pré-carregar FKs existentes (para não duplicar INSERTs)
            Set<String> sh4Conhecidos = carregarCodigosExistentes(conn, "codigo_sh4", "CO_SH4");
            Set<String> paisConhecidos = carregarCodigosExistentes(conn, "codigo_pais", "CO_PAIS");

            System.out.printf("[INFO] FKs já existentes no banco: %d SH4, %d países%n",
                    sh4Conhecidos.size(), paisConhecidos.size());
            System.out.println("[INFO] Novos códigos serão inseridos automaticamente.");

            // ► Montar SQL adequado ao formato
            String sql = montarSql(tabela, formato);
            System.out.println("[INFO] SQL: " + sql);

            int totalLinhas = 0;
            int inseridos = 0;
            int ignorados = 0;
            int erros = 0;

            boolean autoCommitOriginal = conn.getAutoCommit();
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                long startTime = System.currentTimeMillis();

                // ► Iterar linha a linha via Apache POI
                for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                    Row row = sheet.getRow(r);
                    if (row == null) continue;
                    totalLinhas++;

                    try {
                        DadosLinha dados = extrairDados(row, headerRow, formato);
                        if (dados == null) {
                            ignorados++;
                            log.erro(totalLinhas, "Dados inválidos ou campos obrigatórios vazios.");
                            continue;
                        }

                        // Auto-inserir códigos SH4 e País que ainda não existem no banco
                        if (!sh4Conhecidos.contains(dados.sh4)) {
                            inserirCodigoSh4(conn, dados.sh4);
                            sh4Conhecidos.add(dados.sh4);
                        }
                        if (!paisConhecidos.contains(dados.pais)) {
                            inserirCodigoPais(conn, dados.pais);
                            paisConhecidos.add(dados.pais);
                        }

                        // Setar parâmetros JDBC conforme formato
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

                ps.executeBatch();
                conn.commit();

                long elapsed = System.currentTimeMillis() - startTime;

                System.out.println();
                System.out.println("\n========================================");
                System.out.printf("  RESULTADO %s (Apache POI XLSX — %s)%n", tipo, formato);
                System.out.println("========================================");
                System.out.printf("  Total de linhas lidas  : %,d%n", totalLinhas);
                System.out.printf("  Linhas inseridas       : %,d%n", inseridos);
                System.out.printf("  Linhas ignoradas (FK)  : %,d%n", ignorados);
                System.out.printf("  Erros de parsing       : %,d%n", erros);
                System.out.printf("  Tempo total            : %.1f segundos%n", elapsed / 1000.0);
                System.out.printf("  Velocidade             : %,d linhas/seg%n",
                        elapsed > 0 ? (totalLinhas * 1000L / elapsed) : 0);
                System.out.println("========================================");

                // ► Registrar sucesso no log
                log.sucesso(inseridos, ignorados);

                // ► Imprimir resumo do log (erros detalhados)
                log.imprimirResumo();

            } finally {
                conn.setAutoCommit(autoCommitOriginal);
            }
        }
    }

    // ============================================================
    // DETECÇÃO DE FORMATO
    // ============================================================

    /**
     * Detecta se o XLSX é formato NCM ou MUN baseado nas colunas do header.
     * Se existir coluna CO_NCM → formato NCM
     * Se existir coluna SH4    → formato MUN
     */
    private static Formato detectarFormato(Row headerRow) {
        if (findColumn(headerRow, "CO_NCM") != -1) {
            return Formato.NCM;
        }
        return Formato.MUN;
    }

    // ============================================================
    // EXTRAÇÃO DE DADOS (APACHE POI Cell API)
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
     * Extrai dados de uma Row do POI baseando-se no formato detectado.
     */
    private static DadosLinha extrairDados(Row row, Row headerRow, Formato formato) {
        DadosLinha d = new DadosLinha();

        // Campos comuns
        String anoStr = getCellAsString(row.getCell(findColumn(headerRow, "CO_ANO"))).trim();
        String mesStr = getCellAsString(row.getCell(findColumn(headerRow, "CO_MES"))).trim();

        if (anoStr.isEmpty() || mesStr.isEmpty()) return null;

        d.ano = (int) Double.parseDouble(anoStr);
        d.mes = (int) Double.parseDouble(mesStr);

        if (formato == Formato.NCM) {
            // ► Formato NCM: derivar SH4 dos 4 primeiros dígitos do CO_NCM
            String ncm = getCellAsString(row.getCell(findColumn(headerRow, "CO_NCM"))).trim();
            if (ncm.isEmpty() || ncm.length() < 4) return null;

            d.ncm = ncm;
            d.sh4 = ncm.substring(0, 4); // SH4 = primeiros 4 dígitos do NCM

            d.pais = getCellAsString(row.getCell(findColumn(headerRow, "CO_PAIS"))).trim();
            d.uf = getCellAsString(row.getCell(findColumn(headerRow, "SG_UF_NCM"))).trim();

            // Campos opcionais do formato NCM
            int colVia = findColumn(headerRow, "CO_VIA");
            int colUrf = findColumn(headerRow, "CO_URF");
            int colQt = findColumn(headerRow, "QT_ESTAT");

            if (colVia != -1) d.via = getCellAsString(row.getCell(colVia)).trim();
            if (colUrf != -1) d.urf = getCellAsString(row.getCell(colUrf)).trim();
            if (colQt != -1) d.qtEstat = getCellAsDouble(row.getCell(colQt));

            d.mun = null; // Não existe no formato NCM

        } else {
            // ► Formato MUN: SH4 vem direto
            d.sh4 = getCellAsString(row.getCell(findColumn(headerRow, "SH4"))).trim();
            if (d.sh4.isEmpty()) return null;

            d.pais = getCellAsString(row.getCell(findColumn(headerRow, "CO_PAIS"))).trim();
            d.uf = getCellAsString(row.getCell(findColumn(headerRow, "SG_UF_MUN"))).trim();
            d.mun = getCellAsString(row.getCell(findColumn(headerRow, "CO_MUN"))).trim();

            d.ncm = null;
        }

        // Padronizar SH4 para 4 dígitos
        d.sh4 = padLeft(d.sh4, 4, '0');

        // Padronizar CO_PAIS para até 4 caracteres
        if (d.pais.length() > 4) d.pais = d.pais.substring(0, 4);
        // Padronizar UF para 2 caracteres
        if (d.uf.length() > 2) d.uf = d.uf.substring(0, 2);

        // Validações básicas
        if (d.sh4.isEmpty() || d.pais.isEmpty()) return null;

        // KG e FOB (comuns)
        d.kgLiquido = getCellAsDouble(row.getCell(findColumn(headerRow, "KG_LIQUIDO")));
        d.vlFob = getCellAsDouble(row.getCell(findColumn(headerRow, "VL_FOB")));

        return d;
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
            ps.setString(3, d.ncm);                              // CO_NCM
            ps.setString(4, d.sh4);                               // SH4
            ps.setString(5, d.pais);                              // CO_PAIS
            ps.setString(6, d.uf);                                // SG_UF_MUN
            ps.setString(7, d.via);                               // CO_VIA
            ps.setString(8, d.urf);                               // CO_URF
            ps.setBigDecimal(9, BigDecimal.valueOf(d.qtEstat));   // QT_ESTAT
            ps.setBigDecimal(10, BigDecimal.valueOf(d.kgLiquido)); // KG_LIQUIDO
            ps.setBigDecimal(11, BigDecimal.valueOf(d.vlFob));     // VL_FOB
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
    // UTILITÁRIOS APACHE POI
    // ============================================================

    /**
     * Encontra o índice de uma coluna no header de um Sheet do POI.
     * Busca case-insensitive, aceita múltiplos nomes possíveis.
     */
    public static int findColumn(Row headerRow, String... nomes) {
        if (headerRow == null) return -1;
        for (int c = 0; c < headerRow.getLastCellNum(); c++) {
            Cell cell = headerRow.getCell(c);
            if (cell == null) continue;
            String headerName = getCellAsString(cell).toUpperCase().trim();
            for (String nome : nomes) {
                if (headerName.equals(nome.toUpperCase())) return c;
            }
        }
        return -1;
    }

    /**
     * Extrai o valor de uma célula do POI como String.
     */
    public static String getCellAsString(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                double val = cell.getNumericCellValue();
                if (val == Math.floor(val) && !Double.isInfinite(val)) {
                    yield String.valueOf((long) val);
                }
                yield String.valueOf(val);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> {
                try {
                    yield cell.getStringCellValue();
                } catch (Exception e) {
                    try {
                        yield String.valueOf(cell.getNumericCellValue());
                    } catch (Exception e2) {
                        yield "";
                    }
                }
            }
            case BLANK -> "";
            default -> "";
        };
    }

    /**
     * Extrai o valor de uma célula do POI como double.
     */
    public static double getCellAsDouble(Cell cell) {
        if (cell == null) return 0.0;
        return switch (cell.getCellType()) {
            case NUMERIC -> cell.getNumericCellValue();
            case STRING -> {
                try {
                    yield Double.parseDouble(cell.getStringCellValue().trim().replace(",", "."));
                } catch (NumberFormatException e) {
                    yield 0.0;
                }
            }
            case FORMULA -> {
                try {
                    yield cell.getNumericCellValue();
                } catch (Exception e) {
                    yield 0.0;
                }
            }
            default -> 0.0;
        };
    }

    /**
     * Converte uma Row de header para String (para logs).
     */
    public static String headerToString(Row headerRow) {
        if (headerRow == null) return "";
        StringBuilder sb = new StringBuilder("[");
        for (int c = 0; c < headerRow.getLastCellNum(); c++) {
            if (c > 0) sb.append(", ");
            sb.append(getCellAsString(headerRow.getCell(c)));
        }
        sb.append("]");
        return sb.toString();
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

    /**
     * Insere um código SH4 automaticamente quando encontrado nos dados EXP/IMP,
     * sem precisar do TABELAS_AUXILIARES.xlsx. Descrição fica vazia.
     */
    private static void inserirCodigoSh4(Connection conn, String sh4) {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT IGNORE INTO codigo_sh4 (CO_SH4, NO_SH4_POR) VALUES (?, '')")) {
            ps.setString(1, sh4);
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("[AVISO] Erro ao auto-inserir SH4 " + sh4 + ": " + e.getMessage());
        }
    }

    /**
     * Insere um código de país automaticamente quando encontrado nos dados EXP/IMP,
     * sem precisar do TABELAS_AUXILIARES.xlsx. Nome fica vazio.
     */
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
