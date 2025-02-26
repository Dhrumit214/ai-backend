package aifriend.ai_backend.config;

import aifriend.ai_backend.repository.*;
import aifriend.ai_backend.service.*;
import aifriend.ai_backend.util.SecurityUtils;
import okhttp3.*;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Test configuration providing mock beans for the test environment
 */
@TestConfiguration
@Profile("test")
public class TestConfig {

    @Bean
    @Primary
    @ConditionalOnMissingBean
    public TwilioService twilioService() {
        TwilioService mockTwilioService = Mockito.mock(TwilioService.class);
        // Configure mock behavior if needed
        Mockito.doNothing().when(mockTwilioService).sendSms(Mockito.anyString(), Mockito.anyString());
        return mockTwilioService;
    }
    
    @Bean
    @Primary
    @ConditionalOnMissingBean
    public MessageService messageService() {
        return Mockito.mock(MessageService.class);
    }
    
    @Bean
    @Primary
    @ConditionalOnMissingBean
    public BillingService billingService() {
        return Mockito.mock(BillingService.class);
    }
    
    @Bean
    @Primary
    @ConditionalOnMissingBean
    public UserService userService() {
        return Mockito.mock(UserService.class);
    }
    
    @Bean
    @Primary
    @ConditionalOnMissingBean
    public SecurityUtils securityUtils() {
        SecurityUtils mockSecurityUtils = Mockito.mock(SecurityUtils.class);
        Mockito.when(mockSecurityUtils.encrypt(Mockito.anyString())).thenReturn("encrypted");
        Mockito.when(mockSecurityUtils.decrypt(Mockito.anyString())).thenReturn("decrypted");
        Mockito.when(mockSecurityUtils.simpleSHA256(Mockito.anyString())).thenReturn("hashed");
        return mockSecurityUtils;
    }
    
    /**
     * Provides a test version of OkHttpClient with mock response behavior
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean
    public OkHttpClient okHttpClient() {
        OkHttpClient mockClient = Mockito.mock(OkHttpClient.class);
        Call mockCall = Mockito.mock(Call.class);
        Response mockResponse = new Response.Builder()
            .request(new Request.Builder().url("https://api.example.com").build())
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .body(ResponseBody.create(
                MediaType.parse("application/json"),
                "{\"choices\":[{\"message\":{\"content\":\"This is a test response\"}}]}"
            ))
            .build();
            
        try {
            Mockito.when(mockCall.execute()).thenReturn(mockResponse);
        } catch (IOException e) {
            // This won't happen in the mock setup
        }
        
        Mockito.when(mockClient.newCall(Mockito.any())).thenReturn(mockCall);
        return mockClient;
    }
    
    /**
     * Mock version of OpenAI API key for testing
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean(name = "openaiApiKey")
    public String openaiApiKey() {
        return "test-api-key";
    }
    
    /**
     * Mock version of Twilio account SID for testing
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean(name = "twilioAccountSid")
    public String twilioAccountSid() {
        return "test-account-sid";
    }
    
    /**
     * Mock version of Twilio auth token for testing
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean(name = "twilioAuthToken")
    public String twilioAuthToken() {
        return "test-auth-token";
    }
    
    /**
     * Mock version of Twilio phone number for testing
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean(name = "twilioPhoneNumber")
    public String twilioPhoneNumber() {
        return "+15555555555";
    }
    
    // Add mock repositories
    
    @Bean
    @Primary
    @ConditionalOnMissingBean
    public MessageRepository messageRepository() {
        return Mockito.mock(MessageRepository.class);
    }
    
    @Bean
    @Primary
    @ConditionalOnMissingBean
    public UserRepository userRepository() {
        return Mockito.mock(UserRepository.class);
    }
    
    @Bean
    @Primary
    @ConditionalOnMissingBean
    public PersonaRepository personaRepository() {
        return Mockito.mock(PersonaRepository.class);
    }
    
    @Bean
    @Primary
    @ConditionalOnMissingBean
    public UserPreferencesRepository userPreferencesRepository() {
        return Mockito.mock(UserPreferencesRepository.class);
    }
    
    @Bean
    @Primary
    @ConditionalOnMissingBean
    public MessageLimitsRepository messageLimitsRepository() {
        return Mockito.mock(MessageLimitsRepository.class);
    }
    
    @Bean
    @Primary
    @ConditionalOnMissingBean
    public BillingRecordRepository billingRecordRepository() {
        return Mockito.mock(BillingRecordRepository.class);
    }
    
    @Bean
    @Primary
    @ConditionalOnMissingBean(name = "billingRatePerMessage")
    public BigDecimal billingRatePerMessage() {
        return new BigDecimal("0.018");
    }
    
    @Bean
    @Primary
    @ConditionalOnMissingBean
    public StripeService stripeService() {
        return Mockito.mock(StripeService.class);
    }
    
    @Bean
    @Primary
    @ConditionalOnMissingBean(name = "stripeApiKey")
    public String stripeApiKey() {
        return "test-stripe-api-key";
    }
    
    @Bean
    @Primary
    @ConditionalOnMissingBean(name = "stripeWebhookSecret")
    public String stripeWebhookSecret() {
        return "test-stripe-webhook-secret";
    }
}