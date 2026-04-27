package com.example.comex.model;

import java.util.List;

public class FilterRequest {
    private String importCsvPath;
    private String exportCsvPath;
    private TradeType tradeType = TradeType.BOTH;
    private Integer yearStart;
    private Integer yearEnd;
    private Integer monthStart;
    private Integer monthEnd;
    private List<String> sh4List;
    private List<String> countryList;
    private List<String> ufList;
    private List<String> municipalityList;
    private String outputDirectory = "output";
    private String reportTitle = "Relatório COMEX";
    private String prompt = "Analise os dados de comércio exterior e gere insights objetivos.";

    public String getImportCsvPath() { return importCsvPath; }
    public void setImportCsvPath(String importCsvPath) { this.importCsvPath = importCsvPath; }
    public String getExportCsvPath() { return exportCsvPath; }
    public void setExportCsvPath(String exportCsvPath) { this.exportCsvPath = exportCsvPath; }
    public TradeType getTradeType() { return tradeType; }
    public void setTradeType(TradeType tradeType) { this.tradeType = tradeType; }
    public Integer getYearStart() { return yearStart; }
    public void setYearStart(Integer yearStart) { this.yearStart = yearStart; }
    public Integer getYearEnd() { return yearEnd; }
    public void setYearEnd(Integer yearEnd) { this.yearEnd = yearEnd; }
    public Integer getMonthStart() { return monthStart; }
    public void setMonthStart(Integer monthStart) { this.monthStart = monthStart; }
    public Integer getMonthEnd() { return monthEnd; }
    public void setMonthEnd(Integer monthEnd) { this.monthEnd = monthEnd; }
    public List<String> getSh4List() { return sh4List; }
    public void setSh4List(List<String> sh4List) { this.sh4List = sh4List; }
    public List<String> getCountryList() { return countryList; }
    public void setCountryList(List<String> countryList) { this.countryList = countryList; }
    public List<String> getUfList() { return ufList; }
    public void setUfList(List<String> ufList) { this.ufList = ufList; }
    public List<String> getMunicipalityList() { return municipalityList; }
    public void setMunicipalityList(List<String> municipalityList) { this.municipalityList = municipalityList; }
    public String getOutputDirectory() { return outputDirectory; }
    public void setOutputDirectory(String outputDirectory) { this.outputDirectory = outputDirectory; }
    public String getReportTitle() { return reportTitle; }
    public void setReportTitle(String reportTitle) { this.reportTitle = reportTitle; }
    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }
}
