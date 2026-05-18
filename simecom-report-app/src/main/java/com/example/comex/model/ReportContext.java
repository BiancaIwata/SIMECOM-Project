package com.example.comex.model;

import java.nio.file.Path;
import java.util.List;

public class ReportContext {
    private FilterRequest filterRequest;
    private List<TradeRecord> filteredRecords;
    private MetricsResult metricsResult;
    private ChartFiles chartFiles;
    private String insightText;
    private boolean usedAi;
    private Path pdfPath;

    public FilterRequest getFilterRequest() { return filterRequest; }
    public void setFilterRequest(FilterRequest filterRequest) { this.filterRequest = filterRequest; }
    public List<TradeRecord> getFilteredRecords() { return filteredRecords; }
    public void setFilteredRecords(List<TradeRecord> filteredRecords) { this.filteredRecords = filteredRecords; }
    public MetricsResult getMetricsResult() { return metricsResult; }
    public void setMetricsResult(MetricsResult metricsResult) { this.metricsResult = metricsResult; }
    public ChartFiles getChartFiles() { return chartFiles; }
    public void setChartFiles(ChartFiles chartFiles) { this.chartFiles = chartFiles; }
    public String getInsightText() { return insightText; }
    public void setInsightText(String insightText) { this.insightText = insightText; }
    public boolean isUsedAi() { return usedAi; }
    public void setUsedAi(boolean usedAi) { this.usedAi = usedAi; }
    public Path getPdfPath() { return pdfPath; }
    public void setPdfPath(Path pdfPath) { this.pdfPath = pdfPath; }
}
