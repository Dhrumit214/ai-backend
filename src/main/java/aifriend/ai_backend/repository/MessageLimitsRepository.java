package aifriend.ai_backend.repository;

import aifriend.ai_backend.model.MessageLimits;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MessageLimitsRepository extends JpaRepository<MessageLimits, Long> {
    Optional<MessageLimits> findByUserId(Long userId);
}