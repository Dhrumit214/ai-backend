package aifriend.ai_backend.model;




public class MessageResponse {

    private String userId;

    private String aiResponse;

    private String sentiment;

    public MessageResponse(String userId, String aiResponse, String sentiment) {
        this.userId = userId;
        this.aiResponse = aiResponse;
        this.sentiment = sentiment;
    }

    // existing fields and methods

}