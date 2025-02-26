package aifriend.ai_backend.repository;

import aifriend.ai_backend.model.UserPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPreferencesRepository extends JpaRepository<UserPreferences, Long> {
    List<UserPreferences> findByDoNotDisturbFalse();
}