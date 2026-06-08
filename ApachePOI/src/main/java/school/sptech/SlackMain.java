package school.sptech;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;


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
            String mensagemFinal = String.format(
                    ":bar_chart: *Atualização Mensal SIMECOM — %02d/%d*\n\n%s",
                    mesRecente, anoRecente, resumo
            );

            SlackNotificacaoService slack = new SlackNotificacaoService();

           
            RelatorioMensalDAO dao = new RelatorioMensalDAO(conn);
            List<String> idsUsuarios = dao.buscarDestinatariosSlack();

            if (idsUsuarios.isEmpty()) {
                System.out.println("Nenhum usuário encontrado com integração do Slack ativa.");
                return;
            }

            // Envia para a DM de cada usuário encontrado
            boolean peloMenosUmEnviado = false;
            for (String slackUserId : idsUsuarios) {
                System.out.println("Disparando para o usuário: " + slackUserId);
                boolean ok = slack.enviarMensagem(slackUserId, mensagemFinal);
                if (ok) {
                    peloMenosUmEnviado = true;
                }
            }

            if (peloMenosUmEnviado) {
                System.out.println("Relatórios enviados ao Slack com sucesso!");
            } else {
                System.err.println("Slack retornou erro para todos os usuários. Verifique os tokens.");
                return;
            }

        } catch (Exception e) {
            System.err.println("Falha ao enviar para o Slack: " + e.getMessage());
            e.printStackTrace();
            return;
        }
    }
}
