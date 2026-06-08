package school.sptech;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class RelatorioMensalDAO {

    private final Connection conn;

    public RelatorioMensalDAO(Connection conn) {
        this.conn = conn;
    }

    public String gerarResumoMensal(int ano, int mes) throws SQLException {
        StringBuilder sb = new StringBuilder();

        sb.append(gerarBlocoImportacao(ano, mes));
        sb.append("\n─────────────────────────\n\n");
        sb.append(gerarBlocoExportacao(ano, mes));
        sb.append("\n─────────────────────────\n\n");

        return sb.toString();
    }

    public int[] buscarAnoMesMaisRecente() throws SQLException {
        // CO_ANO agora é SMALLINT UNSIGNED — sem necessidade de CAST
        String sql = """
                SELECT CO_ANO AS max_ano, CO_MES AS max_mes
                FROM base_importacao
                ORDER BY CO_ANO DESC, CO_MES DESC
                LIMIT 1
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new int[]{ rs.getInt("max_ano"), rs.getInt("max_mes") };
            }
        }

        java.time.LocalDate mesPassado = java.time.LocalDate.now().minusMonths(1);
        return new int[]{ mesPassado.getYear(), mesPassado.getMonthValue() };
    }

    // ─────────────────────────────────────────
    // IMPORTAÇÃO
    // ─────────────────────────────────────────
    private String gerarBlocoImportacao(int ano, int mes) throws SQLException {
        StringBuilder sb = new StringBuilder();

        // Totais gerais — CO_ANO é SMALLINT UNSIGNED, comparação direta com int funciona
        String sqlTotal = """
                SELECT COUNT(*) AS total,
                       SUM(VL_FOB) AS fob,
                       SUM(KG_LIQUIDO) AS kg
                FROM base_importacao
                WHERE CO_ANO = ? AND CO_MES = ?
                """;

        try (PreparedStatement ps = conn.prepareStatement(sqlTotal)) {
            ps.setInt(1, ano);
            ps.setInt(2, mes);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                sb.append(String.format(
                        "*Importações — %02d/%d*\n" +
                        "Registros: `%d` | FOB: `US$ %,.2f` | Peso: `%,.0f kg`\n\n",
                        mes, ano,
                        rs.getLong("total"),
                        rs.getDouble("fob"),
                        rs.getDouble("kg")));
            }
        }

        // Top 3 setores — JOIN com setores pelo SETORES_ID
        String sqlSetores = """
                SELECT s.nome,
                SUM(b.VL_FOB) AS fob
                FROM base_importacao b
                JOIN codigo_sh4 sh ON sh.CO_SH4 = b.SH4
                JOIN setores s ON s.id = sh.fk_setor
                WHERE b.CO_ANO = ? AND b.CO_MES = ?
                GROUP BY s.id, s.nome
                ORDER BY fob DESC
                LIMIT 3
                """;

        sb.append("*Top Setores:*\n");
        try (PreparedStatement ps = conn.prepareStatement(sqlSetores)) {
            ps.setInt(1, ano);
            ps.setInt(2, mes);
            ResultSet rs = ps.executeQuery();
            int i = 1;
            while (rs.next()) {
                sb.append(String.format("%d. %s — `US$ %,.2f`\n",
                        i++, rs.getString("nome"), rs.getDouble("fob")));
            }
        }

        // Top 3 municípios — JOIN com codigo_municipio pelo CO_MUN_GEO
        String sqlMun = """
                SELECT m.NO_MUN AS nome,
                       SUM(b.VL_FOB) AS fob
                FROM base_importacao b
                JOIN codigo_municipio m ON m.CO_MUN_GEO = b.CO_MUN
                WHERE b.CO_ANO = ? AND b.CO_MES = ?
                GROUP BY m.CO_MUN_GEO, m.NO_MUN
                ORDER BY fob DESC
                LIMIT 3
                """;

        sb.append("\n*Top Municípios:*\n");
        try (PreparedStatement ps = conn.prepareStatement(sqlMun)) {
            ps.setInt(1, ano);
            ps.setInt(2, mes);
            ResultSet rs = ps.executeQuery();
            int i = 1;
            while (rs.next()) {
                sb.append(String.format("%d. %s — `US$ %,.2f`\n",
                        i++, rs.getString("nome"), rs.getDouble("fob")));
            }
        }
      
        return sb.toString();
    }

    // ─────────────────────────────────────────
    // EXPORTAÇÃO
    // ─────────────────────────────────────────
    private String gerarBlocoExportacao(int ano, int mes) throws SQLException {
        StringBuilder sb = new StringBuilder();

        // Totais gerais
        String sqlTotal = """ 
        SELECT COUNT(*) AS total,
                SUM(VL_FOB) AS fob,
                SUM(KG_LIQUIDO) AS kg
        FROM base_exportacao
        WHERE CO_ANO = ? AND CO_MES = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sqlTotal)) {
            ps.setInt(1, ano);
            ps.setInt(2, mes);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                sb.append(String.format(
                        "*Exportações — %02d/%d*\n" +
                        "Registros: `%d` | FOB: `US$ %,.2f` | Peso: `%,.0f kg`\n\n",
                        mes, ano,
                        rs.getLong("total"),
                        rs.getDouble("fob"),
                        rs.getDouble("kg")));
            }
        }

        // Top 3 setores
        String sqlSetores = """
                SELECT s.nome,
                       SUM(b.VL_FOB) AS fob
                FROM base_exportacao b
                JOIN codigo_sh4 sh ON sh.CO_SH4 = b.SH4
                JOIN setores s ON s.id = sh.fk_setor
                WHERE b.CO_ANO = ? AND b.CO_MES = ?
                GROUP BY s.id, s.nome
                ORDER BY fob DESC
                LIMIT 3
                """;

        sb.append("*Top Setores:*\n");
        try (PreparedStatement ps = conn.prepareStatement(sqlSetores)) {
            ps.setInt(1, ano);
            ps.setInt(2, mes);
            ResultSet rs = ps.executeQuery();
            int i = 1;
            while (rs.next()) {
                sb.append(String.format("%d. %s — `US$ %,.2f`\n",
                        i++, rs.getString("nome"), rs.getDouble("fob")));
            }
        }

        // Top 3 municípios
        String sqlMun = """
                SELECT m.NO_MUN AS nome,
                       SUM(b.VL_FOB) AS fob
                FROM base_exportacao b
                JOIN codigo_municipio m ON m.CO_MUN_GEO = b.CO_MUN
                WHERE b.CO_ANO = ? AND b.CO_MES = ?
                GROUP BY m.CO_MUN_GEO, m.NO_MUN
                ORDER BY fob DESC
                LIMIT 3
                """;

        sb.append("\n*Top Municípios:*\n");
        try (PreparedStatement ps = conn.prepareStatement(sqlMun)) {
            ps.setInt(1, ano);
            ps.setInt(2, mes);
            ResultSet rs = ps.executeQuery();
            int i = 1;
            while (rs.next()) {
                sb.append(String.format("%d. %s — `US$ %,.2f`\n",
                        i++, rs.getString("nome"), rs.getDouble("fob")));
            }
        }

        return sb.toString();
    }

    public List<String> buscarDestinatariosSlack() throws SQLException {
        List<String> listaIds = new java.util.ArrayList<>();

        String sql = "SELECT slack_user_id FROM usuarios WHERE slack_user_id IS NOT NULL";

        try (PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                listaIds.add(rs.getString("slack_user_id"));
            }
        }
        return listaIds;
    }

    public int[] buscarAnoMesMaisRecente() throws SQLException {
        String sql = """
                SELECT CAST(CO_ANO AS UNSIGNED) AS max_ano, CO_MES AS max_mes
                FROM base_importacao
                ORDER BY max_ano DESC, max_mes DESC
                LIMIT 1
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                // Retorna um array onde o índice 0 é o ano e o 1 é o mês
                return new int[]{ rs.getInt("max_ano"), rs.getInt("max_mes") };
            }
        }
        
        java.time.LocalDate mesPassado = java.time.LocalDate.now().minusMonths(1);
        return new int[]{ mesPassado.getYear(), mesPassado.getMonthValue() };
    }
}