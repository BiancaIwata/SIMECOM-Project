package school.sptech;

import java.sql.Connection;
import java.time.LocalDate;

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

            String canal = Config.get("slack.canal");
            SlackNotificacaoService slack = new SlackNotificacaoService();
            boolean ok = slack.enviarMensagem(canal, mensagemFinal);

        } catch (Exception e) {
            System.err.println("Erro ao enviar notificação: " + e.getMessage());
            e.printStackTrace();
        }
    }
}