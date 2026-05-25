package school.sptech.comex.model;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

public class MetricsResult {
    private BigDecimal totalVlfobImport = BigDecimal.ZERO;
    private BigDecimal totalVlfobExport = BigDecimal.ZERO;
    private BigDecimal totalKgImport = BigDecimal.ZERO;
    private BigDecimal totalKgExport = BigDecimal.ZERO;
    private BigDecimal fobDifference = BigDecimal.ZERO;
    private BigDecimal fobDifferencePercent = BigDecimal.ZERO;
    private Map<String, BigDecimal> monthlyFobByTradeType = new LinkedHashMap<>();
    private Map<String, BigDecimal> topSh4ByFob = new LinkedHashMap<>();

    public BigDecimal getTotalVlfobImport() { return totalVlfobImport; }
    public void setTotalVlfobImport(BigDecimal totalVlfobImport) { this.totalVlfobImport = totalVlfobImport; }
    public BigDecimal getTotalVlfobExport() { return totalVlfobExport; }
    public void setTotalVlfobExport(BigDecimal totalVlfobExport) { this.totalVlfobExport = totalVlfobExport; }
    public BigDecimal getTotalKgImport() { return totalKgImport; }
    public void setTotalKgImport(BigDecimal totalKgImport) { this.totalKgImport = totalKgImport; }
    public BigDecimal getTotalKgExport() { return totalKgExport; }
    public void setTotalKgExport(BigDecimal totalKgExport) { this.totalKgExport = totalKgExport; }
    public BigDecimal getFobDifference() { return fobDifference; }
    public void setFobDifference(BigDecimal fobDifference) { this.fobDifference = fobDifference; }
    public BigDecimal getFobDifferencePercent() { return fobDifferencePercent; }
    public void setFobDifferencePercent(BigDecimal fobDifferencePercent) { this.fobDifferencePercent = fobDifferencePercent; }
    public Map<String, BigDecimal> getMonthlyFobByTradeType() { return monthlyFobByTradeType; }
    public void setMonthlyFobByTradeType(Map<String, BigDecimal> monthlyFobByTradeType) { this.monthlyFobByTradeType = monthlyFobByTradeType; }
    public Map<String, BigDecimal> getTopSh4ByFob() { return topSh4ByFob; }
    public void setTopSh4ByFob(Map<String, BigDecimal> topSh4ByFob) { this.topSh4ByFob = topSh4ByFob; }
}
