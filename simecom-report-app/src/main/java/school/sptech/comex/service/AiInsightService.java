package school.sptech.comex.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import school.sptech.comex.config.AppConfig;
import school.sptech.comex.model.FilterRequest;
import school.sptech.comex.model.MetricsResult;
import school.sptech.comex.util.NumberUtils;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AiInsightService {

    private static final String DEFAULT_LOCAL_AI_URL = "http://localhost:8090/v1/chat/completions";
    private static final String DEFAULT_LOCAL_AI_MODEL = "local-model";

    private static final int CONNECT_TIMEOUT_SECONDS = 10;
    private static final int REQUEST_TIMEOUT_SECONDS = 120;
    private static final int MAX_TOKENS = 900;
    private static final double TEMPERATURE = 0.2;

    private final AppConfig appConfig;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient;

    public AiInsightService(AppConfig appConfig) {
        this.appConfig = appConfig;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(CONNECT_TIMEOUT_SECONDS))
                .build();
    }

    public String generate(FilterRequest filter, MetricsResult metrics) throws IOException, InterruptedException {
        validateInput(filter, metrics);

        String aiApiUrl = resolveAiApiUrl();
        String aiModel = resolveAiModel();
        String aiApiKey = resolveAiApiKey();

        String body = objectMapper.writeValueAsString(buildRequestPayload(aiModel, filter, metrics));

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(aiApiUrl))
                .timeout(Duration.ofSeconds(REQUEST_TIMEOUT_SECONDS))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body));

        /*
         * Para llamafile/local AI, a API key normalmente não é necessária.
         * Mantemos suporte opcional caso você rode a IA atrás de proxy,
         * gateway ou servidor local com autenticação.
         */
        if (hasText(aiApiKey)) {
            requestBuilder.header("Authorization", "Bearer " + aiApiKey.trim());
        }

        HttpResponse<String> response;

        try {
            response = httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
        } catch (ConnectException e) {
            throw new IOException(
                    "Não foi possível conectar na IA local em " + aiApiUrl
                            + ". Verifique se o llamafile está rodando na porta 8090.",
                    e
            );
        }

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException(
                    "Falha na chamada da IA local. HTTP "
                            + response.statusCode()
                            + ": "
                            + safeBody(response.body())
            );
        }

        String text = extractText(response.body());

        if (!hasText(text)) {
            throw new IOException("A IA local respondeu sem conteúdo textual válido.");
        }

        return normalizeInsightText(text);
    }

    private Map<String, Object> buildRequestPayload(String model, FilterRequest filter, MetricsResult metrics) {
        Map<String, Object> payload = new LinkedHashMap<>();

        payload.put("model", model);
        payload.put("temperature", TEMPERATURE);
        payload.put("max_tokens", MAX_TOKENS);
        payload.put("stream", false);

        payload.put("messages", List.of(
                Map.of(
                        "role", "system",
                        "content", buildSystemPrompt()
                ),
                Map.of(
                        "role", "user",
                        "content", buildUserPrompt(filter, metrics)
                )
        ));

        return payload;
    }

    private String buildSystemPrompt() {
        return """
                Você é um analista de comércio exterior.
                Responda sempre em português do Brasil.
                Gere insights executivos objetivos, úteis e tecnicamente coerentes.
                Não invente dados além dos números fornecidos.
                Não cite que é uma IA.
                Não use markdown com tabelas.
                Escreva em parágrafos curtos, separados por quebra de linha.
                """;
    }

    private String buildUserPrompt(FilterRequest filter, MetricsResult metrics) {
        StringBuilder sb = new StringBuilder();

        sb.append("Gere um relatório executivo curto com base nos dados abaixo.\n\n");

        sb.append("Regras da saída:\n");
        sb.append("- Entregue entre 4 e 6 insights numerados.\n");
        sb.append("- Cada insight deve ter no máximo 3 linhas.\n");
        sb.append("- Inclua riscos, oportunidades e leitura executiva dos números.\n");
        sb.append("- Use linguagem clara para tomada de decisão.\n");
        sb.append("- Separe cada insight com uma linha em branco.\n\n");

        sb.append("Contexto informado pelo usuário:\n");
        sb.append(nullToDash(filter.getPrompt())).append("\n\n");

        sb.append("Filtros aplicados:\n");
        sb.append("Tipo de análise: ").append(nullToDash(filter.getTradeType())).append("\n");
        sb.append("Ano inicial: ").append(nullToDash(filter.getYearStart())).append("\n");
        sb.append("Ano final: ").append(nullToDash(filter.getYearEnd())).append("\n");
        sb.append("Mês inicial: ").append(nullToDash(filter.getMonthStart())).append("\n");
        sb.append("Mês final: ").append(nullToDash(filter.getMonthEnd())).append("\n");
        sb.append("SH4: ").append(listToText(filter.getSh4List())).append("\n");
        sb.append("Países: ").append(listToText(filter.getCountryList())).append("\n");
        sb.append("UFs: ").append(listToText(filter.getUfList())).append("\n");
        sb.append("Municípios: ").append(listToText(filter.getMunicipalityList())).append("\n\n");

        sb.append("Métricas calculadas:\n");
        sb.append("Total VL_FOB importação: ").append(NumberUtils.formatMoney(metrics.getTotalVlfobImport())).append("\n");
        sb.append("Total VL_FOB exportação: ").append(NumberUtils.formatMoney(metrics.getTotalVlfobExport())).append("\n");
        sb.append("Diferença absoluta VL_FOB: ").append(NumberUtils.formatMoney(metrics.getFobDifference())).append("\n");
        sb.append("Diferença percentual VL_FOB: ").append(NumberUtils.formatPercent(metrics.getFobDifferencePercent())).append("\n");
        sb.append("Total KG importação: ").append(NumberUtils.formatMoney(metrics.getTotalKgImport())).append("\n");
        sb.append("Total KG exportação: ").append(NumberUtils.formatMoney(metrics.getTotalKgExport())).append("\n");
        sb.append("Top SH4 por VL_FOB: ").append(nullToDash(metrics.getTopSh4ByFob())).append("\n");
        sb.append("Série mensal por tipo: ").append(nullToDash(metrics.getMonthlyFobByTradeType())).append("\n");

        return sb.toString();
    }

    private String extractText(String responseBody) throws IOException {
        if (!hasText(responseBody)) {
            return "";
        }

        JsonNode root = objectMapper.readTree(responseBody);

        /*
         * Formato esperado do llamafile / llama.cpp OpenAI-compatible:
         *
         * {
         *   "choices": [
         *     {
         *       "message": {
         *         "role": "assistant",
         *         "content": "texto..."
         *       }
         *     }
         *   ]
         * }
         */
        JsonNode choices = root.path("choices");
        if (choices.isArray() && !choices.isEmpty()) {
            JsonNode firstChoice = choices.get(0);

            JsonNode messageContent = firstChoice.path("message").path("content");
            if (messageContent.isTextual() && hasText(messageContent.asText())) {
                return messageContent.asText();
            }

            JsonNode text = firstChoice.path("text");
            if (text.isTextual() && hasText(text.asText())) {
                return text.asText();
            }
        }

        /*
         * Compatibilidade com possíveis respostas alternativas.
         */
        JsonNode content = root.path("content");
        if (content.isTextual() && hasText(content.asText())) {
            return content.asText();
        }

        JsonNode text = root.path("text");
        if (text.isTextual() && hasText(text.asText())) {
            return text.asText();
        }

        /*
         * Compatibilidade com formato antigo usado por APIs tipo Responses.
         */
        JsonNode output = root.path("output");
        if (output.isArray()) {
            StringBuilder sb = new StringBuilder();

            for (JsonNode item : output) {
                JsonNode itemContent = item.path("content");

                if (itemContent.isArray()) {
                    for (JsonNode part : itemContent) {
                        JsonNode partText = part.path("text");

                        if (partText.isTextual() && hasText(partText.asText())) {
                            sb.append(partText.asText()).append("\n");
                        }
                    }
                }
            }

            if (!sb.isEmpty()) {
                return sb.toString().trim();
            }
        }

        return "";
    }

    private String resolveAiApiUrl() {
        /*
         * Ordem de prioridade:
         * 1. AI_API_URL configurada no ambiente/AppConfig.
         * 2. URL local padrão na porta 8090.
         *
         * Aceita os seguintes formatos:
         * - http://localhost:8090
         * - http://localhost:8090/v1
         * - http://localhost:8090/v1/chat/completions
         */
        String configuredUrl = appConfig != null ? appConfig.getAiApiUrl() : null;
        String url = hasText(configuredUrl) ? configuredUrl.trim() : DEFAULT_LOCAL_AI_URL;

        return normalizeChatCompletionsUrl(url);
    }

    private String normalizeChatCompletionsUrl(String url) {
        if (!hasText(url)) {
            return DEFAULT_LOCAL_AI_URL;
        }

        String normalized = url.trim();

        while (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }

        if (normalized.endsWith("/v1/chat/completions")) {
            return normalized;
        }

        if (normalized.endsWith("/v1")) {
            return normalized + "/chat/completions";
        }

        return normalized + "/v1/chat/completions";
    }

    private String resolveAiModel() {
        /*
         * O AppConfig atual do projeto usa "gpt-4.1-mini" como padrão.
         * Para IA local, esse default não faz sentido.
         *
         * Por isso, se AI_MODEL não estiver definido no ambiente, usamos "local-model".
         */
        String envModel = System.getenv("AI_MODEL");

        if (hasText(envModel)) {
            return envModel.trim();
        }

        String configModel = appConfig != null ? appConfig.getAiModel() : null;

        if (hasText(configModel) && !configModel.toLowerCase().contains("gpt")) {
            return configModel.trim();
        }

        return DEFAULT_LOCAL_AI_MODEL;
    }

    private String resolveAiApiKey() {
        /*
         * Para llamafile, a chave é opcional.
         * Se existir, será enviada.
         * Se não existir, a chamada segue sem Authorization.
         */
        String configuredKey = appConfig != null ? appConfig.getAiApiKey() : null;

        if (hasText(configuredKey)) {
            return configuredKey;
        }

        return System.getenv("AI_API_KEY");
    }

    private void validateInput(FilterRequest filter, MetricsResult metrics) {
        if (filter == null) {
            throw new IllegalArgumentException("FilterRequest não pode ser nulo.");
        }

        if (metrics == null) {
            throw new IllegalArgumentException("MetricsResult não pode ser nulo.");
        }
    }

    private String normalizeInsightText(String text) {
        if (!hasText(text)) {
            return "";
        }

        return text
                .replace("\r\n", "\n")
                .replace('\r', '\n')
                .replaceAll("[ \\t]+\\n", "\n")
                .replaceAll("\\n{3,}", "\n\n")
                .trim();
    }

    private String safeBody(String body) {
        if (!hasText(body)) {
            return "<sem corpo de resposta>";
        }

        String normalized = body.replace("\n", " ").replace("\r", " ").trim();

        if (normalized.length() <= 1000) {
            return normalized;
        }

        return normalized.substring(0, 1000) + "...";
    }

    private String listToText(List<?> values) {
        if (values == null || values.isEmpty()) {
            return "-";
        }

        return String.join(", ", values.stream()
                .map(String::valueOf)
                .toList());
    }

    private String nullToDash(Object value) {
        if (value == null) {
            return "-";
        }

        String text = String.valueOf(value);

        return hasText(text) ? text : "-";
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}