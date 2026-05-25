package school.sptech.comex.service;

import school.sptech.comex.model.FilterRequest;
import school.sptech.comex.model.TradeRecord;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FilterService {

    public List<TradeRecord> apply(List<TradeRecord> source, FilterRequest filter) {
        Set<String> sh4Set = normalizeSet(filter.getSh4List());
        Set<String> countrySet = normalizeSet(filter.getCountryList());
        Set<String> ufSet = normalizeSet(filter.getUfList());
        Set<String> municipalitySet = normalizeSet(filter.getMunicipalityList());

        return source.stream()
                .filter(r -> filter.getYearStart() == null || r.getCoAno() >= filter.getYearStart())
                .filter(r -> filter.getYearEnd() == null || r.getCoAno() <= filter.getYearEnd())
                .filter(r -> filter.getMonthStart() == null || r.getCoMes() >= filter.getMonthStart())
                .filter(r -> filter.getMonthEnd() == null || r.getCoMes() <= filter.getMonthEnd())
                .filter(r -> sh4Set.isEmpty() || sh4Set.contains(r.getSh4()))
                .filter(r -> countrySet.isEmpty() || countrySet.contains(r.getCoPais()))
                .filter(r -> ufSet.isEmpty() || ufSet.contains(r.getSgUfMun()))
                .filter(r -> municipalitySet.isEmpty() || municipalitySet.contains(r.getCoMun()))
                .toList();
    }

    private Set<String> normalizeSet(List<String> list) {
        if (list == null) {
            return Set.of();
        }
        return list.stream()
                .filter(v -> v != null && !v.isBlank())
                .map(String::trim)
                .collect(Collectors.toSet());
    }
}
