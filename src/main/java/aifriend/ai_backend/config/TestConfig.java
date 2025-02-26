package aifriend.ai_backend.config;

import okhttp3.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * Test configuration providing mock beans for the test environment
 */
@Configuration
@Profile("test")
public class TestConfig {

    /**
     * Provides a test version of OkHttpClient
     */
    @Bean
    @Primary
    public OkHttpClient okHttpClient() {
        // Simple client for testing - no special configuration needed
        return new OkHttpClient();
    }
} 