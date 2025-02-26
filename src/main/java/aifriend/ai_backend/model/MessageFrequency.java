package aifriend.ai_backend.model;

public enum MessageFrequency {
    DAILY("daily"),
    WEEKLY("weekly"),
    MONTHLY("monthly");
    
    private final String value;
    
    MessageFrequency(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static MessageFrequency fromValue(String value) {
        for (MessageFrequency frequency : MessageFrequency.values()) {
            if (frequency.value.equalsIgnoreCase(value)) {
                return frequency;
            }
        }
        throw new IllegalArgumentException("Unknown message frequency: " + value);
    }
}
