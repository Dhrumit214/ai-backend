package aifriend.ai_backend.repository;

import aifriend.ai_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByPhoneNumberHash(String phoneNumberHash);
    boolean existsByPhoneNumberHash(String phoneNumberHash);
}