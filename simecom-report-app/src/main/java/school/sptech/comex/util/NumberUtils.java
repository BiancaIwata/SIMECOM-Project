package school.sptech.comex.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class NumberUtils {
    private NumberUtils() {
    }

    public static BigDecimal safeBigDecimal(String value) {
        if (value == null || value.isBlank()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(value.trim().replace(",", "."));
    }

    public static String formatMoney(BigDecimal value) {
        return value == null ? "0.00" : value.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    public static String formatPercent(BigDecimal value) {
        return value == null ? "0.00%" : value.setScale(2, RoundingMode.HALF_UP).toPlainString() + "%";
    }
}
