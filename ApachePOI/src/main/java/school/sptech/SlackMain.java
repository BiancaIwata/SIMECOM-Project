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
            conn = DatabaseConnection.getConnection();
            System.out.println("Banco conectado!\n");
        } catch (Exception e) {
            System.err.println("Falha ao conectar no banco: " + e.getMessage());
            return;
        }

       // 2. Gera o relatório com os dados mais recentes do banco
        System.out.println(">> Buscando dados mais recentes no banco...");
        String resumo;
        int anoRecente;
        int mesRecente;
        
        try {
            RelatorioMensalDAO dao = new RelatorioMensalDAO(conn);

            // Busca dinamicamente o último ano e mês disponíveis
            int[] dataRecente = dao.buscarAnoMesMaisRecente();
            anoRecente = dataRecente[0];
            mesRecente = dataRecente[1];
            
            System.out.printf("Dados encontrados referentes a: %02d/%d%n", mesRecente, anoRecente);
            System.out.println(">> Gerando relatório...");

            resumo = dao.gerarResumoMensal(anoRecente, mesRecente);
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
            // Repare que aqui usamos as variáveis dinâmicas no título do Slack também
            String mensagemFinal = String.format(
                    ":bar_chart: *Atualização Mensal SIMECOM — %02d/%d*\n\n%s",
                    mesRecente, anoRecente, resumo
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
    }
}
