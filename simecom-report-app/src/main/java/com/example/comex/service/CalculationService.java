package com.example.comex.service;

import com.example.comex.model.MetricsResult;
import com.example.comex.model.TradeRecord;
import com.example.comex.model.TradeType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class CalculationService {

    public MetricsResult calculate(List<TradeRecord> records) {
        MetricsResult result = new MetricsResult();

        BigDecimal importFob = sumByType(records, TradeType.IMPORT, true);
        BigDecimal exportFob = sumByType(records, TradeType.EXPORT, true);
        BigDecimal importKg = sumByType(records, TradeType.IMPORT, false);
        BigDecimal exportKg = sumByType(records, TradeType.EXPORT, false);

        result.setTotalVlfobImport(importFob);
        result.setTotalVlfobExport(exportFob);
        result.setTotalKgImport(importKg);
        result.setTotalKgExport(exportKg);
        result.setFobDifference(exportFob.subtract(importFob));

        if (importFob.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal percent = exportFob.subtract(importFob)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(importFob, 4, RoundingMode.HALF_UP);
            result.setFobDifferencePercent(percent);
        }

        result.setMonthlyFobByTradeType(buildMonthlyChartData(records));
        result.setTopSh4ByFob(buildTopSh4(records));
        return result;
    }

    private BigDecimal sumByType(List<TradeRecord> records, TradeType tradeType, boolean useFob) {
        return records.stream()
                .filter(record -> record.getTradeType() == tradeType)
                .map(record -> useFob ? record.getVlFob() : record.getKgLiquido())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Map<String, BigDecimal> buildMonthlyChartData(List<TradeRecord> records) {
        Map<String, BigDecimal> map = new TreeMap<>();
        for (TradeRecord record : records) {
            String key = String.format("%04d-%02d-%s", record.getCoAno(), record.getCoMes(), record.getTradeType().name());
            map.merge(key, record.getVlFob(), BigDecimal::add);
        }
        return map;
    }

    private Map<String, BigDecimal> buildTopSh4(List<TradeRecord> records) {
        Map<String, BigDecimal> grouped = records.stream()
                .collect(Collectors.groupingBy(TradeRecord::getSh4,
                        Collectors.reducing(BigDecimal.ZERO, TradeRecord::getVlFob, BigDecimal::add)));

        return grouped.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue(Comparator.reverseOrder()))
                .limit(10)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }
}
