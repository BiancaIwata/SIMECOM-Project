package com.example.comex.service;

import com.example.comex.model.ChartFiles;
import com.example.comex.model.MetricsResult;
import com.example.comex.util.FileUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public class ChartService {

    public ChartFiles generate(MetricsResult metrics, String outputDirectory) throws IOException {
        Path outDir = FileUtils.ensureDirectory(outputDirectory);
        ChartFiles chartFiles = new ChartFiles();

        chartFiles.setChart1(createFobComparisonChart(metrics, outDir));
        chartFiles.setChart2(createMonthlyFobChart(metrics, outDir));
        chartFiles.setChart3(createTopSh4Chart(metrics, outDir));

        return chartFiles;
    }

    private Path createFobComparisonChart(MetricsResult metrics, Path outDir) throws IOException {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        dataset.setValue("Importação", metrics.getTotalVlfobImport());
        dataset.setValue("Exportação", metrics.getTotalVlfobExport());

        JFreeChart chart = ChartFactory.createPieChart(
                "Comparativo de VL_FOB",
                dataset,
                true,
                true,
                false
        );

        Path path = outDir.resolve("grafico_1_comparativo_fob.png");
        ChartUtils.saveChartAsPNG(path.toFile(), chart, 900, 500);
        return path;
    }

    private Path createMonthlyFobChart(MetricsResult metrics, Path outDir) throws IOException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, java.math.BigDecimal> entry : metrics.getMonthlyFobByTradeType().entrySet()) {
            String[] parts = entry.getKey().split("-");
            String category = parts[0] + "-" + parts[1];
            String series = parts[2];
            dataset.addValue(entry.getValue(), series, category);
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "VL_FOB por mês e tipo",
                "Mês",
                "VL_FOB",
                dataset
        );

        Path path = outDir.resolve("grafico_2_serie_mensal.png");
        ChartUtils.saveChartAsPNG(path.toFile(), chart, 1000, 500);
        return path;
    }

    private Path createTopSh4Chart(MetricsResult metrics, Path outDir) throws IOException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, java.math.BigDecimal> entry : metrics.getTopSh4ByFob().entrySet()) {
            dataset.addValue(entry.getValue(), "VL_FOB", entry.getKey());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Top SH4 por VL_FOB",
                "SH4",
                "VL_FOB",
                dataset
        );

        Path path = outDir.resolve("grafico_3_top_sh4.png");
        ChartUtils.saveChartAsPNG(path.toFile(), chart, 1000, 500);
        return path;
    }
}
