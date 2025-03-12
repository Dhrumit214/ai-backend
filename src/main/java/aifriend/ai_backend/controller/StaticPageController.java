package aifriend.ai_backend.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Controller for serving static HTML pages with proper content type
 */
@Controller
public class StaticPageController {

    /**
     * Serves the privacy policy page
     */
    @GetMapping(value = "/privacy-policy", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String privacyPolicy() throws IOException {
        Resource resource = new ClassPathResource("static/privacy-policy.html");
        try (InputStream inputStream = resource.getInputStream()) {
            return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        }
    }

    /**
     * Serves the terms and conditions page
     */
    @GetMapping(value = "/terms-conditions", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String termsConditions() throws IOException {
        Resource resource = new ClassPathResource("static/terms-conditions.html");
        try (InputStream inputStream = resource.getInputStream()) {
            return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        }
    }
}