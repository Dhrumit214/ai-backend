package aifriend.ai_backend.model;

public enum PlanType {
    FREE("free"),
    PAYASYOUGO("payasyougo"),
    BASIC("basic"),
    PREMIUM("premium"),
    VIP("vip");
    
    private final String value;
    
    PlanType(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static PlanType fromValue(String value) {
        for (PlanType type : PlanType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown plan type: " + value);
    }
}
