package aifriend.ai_backend.config;

import aifriend.ai_backend.service.TwilioService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public TwilioService twilioService() {
        TwilioService mockTwilioService = Mockito.mock(TwilioService.class);
        // Configure mock behavior if needed
        Mockito.doNothing().when(mockTwilioService).sendSms(Mockito.anyString(), Mockito.anyString());
        return mockTwilioService;
    }
}