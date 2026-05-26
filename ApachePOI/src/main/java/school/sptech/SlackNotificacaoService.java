package school.sptech;

import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;

public class SlackNotificacaoService {

    private final MethodsClient client;

    public SlackNotificacaoService() {
        Slack slack = Slack.getInstance();
        this.client = slack.methods(Config.get("slack.bot.token"));
    }

    public boolean enviarMensagem(String destinatario, String mensagem) {
        try {
            ChatPostMessageResponse response = client.chatPostMessage(req -> req
                    .channel(destinatario)
                    .text(mensagem)
                    .mrkdwn(true)
            );

            if (!response.isOk()) {
                System.err.println("Erro Slack: " + response.getError());
                return false;
            }
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String buscarUserIdPorEmail(String email) {
        try {
            var response = client.usersLookupByEmail(req -> req.email(email));
            if (response.isOk()) {
                return response.getUser().getId();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}