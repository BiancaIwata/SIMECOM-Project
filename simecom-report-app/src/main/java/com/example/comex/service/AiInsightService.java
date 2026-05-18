package com.example.comex.service;

import com.example.comex.config.AppConfig;
import com.example.comex.model.FilterRequest;
import com.example.comex.model.MetricsResult;
import com.example.comex.util.NumberUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

public class AiInsightService {
    private final AppConfig appConfig;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AiInsightService(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    public String generate(FilterRequest filter, MetricsResult metrics) throws IOException, InterruptedException {
        if (!appConfig.isAiConfigured()) {
            throw new IllegalStateException("IA não configurada.");
        }

        String prompt = buildPrompt(filter, metrics);
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("model", appConfig.getAiModel());
        payload.put("input", prompt);

        String body = objectMapper.writeValueAsString(payload);

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(20))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(appConfig.getAiApiUrl()))
                .timeout(Duration.ofSeconds(60))
                .header("Authorization", "Bearer " + appConfig.getAiApiKey())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("Falha na chamada da IA. HTTP " + response.statusCode() + ": " + response.body());
        }

        return extractText(response.body());
    }

    private String buildPrompt(FilterRequest filter, MetricsResult metrics) {
        return "Você é um analista de comércio exterior. Gere um relatório executivo curto, em português do Brasil, " +
                "com insights objetivos, riscos e oportunidades.\n\n" +
                "Prompt base do usuário: " + filter.getPrompt() + "\n" +
                "Tipo de análise: " + filter.getTradeType() + "\n" +
                "Período: " + filter.getYearStart() + " a " + filter.getYearEnd() + "\n" +
                "Total VL_FOB importação: " + NumberUtils.formatMoney(metrics.getTotalVlfobImport()) + "\n" +
                "Total VL_FOB exportação: " + NumberUtils.formatMoney(metrics.getTotalVlfobExport()) + "\n" +
                "Diferença absoluta: " + NumberUtils.formatMoney(metrics.getFobDifference()) + "\n" +
                "Diferença percentual: " + NumberUtils.formatPercent(metrics.getFobDifferencePercent()) + "\n" +
                "Total KG importação: " + NumberUtils.formatMoney(metrics.getTotalKgImport()) + "\n" +
                "Total KG exportação: " + NumberUtils.formatMoney(metrics.getTotalKgExport()) + "\n" +
                "Top SH4 por VL_FOB: " + metrics.getTopSh4ByFob() + "\n" +
                "Série mensal por tipo: " + metrics.getMonthlyFobByTradeType() + "\n";
    }

    private String extractText(String responseBody) throws IOException {
        JsonNode root = objectMapper.readTree(responseBody);

        if (root.has("output") && root.get("output").isArray()) {
            StringBuilder sb = new StringBuilder();
            for (JsonNode item : root.get("output")) {
                JsonNode content = item.get("content");
                if (content != null && content.isArray()) {
                    for (JsonNode part : content) {
                        if (part.has("text")) {
                            sb.append(part.get("text").asText()).append("\n");
                        }
                    }
                }
            }
            if (!sb.isEmpty()) {
                return sb.toString().trim();
            }
        }

        if (root.has("text")) {
            return root.get("text").asText();
        }

        return responseBody;
    }
}
