package school.sptech.comex.service;

import school.sptech.comex.model.FilterRequest;
import school.sptech.comex.model.MetricsResult;
import school.sptech.comex.util.NumberUtils;

public class LocalInsightService {

    public String generate(FilterRequest filter, MetricsResult metrics) {
        StringBuilder sb = new StringBuilder();
        sb.append("Relatório gerado sem apoio da IA externa.\n\n");
        sb.append("1. O total de VL_FOB de importação no recorte foi ")
                .append(NumberUtils.formatMoney(metrics.getTotalVlfobImport()))
                .append(", enquanto o total de exportação foi ")
                .append(NumberUtils.formatMoney(metrics.getTotalVlfobExport()))
                .append(".\n");
        sb.append("2. A diferença entre exportação e importação ficou em ")
                .append(NumberUtils.formatMoney(metrics.getFobDifference()))
                .append(" (")
                .append(NumberUtils.formatPercent(metrics.getFobDifferencePercent()))
                .append(").\n");
        sb.append("3. O volume líquido total analisado foi de ")
                .append(NumberUtils.formatMoney(metrics.getTotalKgImport().add(metrics.getTotalKgExport())))
                .append(" kg.\n");
        if (!metrics.getTopSh4ByFob().isEmpty()) {
            String topSh4 = metrics.getTopSh4ByFob().keySet().iterator().next();
            sb.append("4. O código SH4 com maior participação em VL_FOB foi ")
                    .append(topSh4)
                    .append(".\n");
        }
        sb.append("5. Filtros aplicados: tipo=")
                .append(filter.getTradeType())
                .append(", anos=")
                .append(filter.getYearStart())
                .append(" até ")
                .append(filter.getYearEnd())
                .append(".\n");
        return sb.toString();
    }
}
