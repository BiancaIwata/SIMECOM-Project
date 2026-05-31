package school.sptech.comex.service;

import school.sptech.comex.model.TradeRecord;
import school.sptech.comex.model.TradeType;
import school.sptech.comex.util.CsvUtils;
import school.sptech.comex.util.NumberUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CsvReaderService {

    public List<TradeRecord> read(Path path, TradeType tradeType) throws IOException {
        List<TradeRecord> records = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line = reader.readLine();
            if (line == null) {
                return records;
            }

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                List<String> columns = CsvUtils.splitSemicolonCsvLine(line);
                if (columns.size() < 8) {
                    continue;
                }
                TradeRecord record = new TradeRecord();
                record.setCoAno(Integer.parseInt(columns.get(0)));
                record.setCoMes(Integer.parseInt(columns.get(1)));
                record.setSh4(columns.get(2));
                record.setCoPais(columns.get(3));
                record.setSgUfMun(columns.get(4));
                record.setCoMun(columns.get(5));
                record.setKgLiquido(NumberUtils.safeBigDecimal(columns.get(6)));
                record.setVlFob(NumberUtils.safeBigDecimal(columns.get(7)));
                record.setTradeType(tradeType);
                records.add(record);
            }
        }
        return records;
    }
}
