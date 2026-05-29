package school.sptech;

import java.sql.Connection;
import java.time.LocalDate;

public class NotificacaoMensal implements Runnable {

    @Override
    public void run() {
        try (Connection conn = DatabaseConnection.getConnection()) {

            RelatorioMensalDAO dao = new RelatorioMensalDAO(conn);
            
            // Busca do banco o mês/ano mais recente, abandonando o LocalDate
            int[] dataRecente = dao.buscarAnoMesMaisRecente();
            int ano = dataRecente[0];
            int mes = dataRecente[1];

            String resumo = dao.gerarResumoMensal(ano, mes);

            String mensagemFinal = String.format(
                    ":bar_chart: *Atualização Mensal SIMECOM — %02d/%d*\n\n%s", mes, ano, resumo);

            String canal = Config.get("slack.canal");
            SlackNotificacaoService slack = new SlackNotificacaoService();
            slack.enviarMensagem(canal, mensagemFinal);

        } catch (Exception e) {
            System.err.println("Erro ao enviar notificação: " + e.getMessage());
            e.printStackTrace();
        }
    }
}