package aifriend.ai_backend.model;

public enum SentimentType {
    POSITIVE("positive"),
    NEUTRAL("neutral"),
    NEGATIVE("negative");
    
    private final String value;
    
    SentimentType(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static SentimentType fromValue(String value) {
        for (SentimentType type : SentimentType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown sentiment type: " + value);
    }
}
