package aifriend.ai_backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Composite primary key for the Message entity
 * Combines id and createdAt fields
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageId implements Serializable {
    private Long id;
    private LocalDateTime createdAt;
}