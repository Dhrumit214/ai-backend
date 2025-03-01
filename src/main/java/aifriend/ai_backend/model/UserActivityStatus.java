package aifriend.ai_backend.model;

public enum UserActivityStatus {
    VERY_ACTIVE("very_active"),
    ACTIVE("active"),
    OCCASIONAL("occasional"),
    DORMANT("dormant");
    
    private final String value;
    
    UserActivityStatus(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static UserActivityStatus fromValue(String value) {
        for (UserActivityStatus status : UserActivityStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown user activity status: " + value);
    }
}