package school.sptech;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class TabelasAuxiliaresProcessor extends DataProcessor {

    public TabelasAuxiliaresProcessor(ProcessorContext context) {
        super(context, "TABELAS AUXILIARES");
    }

    @Override
    protected ProcessingResult processarInterno(Path arquivoPath) throws Exception {
        int inseridos = 0;

        String sql = """
            INSERT INTO codigo_sh4 (CO_SH4, NO_SH4_POR)
            VALUES (?, ?)
            ON DUPLICATE KEY UPDATE
                NO_SH4_POR = VALUES(NO_SH4_POR)
        """;

        try (
                InputStream is = Files.newInputStream(arquivoPath);
                Workbook workbook = new XSSFWorkbook(is);
                PreparedStatement ps = context.getConnection().prepareStatement(sql)
        ) {
            Sheet sheet = workbook.getSheetAt(0);

            Row header = sheet.getRow(0);

            int colSh4 = -1;
            int colDesc = -1;

            for (Cell cell : header) {
                String nomeColuna = cell.getStringCellValue().trim();

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

            DataFormatter formatter = new DataFormatter();

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

        return ProcessingResult.sucesso(inseridos);
    }
}
