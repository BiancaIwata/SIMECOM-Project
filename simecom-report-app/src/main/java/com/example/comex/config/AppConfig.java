package com.example.comex.config;

public class AppConfig {
    private final String aiApiUrl;
    private final String aiApiKey;
    private final String aiModel;

    public AppConfig() {
        this.aiApiUrl = System.getenv("AI_API_URL");
        this.aiApiKey = System.getenv("AI_API_KEY");
        this.aiModel = System.getenv().getOrDefault("AI_MODEL", "gpt-4.1-mini");
    }

    public String getAiApiUrl() { return aiApiUrl; }
    public String getAiApiKey() { return aiApiKey; }
    public String getAiModel() { return aiModel; }

    public boolean isAiConfigured() {
        return aiApiUrl != null && !aiApiUrl.isBlank() && aiApiKey != null && !aiApiKey.isBlank();
    }
}
