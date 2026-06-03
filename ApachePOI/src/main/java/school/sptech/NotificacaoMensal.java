package school.sptech;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

public class NotificacaoMensal implements Runnable {

    @Override
    public void run() {
        try (Connection conn = DataSource.getConnection()) {

            LocalDate mesPassado = LocalDate.now().minusMonths(1);
            int ano = mesPassado.getYear();
            int mes = mesPassado.getMonthValue();

            RelatorioMensalDAO dao = new RelatorioMensalDAO(conn);
            String resumo = dao.gerarResumoMensal(ano, mes);

            String mensagemFinal = String.format(
                    ":bar_chart: *Atualização Mensal SIMECOM — %02d/%d*\n\n%s", mes, ano, resumo);

            SlackNotificacaoService slack = new SlackNotificacaoService();
            
            List<String> idsUsuarios = dao.buscarDestinatariosSlack();

            if (idsUsuarios.isEmpty()) {
                System.out.println("Nenhum usuário com integração ativa no Slack para receber o relatório.");
                return;
            }

            for (String slackUserId : idsUsuarios) {
                System.out.println("Enviando relatório mensal para o usuário Slack: " + slackUserId);
                boolean ok = slack.enviarMensagem(slackUserId, mensagemFinal);
                
                if (!ok) {
                    System.err.println("Falha ao enviar para o usuário: " + slackUserId);
                }
            }

        } catch (Exception e) {
            System.err.println("Erro ao enviar notificação: " + e.getMessage());
            e.printStackTrace();
        }
    }
}