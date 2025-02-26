package aifriend.ai_backend.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class SecurityUtilsTest {

    @Autowired
    private SecurityUtils securityUtils;
    
    @Test
    public void testEncryptAndDecrypt() {
        // Given
        String sensitiveData = "+15551234567";
        
        // When
        String encrypted = securityUtils.encrypt(sensitiveData);
        String decrypted = securityUtils.decrypt(encrypted);
        
        // Then
        assertNotEquals(sensitiveData, encrypted, "Encrypted data should not match original");
        assertEquals(sensitiveData, decrypted, "Decrypted data should match original");
        
        // Verify encryption produces different ciphertext for same data (due to random IV)
        String encrypted2 = securityUtils.encrypt(sensitiveData);
        assertNotEquals(encrypted, encrypted2, "Encrypting same data twice should produce different results due to random IV");
    }
    
    @Test
    public void testSecureHash() {
        // Given
        String sensitiveData = "+15551234567";
        
        // When
        String hash1 = securityUtils.secureHash(sensitiveData);
        String hash2 = securityUtils.secureHash(sensitiveData);
        
        // Then
        assertNotNull(hash1, "Hash should not be null");
        assertNotEquals(sensitiveData, hash1, "Hash should not match original data");
        
        // Same input with same salt should produce same hash
        assertEquals(hash1, hash2, "Same input should produce same hash");
        
        // Different input should produce different hash
        String differentData = "+15559876543";
        String hash3 = securityUtils.secureHash(differentData);
        assertNotEquals(hash1, hash3, "Different input should produce different hash");
    }
    
    @Test
    public void testSimpleSHA256() {
        // Given
        String sensitiveData = "+15551234567";
        
        // When
        String hash = securityUtils.simpleSHA256(sensitiveData);
        
        // Then
        assertNotNull(hash, "Hash should not be null");
        assertNotEquals(sensitiveData, hash, "Hash should not match original data");
        
        // Different input should produce different hash
        String differentData = "+15559876543";
        String hash2 = securityUtils.simpleSHA256(differentData);
        assertNotEquals(hash, hash2, "Different input should produce different hash");
    }
} 