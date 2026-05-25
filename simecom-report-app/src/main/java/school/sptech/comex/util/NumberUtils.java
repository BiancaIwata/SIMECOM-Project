package school.sptech.comex.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public final class NumberUtils {

    private static final Locale PT_BR = Locale.of("pt", "BR");

    private NumberUtils() {
    }

    public static BigDecimal safeBigDecimal(String value) {
        if (value == null || value.isBlank()) {
            return BigDecimal.ZERO;
        }

        try {
            return new BigDecimal(value.trim().replace(",", "."));
        } catch (NumberFormatException ex) {
            return BigDecimal.ZERO;
        }
    }

    public static String formatMoney(BigDecimal value) {
        if (value == null) {
            value = BigDecimal.ZERO;
        }

        NumberFormat formatter = NumberFormat.getNumberInstance(PT_BR);
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);

        return "US$ " + formatter.format(value);
    }

    public static String formatPercent(BigDecimal value) {
        if (value == null) {
            value = BigDecimal.ZERO;
        }

        NumberFormat formatter = NumberFormat.getNumberInstance(PT_BR);
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);

        return formatter.format(value) + "%";
    }

    public static String formatNumber(BigDecimal value) {
        if (value == null) {
            value = BigDecimal.ZERO;
        }

        NumberFormat formatter = NumberFormat.getNumberInstance(PT_BR);
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);

        return formatter.format(value);
    }
}