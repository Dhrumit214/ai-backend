package aifriend.ai_backend.model;

public enum MessageContentType {
    TEXT("text"),
    AUDIO("audio"),
    IMAGE("image");
    
    private final String value;
    
    MessageContentType(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static MessageContentType fromValue(String value) {
        for (MessageContentType type : MessageContentType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown message content type: " + value);
    }
}
