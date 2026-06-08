package school.sptech;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.PreparedStatement;

public class TabelasAuxiliaresProcessor extends DataProcessor {

    public TabelasAuxiliaresProcessor(ProcessorContext context) {
        super(context, "TABELAS AUXILIARES");
    }

    @Override
    protected ProcessingResult processarInterno(Path arquivoPath) throws Exception {
        int inseridos = 0;

        try (
                InputStream is = Files.newInputStream(arquivoPath);
                Workbook workbook = new XSSFWorkbook(is)
        ) {
            Sheet sheet = workbook.getSheetAt(0);
            Row header = sheet.getRow(0);

            DataFormatter formatter = new DataFormatter();

            boolean ehMunicipio = false;
            boolean ehPais = false;

            for (Cell cell : header) {
                String nomeColuna = formatter.formatCellValue(cell).trim();

                if (
                        nomeColuna.equalsIgnoreCase("CO_MUN_GEO") ||
                                nomeColuna.equalsIgnoreCase("CO_MUN") ||
                                nomeColuna.equalsIgnoreCase("Código do Município")
                ) {
                    ehMunicipio = true;
                    break;
                } else if (nomeColuna.equalsIgnoreCase("CO_PAIS")) {
                    ehPais = true;
                    break;
                }
            }

            if (ehPais) {
                inseridos = processarPaises(sheet, formatter);
            } else if (ehMunicipio) {
                inseridos = processarMunicipios(sheet, formatter);
            } else {
                inseridos = processarSh4(sheet, formatter);
            }
        }

        return ProcessingResult.sucesso(inseridos);
    }

    private int processarPaises(Sheet sheet, DataFormatter formatter) throws Exception {
        int inseridos = 0;

        String sql = """
            INSERT INTO codigo_pais (CO_PAIS, NO_PAIS)
            VALUES (?, ?)
            ON DUPLICATE KEY UPDATE
                NO_PAIS = VALUES(NO_PAIS)
        """;

        Row header = sheet.getRow(0);

        int colCoPais = -1;
        int colNoPais = -1;

        for (Cell cell : header) {
            String nomeColuna = formatter.formatCellValue(cell).trim();

            if (nomeColuna.equalsIgnoreCase("CO_PAIS")) {
                colCoPais = cell.getColumnIndex();
            } else if (nomeColuna.equalsIgnoreCase("NO_PAIS")) {
                colNoPais = cell.getColumnIndex();
            }
        }

        if (colCoPais == -1 || colNoPais == -1) {
            throw new RuntimeException("Colunas essenciais (CO_PAIS ou NO_PAIS) não encontradas no arquivo auxiliar.");
        }

        try (PreparedStatement ps = context.getConnection().prepareStatement(sql)) {
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String coPais = formatter.formatCellValue(row.getCell(colCoPais)).trim();
                String noPais = formatter.formatCellValue(row.getCell(colNoPais)).trim();

                if (coPais.isEmpty() || noPais.isEmpty()) continue;

                coPais = coPais.replaceAll("\\D", "");

                ps.setString(1, coPais);
                ps.setString(2, noPais);
                ps.addBatch();

                inseridos++;

                if (inseridos % 1000 == 0) {
                    ps.executeBatch();
                }
            }

            ps.executeBatch();
        }

        System.out.println("[AUX] Países inseridos/atualizados na EC2: " + inseridos);

        return inseridos;
    }

    private int processarMunicipios(Sheet sheet, DataFormatter formatter) throws Exception {
        int inseridos = 0;

        String sql = """
        INSERT INTO codigo_municipio (CO_MUN_GEO, NO_MUN)
        VALUES (?, ?)
        ON DUPLICATE KEY UPDATE
            NO_MUN = VALUES(NO_MUN)
    """;

        Row header = sheet.getRow(0);

        int colCodigo = -1;
        int colNome = -1;

        for (Cell cell : header) {
            String nomeColuna = formatter.formatCellValue(cell).trim();

            if (
                    nomeColuna.equalsIgnoreCase("CO_MUN_GEO") ||
                            nomeColuna.equalsIgnoreCase("CO_MUN") ||
                            nomeColuna.equalsIgnoreCase("Código do Município")
            ) {
                colCodigo = cell.getColumnIndex();
            }

            if (
                    nomeColuna.equalsIgnoreCase("NO_MUN") ||
                            nomeColuna.equalsIgnoreCase("NO_MUN_MIN") ||
                            nomeColuna.equalsIgnoreCase("Nome do Município") ||
                            nomeColuna.equalsIgnoreCase("Município")
            ) {
                colNome = cell.getColumnIndex();
            }
        }

        if (colCodigo == -1 || colNome == -1) {
            throw new RuntimeException("Colunas de município não encontradas no arquivo auxiliar.");
        }

        try (PreparedStatement ps = context.getConnection().prepareStatement(sql)) {
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String codigo = formatter.formatCellValue(row.getCell(colCodigo)).trim();
                String nome = formatter.formatCellValue(row.getCell(colNome)).trim();

                if (codigo.isEmpty() || nome.isEmpty()) continue;

                codigo = codigo.replaceAll("\\D", "");

                ps.setString(1, codigo);
                ps.setString(2, nome);
                ps.addBatch();

                inseridos++;

                if (inseridos % 1000 == 0) {
                    ps.executeBatch();
                }
            }

            ps.executeBatch();
        }

        System.out.println("[AUX] Municípios inseridos/atualizados: " + inseridos);

        return inseridos;
    }

    private int processarSh4(Sheet sheet, DataFormatter formatter) throws Exception {
        int inseridos = 0;

        String sql = """
        INSERT INTO codigo_sh4 (CO_SH4, NO_SH4_POR)
        VALUES (?, ?)
        ON DUPLICATE KEY UPDATE
            NO_SH4_POR = VALUES(NO_SH4_POR)
    """;

        Row header = sheet.getRow(0);

        int colSh4 = -1;
        int colDesc = -1;

        for (Cell cell : header) {
            String nomeColuna = formatter.formatCellValue(cell).trim();

            if (nomeColuna.equalsIgnoreCase("Posição (SH4) - Código")) {
                colSh4 = cell.getColumnIndex();
            }

            if (nomeColuna.equalsIgnoreCase("Posição (SH4) - Descrição")) {
                colDesc = cell.getColumnIndex();
            }
        }

        if (colSh4 == -1 || colDesc == -1) {
            throw new RuntimeException("Colunas SH4 não encontradas no arquivo auxiliar.");
        }

        try (PreparedStatement ps = context.getConnection().prepareStatement(sql)) {
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String sh4 = formatter.formatCellValue(row.getCell(colSh4)).trim();
                String descricao = formatter.formatCellValue(row.getCell(colDesc)).trim();

                if (sh4.isEmpty() || descricao.isEmpty()) continue;

                sh4 = sh4.replaceAll("\\D", "");

                while (sh4.length() < 4) {
                    sh4 = "0" + sh4;
                }

                if (sh4.length() > 4) {
                    sh4 = sh4.substring(0, 4);
                }

                ps.setString(1, sh4);
                ps.setString(2, descricao);
                ps.addBatch();

                inseridos++;

                if (inseridos % 1000 == 0) {
                    ps.executeBatch();
                }
            }

            ps.executeBatch();
        }

        System.out.println("[AUX] SH4 inseridos/atualizados: " + inseridos);

        return inseridos;
    }
}