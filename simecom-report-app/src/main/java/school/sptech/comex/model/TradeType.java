package school.sptech.comex.model;

public enum TradeType {
    IMPORT,
    EXPORT,
    BOTH;

    public static TradeType fromString(String value) {
        if (value == null || value.isBlank()) {
            return BOTH;
        }
        return TradeType.valueOf(value.trim().toUpperCase());
    }
}
