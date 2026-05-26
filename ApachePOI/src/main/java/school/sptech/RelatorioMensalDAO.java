package school.sptech;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    private String gerarBlocoImportacao(int ano, int mes) throws SQLException {
        StringBuilder sb = new StringBuilder();

        // Totais gerais
        String sqlTotal = """
            SELECT COUNT(*) AS total, SUM(valor_fob) AS fob, SUM(kg_liquido) AS kg
            FROM base_importacao
            WHERE ano = ? AND mes = ?
            """;

        try (PreparedStatement ps = conn.prepareStatement(sqlTotal)) {
            ps.setInt(1, ano); ps.setInt(2, mes);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                sb.append(String.format(
                        "*Importações — %02d/%d*\n" +
                                "Registros: `%d` | FOB: `US$ %,.2f` | Peso: `%,.0f kg`\n\n",
                        mes, ano,
                        rs.getLong("total"),
                        rs.getDouble("fob"),
                        rs.getDouble("kg")
                ));
            }
        }

        // Top 3 setores
        String sqlSetores = """
            SELECT s.nome, SUM(b.valor_fob) AS fob
            FROM base_importacao b
            JOIN setores s ON b.setor_id = s.id
            WHERE b.ano = ? AND b.mes = ?
            GROUP BY s.nome ORDER BY fob DESC LIMIT 3
            """;

        sb.append("*Top Setores:*\n");
        try (PreparedStatement ps = conn.prepareStatement(sqlSetores)) {
            ps.setInt(1, ano); ps.setInt(2, mes);
            ResultSet rs = ps.executeQuery();
            int i = 1;
            while (rs.next()) {
                sb.append(String.format("%d. %s — `US$ %,.2f`\n",
                        i++, rs.getString("nome"), rs.getDouble("fob")));
            }
        }

        // Top 3 municípios
        String sqlMun = """
            SELECT m.nome, SUM(b.valor_fob) AS fob
            FROM base_importacao b
            JOIN codigo_municipio m ON b.co_mun = m.codigo
            WHERE b.ano = ? AND b.mes = ?
            GROUP BY m.nome ORDER BY fob DESC LIMIT 3
            """;

        sb.append("\n*Top Municípios:*\n");
        try (PreparedStatement ps = conn.prepareStatement(sqlMun)) {
            ps.setInt(1, ano); ps.setInt(2, mes);
            ResultSet rs = ps.executeQuery();
            int i = 1;
            while (rs.next()) {
                sb.append(String.format("%d. %s — `US$ %,.2f`\n",
                        i++, rs.getString("nome"), rs.getDouble("fob")));
            }
        }

        return sb.toString();
    }


    private String gerarBlocoExportacao(int ano, int mes) throws SQLException {
        StringBuilder sb = new StringBuilder();

        String sqlTotal = """
            SELECT COUNT(*) AS total, SUM(valor_fob) AS fob, SUM(kg_liquido) AS kg
            FROM base_exportacao
            WHERE ano = ? AND mes = ?
            """;

        try (PreparedStatement ps = conn.prepareStatement(sqlTotal)) {
            ps.setInt(1, ano); ps.setInt(2, mes);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                sb.append(String.format(
                        "*Exportações — %02d/%d*\n" +
                                "Registros: `%d` | FOB: `US$ %,.2f` | Peso: `%,.0f kg`\n\n",
                        mes, ano,
                        rs.getLong("total"),
                        rs.getDouble("fob"),
                        rs.getDouble("kg")
                ));
            }
        }

        // Top 3 setores
        String sqlSetores = """
            SELECT s.nome, SUM(b.valor_fob) AS fob
            FROM base_exportacao b
            JOIN setores s ON b.setor_id = s.id
            WHERE b.ano = ? AND b.mes = ?
            GROUP BY s.nome ORDER BY fob DESC LIMIT 3
            """;

        sb.append("*Top Setores:*\n");
        try (PreparedStatement ps = conn.prepareStatement(sqlSetores)) {
            ps.setInt(1, ano); ps.setInt(2, mes);
            ResultSet rs = ps.executeQuery();
            int i = 1;
            while (rs.next()) {
                sb.append(String.format("%d. %s — `US$ %,.2f`\n",
                        i++, rs.getString("nome"), rs.getDouble("fob")));
            }
        }

        // Top 3 municípios
        String sqlMun = """
            SELECT m.nome, SUM(b.valor_fob) AS fob
            FROM base_exportacao b
            JOIN codigo_municipio m ON b.co_mun = m.codigo
            WHERE b.ano = ? AND b.mes = ?
            GROUP BY m.nome ORDER BY fob DESC LIMIT 3
            """;

        sb.append("\n*Top Municípios:*\n");
        try (PreparedStatement ps = conn.prepareStatement(sqlMun)) {
            ps.setInt(1, ano); ps.setInt(2, mes);
            ResultSet rs = ps.executeQuery();
            int i = 1;
            while (rs.next()) {
                sb.append(String.format("%d. %s — `US$ %,.2f`\n",
                        i++, rs.getString("nome"), rs.getDouble("fob")));
            }
        }

        return sb.toString();
    }
}