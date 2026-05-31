package school.sptech.comex.app;

import school.sptech.comex.config.AppConfig;
import school.sptech.comex.model.FilterRequest;
import school.sptech.comex.model.ReportContext;
import school.sptech.comex.model.TradeRecord;
import school.sptech.comex.model.TradeType;
import school.sptech.comex.service.AiInsightService;
import school.sptech.comex.service.CalculationService;
import school.sptech.comex.service.ChartService;
import school.sptech.comex.service.CsvReaderService;
import school.sptech.comex.service.FilterService;
import school.sptech.comex.service.LocalInsightService;
import school.sptech.comex.service.PdfReportService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                System.out.println("Uso: java -jar comex-report-app-1.0.0-jar-with-dependencies.jar caminho/do/filtro.json");
                return;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            FilterRequest filterRequest = objectMapper.readValue(Path.of(args[0]).toFile(), FilterRequest.class);
            if (filterRequest.getTradeType() == null) {
                filterRequest.setTradeType(TradeType.BOTH);
            }

            CsvReaderService csvReaderService = new CsvReaderService();
            List<TradeRecord> allRecords = new ArrayList<>();

            if (filterRequest.getTradeType() == TradeType.IMPORT || filterRequest.getTradeType() == TradeType.BOTH) {
                allRecords.addAll(csvReaderService.read(Path.of(filterRequest.getImportCsvPath()), TradeType.IMPORT));
            }
            if (filterRequest.getTradeType() == TradeType.EXPORT || filterRequest.getTradeType() == TradeType.BOTH) {
                allRecords.addAll(csvReaderService.read(Path.of(filterRequest.getExportCsvPath()), TradeType.EXPORT));
            }

            FilterService filterService = new FilterService();
            List<TradeRecord> filteredRecords = filterService.apply(allRecords, filterRequest);

            CalculationService calculationService = new CalculationService();
            var metrics = calculationService.calculate(filteredRecords);

            ChartService chartService = new ChartService();
            var chartFiles = chartService.generate(metrics, filterRequest.getOutputDirectory());

            AppConfig appConfig = new AppConfig();
            String insightText;
            boolean usedAi = false;
            try {
                insightText = new AiInsightService(appConfig).generate(filterRequest, metrics);
                usedAi = true;
                System.out.println("IA utilizada para gerar insights.");
            } catch (Exception e) {
                insightText = new LocalInsightService().generate(filterRequest, metrics);
                System.out.println("Fallback local utilizado: " + e.getMessage());
            }

            ReportContext reportContext = new ReportContext();
            reportContext.setFilterRequest(filterRequest);
            reportContext.setFilteredRecords(filteredRecords);
            reportContext.setMetricsResult(metrics);
            reportContext.setChartFiles(chartFiles);
            reportContext.setInsightText(insightText);
            reportContext.setUsedAi(usedAi);

            PdfReportService pdfReportService = new PdfReportService();
            Path pdfPath = pdfReportService.generate(reportContext);
            reportContext.setPdfPath(pdfPath);

            System.out.println("Registros lidos: " + allRecords.size());
            System.out.println("Registros filtrados: " + filteredRecords.size());
            System.out.println("PDF gerado em: " + pdfPath.toAbsolutePath());
            System.out.println("Gráficos gerados em: " + chartFiles.getChart1().getParent().toAbsolutePath());
        } catch (Exception e) {
            System.err.println("Erro ao executar a aplicação: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
