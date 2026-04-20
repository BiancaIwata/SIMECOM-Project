package school.sptech;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

/**
 * Lê o arquivo TABELAS_AUXILIARES.xlsx (Apache POI) e insere dados nas tabelas:
 *   - codigo_sh4 (aba "NCM_SH" → extrai os 4 primeiros dígitos do NCM como SH4)
 *   - codigo_pais (aba "PAIS" ou "PAIS_BLOCOS")
 *   - codigo_municipio (aba "UF_MUN" ou "NCM_UF_MUN")
 *
 * Também popula a tabela 'setores' com agrupamentos por seção SH.
 */
public class TabelasAuxiliaresLoader {

    /**
     * Carrega TODAS as tabelas auxiliares a partir do XLSX do MDIC.
     */
    public static void carregarTudo(Path caminhoXlsx, Connection conn) throws Exception {
        System.out.println("\n========================================");
        System.out.println("  CARREGANDO TABELAS AUXILIARES (XLSX)  ");
        System.out.println("========================================\n");

        try (InputStream is = new FileInputStream(caminhoXlsx.toFile());
             Workbook workbook = new XSSFWorkbook(is)) {

            // Lista todas as abas disponíveis
            System.out.println("[INFO] Abas encontradas no XLSX:");
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                System.out.println("  - " + workbook.getSheetName(i));
            }
            System.out.println();

            // 1) Carregar setores (baseado em seções/capítulos SH)
            carregarSetores(workbook, conn);

            // 2) Carregar código SH4
            carregarCodigoSh4(workbook, conn);

            // 3) Carregar código de países
            carregarCodigoPais(workbook, conn);

            // 4) Carregar código de municípios
            carregarCodigoMunicipio(workbook, conn);
        }
    }

    // ============================================================
    // SETORES
    // ============================================================
    private static void carregarSetores(Workbook workbook, Connection conn) throws Exception {
        System.out.println("[SETORES] Inserindo setores padrão de comércio exterior...");

        // Setores baseados nas seções do Sistema Harmonizado (SH)
        String[] setores = {
                "Animais vivos e produtos do reino animal",
                "Produtos do reino vegetal",
                "Gorduras e óleos animais ou vegetais",
                "Produtos das indústrias alimentares; bebidas e tabaco",
                "Produtos minerais",
                "Produtos das indústrias químicas",
                "Plásticos e borracha",
                "Peles, couros e obras",
                "Madeira, carvão vegetal e cortiça",
                "Pasta de madeira, papel e cartão",
                "Matérias têxteis e suas obras",
                "Calçados, chapéus e semelhantes",
                "Obras de pedra, cerâmica e vidro",
                "Pérolas, pedras preciosas e metais preciosos",
                "Metais comuns e suas obras",
                "Máquinas e aparelhos, material elétrico",
                "Material de transporte",
                "Instrumentos e aparelhos de óptica, fotografia e cinematografia",
                "Armas e munições",
                "Mercadorias e produtos diversos",
                "Objetos de arte e antiguidades"
        };

        String sql = "INSERT IGNORE INTO setores (nome) VALUES (?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (String setor : setores) {
                ps.setString(1, setor);
                ps.addBatch();
            }
            int[] results = ps.executeBatch();
            int inseridos = contarInseridos(results);
            System.out.println("[SETORES] " + inseridos + " setores inseridos.\n");
        }
    }

    // ============================================================
    // CÓDIGO SH4
    // ============================================================
    private static void carregarCodigoSh4(Workbook workbook, Connection conn) throws Exception {
        // Tenta encontrar a aba de NCM/SH
        Sheet sheet = encontrarAba(workbook, "NCM_SH", "NCM", "SH");
        if (sheet == null) {
            System.err.println("[AVISO] Aba NCM_SH não encontrada. Pulando carregamento de SH4.");
            return;
        }

        System.out.println("[SH4] Lendo aba '" + sheet.getSheetName() + "'...");

        // Identificar colunas pelo header
        Row header = sheet.getRow(0);
        int colCoSh4 = findColumn(header, "CO_SH4");
        int colNoSh4Por = findColumn(header, "NO_SH4_POR");

        if (colCoSh4 == -1 || colNoSh4Por == -1) {
            System.err.println("[AVISO] Colunas CO_SH4/NO_SH4_POR não encontradas no header.");
            System.err.println("        Headers encontrados: " + headerToString(header));
            return;
        }

        String sql = "INSERT IGNORE INTO codigo_sh4 (CO_SH4, NO_SH4_POR) VALUES (?, ?)";
        Set<String> sh4Unicos = new HashSet<>();
        int inseridos = 0;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;

                String coSh4 = getCellAsString(row.getCell(colCoSh4)).trim();
                String noSh4 = getCellAsString(row.getCell(colNoSh4Por)).trim();

                // Garantir 4 dígitos
                if (coSh4.isEmpty() || coSh4.length() > 4) continue;
                coSh4 = String.format("%4s", coSh4).replace(' ', '0');

                // Evitar duplicatas (o XLSX pode ter linhas repetidas por NCM)
                if (!sh4Unicos.add(coSh4)) continue;

                if (noSh4.length() > 80) noSh4 = noSh4.substring(0, 80);

                ps.setString(1, coSh4);
                ps.setString(2, noSh4);
                ps.addBatch();

                if (sh4Unicos.size() % 500 == 0) {
                    ps.executeBatch();
                }
            }
            int[] results = ps.executeBatch();
            inseridos = sh4Unicos.size();
        }

        System.out.println("[SH4] " + inseridos + " códigos SH4 únicos processados.\n");
    }

    // ============================================================
    // CÓDIGO DE PAÍSES
    // ============================================================
    private static void carregarCodigoPais(Workbook workbook, Connection conn) throws Exception {
        Sheet sheet = encontrarAba(workbook, "PAIS", "PAIS_BLOCOS");
        if (sheet == null) {
            System.err.println("[AVISO] Aba PAIS não encontrada. Pulando.");
            return;
        }

        System.out.println("[PAIS] Lendo aba '" + sheet.getSheetName() + "'...");

        Row header = sheet.getRow(0);
        int colCoPais = findColumn(header, "CO_PAIS");
        int colNoPais = findColumn(header, "NO_PAIS");

        if (colCoPais == -1 || colNoPais == -1) {
            System.err.println("[AVISO] Colunas CO_PAIS/NO_PAIS não encontradas.");
            System.err.println("        Headers: " + headerToString(header));
            return;
        }

        String sql = "INSERT IGNORE INTO codigo_pais (CO_PAIS, NO_PAIS) VALUES (?, ?)";
        Set<String> paisesUnicos = new HashSet<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;

                String coPais = getCellAsString(row.getCell(colCoPais)).trim();
                String noPais = getCellAsString(row.getCell(colNoPais)).trim();

                if (coPais.isEmpty()) continue;
                // Padronizar para 4 caracteres
                if (coPais.length() > 4) coPais = coPais.substring(0, 4);

                if (!paisesUnicos.add(coPais)) continue;

                if (noPais.length() > 45) noPais = noPais.substring(0, 45);

                ps.setString(1, coPais);
                ps.setString(2, noPais);
                ps.addBatch();

                if (paisesUnicos.size() % 100 == 0) {
                    ps.executeBatch();
                }
            }
            ps.executeBatch();
        }

        System.out.println("[PAIS] " + paisesUnicos.size() + " países únicos processados.\n");
    }

    // ============================================================
    // CÓDIGO DE MUNICÍPIOS
    // ============================================================
    private static void carregarCodigoMunicipio(Workbook workbook, Connection conn) throws Exception {
        Sheet sheet = encontrarAba(workbook, "UF_MUN", "MUN", "MUNICIPIO");
        if (sheet == null) {
            System.err.println("[AVISO] Aba UF_MUN/MUNICIPIO não encontrada. Pulando.");
            return;
        }

        System.out.println("[MUN] Lendo aba '" + sheet.getSheetName() + "'...");

        Row header = sheet.getRow(0);
        int colCoMun = findColumn(header, "CO_MUN_GEO", "CO_MUN");
        int colNoMun = findColumn(header, "NO_MUN_MIN", "NO_MUN");

        if (colCoMun == -1 || colNoMun == -1) {
            System.err.println("[AVISO] Colunas de município não encontradas.");
            System.err.println("        Headers: " + headerToString(header));
            return;
        }

        String sql = "INSERT IGNORE INTO codigo_municipio (CO_MUN_GEO, NO_MUN) VALUES (?, ?)";
        Set<String> munUnicos = new HashSet<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;

                String coMun = getCellAsString(row.getCell(colCoMun)).trim();
                String noMun = getCellAsString(row.getCell(colNoMun)).trim();

                if (coMun.isEmpty()) continue;
                // Remover ".0" de números formatados como decimal
                if (coMun.contains(".")) coMun = coMun.substring(0, coMun.indexOf('.'));
                if (coMun.length() > 10) coMun = coMun.substring(0, 10);

                if (!munUnicos.add(coMun)) continue;

                if (noMun.length() > 35) noMun = noMun.substring(0, 35);

                ps.setString(1, coMun);
                ps.setString(2, noMun);
                ps.addBatch();

                if (munUnicos.size() % 1000 == 0) {
                    ps.executeBatch();
                }
            }
            ps.executeBatch();
        }

        System.out.println("[MUN] " + munUnicos.size() + " municípios únicos processados.\n");
    }

    // ============================================================
    // UTILITÁRIOS
    // ============================================================

    /**
     * Tenta encontrar uma aba pelo nome (case-insensitive, aceita múltiplos nomes).
     */
    private static Sheet encontrarAba(Workbook workbook, String... nomes) {
        for (String nome : nomes) {
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                if (workbook.getSheetName(i).toUpperCase().contains(nome.toUpperCase())) {
                    return workbook.getSheetAt(i);
                }
            }
        }
        return null;
    }

    private static int contarInseridos(int[] results) {
        int count = 0;
        for (int r : results) {
            if (r > 0 || r == PreparedStatement.SUCCESS_NO_INFO) count++;
        }
        return count;
    }

    // ============================================================
    // UTILITÁRIOS APACHE POI
    // ============================================================

    /**
     * Encontra o índice de uma coluna no header de um Sheet do POI.
     * Busca case-insensitive, aceita múltiplos nomes possíveis.
     */
    private static int findColumn(Row headerRow, String... nomes) {
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
    private static String getCellAsString(Cell cell) {
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
     * Converte uma Row de header para String (para logs).
     */
    private static String headerToString(Row headerRow) {
        if (headerRow == null) return "";
        StringBuilder sb = new StringBuilder("[");
        for (int c = 0; c < headerRow.getLastCellNum(); c++) {
            if (c > 0) sb.append(", ");
            sb.append(getCellAsString(headerRow.getCell(c)));
        }
        sb.append("]");
        return sb.toString();
    }
}
