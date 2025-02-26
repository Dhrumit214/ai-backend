package aifriend.ai_backend.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

@Component
public class SecurityUtils {

    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;
    private static final String HASH_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int HASH_ITERATIONS = 65536;
    private static final int HASH_KEY_LENGTH = 256;
    
    @Value("${security.encryption.key:fallbackKey1234567890123456}")
    private String encryptionKey;
    
    @Value("${security.salt:fallbackSalt1234567890123456}")
    private String hashSalt;
    
    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Encrypts data using AES-GCM with a randomly generated IV
     */
    public String encrypt(String data) {
        try {
            // Generate a random IV
            byte[] iv = new byte[GCM_IV_LENGTH];
            secureRandom.nextBytes(iv);
            
            // Create GCM parameters
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            
            // Initialize cipher
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            SecretKey secretKey = generateAESKey(encryptionKey);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
            
            // Encrypt
            byte[] encryptedData = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            
            // Combine IV and encrypted data and encode as Base64
            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + encryptedData.length);
            byteBuffer.put(iv);
            byteBuffer.put(encryptedData);
            
            return Base64.getEncoder().encodeToString(byteBuffer.array());
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting data", e);
        }
    }

    /**
     * Decrypts data that was encrypted with the encrypt method
     */
    public String decrypt(String encryptedData) {
        try {
            // Decode from Base64
            byte[] decodedData = Base64.getDecoder().decode(encryptedData);
            
            // Extract IV
            ByteBuffer byteBuffer = ByteBuffer.wrap(decodedData);
            byte[] iv = new byte[GCM_IV_LENGTH];
            byteBuffer.get(iv);
            
            // Extract encrypted data
            byte[] cipherText = new byte[byteBuffer.remaining()];
            byteBuffer.get(cipherText);
            
            // Initialize cipher for decryption
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            SecretKey secretKey = generateAESKey(encryptionKey);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);
            
            // Decrypt
            byte[] decryptedData = cipher.doFinal(cipherText);
            return new String(decryptedData, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting data", e);
        }
    }

    /**
     * Creates a secure hash for sensitive data like phone numbers
     * Uses PBKDF2 with SHA-256 for high security
     */
    public String secureHash(String data) {
        try {
            // Convert the string salt to bytes
            byte[] salt = hashSalt.getBytes(StandardCharsets.UTF_8);
            
            // Create the key spec with PBKDF2
            KeySpec spec = new PBEKeySpec(
                data.toCharArray(),
                salt,
                HASH_ITERATIONS,
                HASH_KEY_LENGTH
            );
            
            // Generate the hash
            SecretKeyFactory factory = SecretKeyFactory.getInstance(HASH_ALGORITHM);
            byte[] hash = factory.generateSecret(spec).getEncoded();
            
            // Return as Base64 string
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error hashing data", e);
        }
    }
    
    /**
     * Fallback to simple SHA-256 hashing if PBKDF2 is not available
     */
    public String simpleSHA256(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(
                (data + hashSalt).getBytes(StandardCharsets.UTF_8)
            );
            return bytesToHex(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
    
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
    
    /**
     * Generates a valid AES key from a string
     * AES requires keys of exactly 16, 24, or 32 bytes (128, 192, or 256 bits)
     */
    private SecretKey generateAESKey(String keyString) {
        try {
            // Use SHA-256 to get a consistent 32-byte key
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = digest.digest(keyString.getBytes(StandardCharsets.UTF_8));
            
            // Use first 32 bytes (256 bits) for AES-256
            return new SecretKeySpec(keyBytes, "AES");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Could not generate AES key", e);
        }
    }
} 