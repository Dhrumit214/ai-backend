package aifriend.ai_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
            .defaultContentType(MediaType.TEXT_HTML)
            .mediaType("html", MediaType.TEXT_HTML)
            .mediaType("css", MediaType.valueOf("text/css"))
            .mediaType("js", MediaType.valueOf("application/javascript"));
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
            .addResourceLocations("classpath:/static/")
            .setCachePeriod(3600)
            .resourceChain(true);
    }
}