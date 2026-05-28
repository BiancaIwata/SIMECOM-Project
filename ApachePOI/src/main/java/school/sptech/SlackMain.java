package school.sptech;

import java.sql.Connection;
import java.time.LocalDate;

public class SlackMain {
    public static void main(String[] args) {

        System.out.println("=== INICIANDO TESTE COMPLETO ===\n");

        // 1. Testa conexão com o banco
        System.out.println(">> Conectando ao banco...");
        Connection conn;
        try {
            conn = DataSource.getConnection();
            System.out.println("Banco conectado!\n");
        } catch (Exception e) {
            System.err.println("Falha ao conectar no banco: " + e.getMessage());
            return;
        }

        // 2. Gera o relatório
        System.out.println(">> Gerando relatório mensal...");
        String resumo;
        try {
            RelatorioMensalDAO dao = new RelatorioMensalDAO(conn);

            // Troque para o mês/ano que tem dados no seu banco
            int ano = 2024;
            int mes = 4;

            resumo = dao.gerarResumoMensal(ano, mes);
            System.out.println("Relatório gerado:\n");
            System.out.println("─────────────────────────────");
            System.out.println(resumo);
            System.out.println("─────────────────────────────\n");

        } catch (Exception e) {
            System.err.println("Falha ao gerar relatório: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        // 3. Envia para o Slack
        System.out.println(">> Enviando para o Slack...");
        try {
            LocalDate mesPassado = LocalDate.now().minusMonths(1);
            String mensagemFinal = String.format(
                    ":bar_chart: *Atualização Mensal SIMECOM — %02d/%d*\n\n%s",
                    mesPassado.getMonthValue(), mesPassado.getYear(), resumo
            );

            SlackNotificacaoService slack = new SlackNotificacaoService();
            boolean ok = slack.enviarMensagem(Config.get("slack.canal"), mensagemFinal);

            if (ok) {
                System.out.println("Mensagem enviada ao Slack com sucesso!");
            } else {
                System.err.println("Slack retornou erro. Verifique a URL do webhook.");
                return;
            }

        } catch (Exception e) {
            System.err.println("Falha ao enviar para o Slack: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        System.out.println("\n=== TESTE CONCLUÍDO COM SUCESSO ===");
    }
}
