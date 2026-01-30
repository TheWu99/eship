package com.thewu.eship.service.dhl;

import com.thewu.eship.config.DhlApiConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;

/**
 * Service for managing DHL OAuth 2.0 authentication
 * Handles token generation and caching
 */
@Service
public class DhlOAuthService {

    private static final Logger log = LoggerFactory.getLogger(DhlOAuthService.class);

    @Autowired
    private DhlApiConfig dhlConfig;

    @Autowired
    @Qualifier("dhlRestTemplate")
    private RestTemplate restTemplate;

    private String cachedAccessToken;
    private Instant tokenExpiryTime;

    /**
     * Get OAuth 2.0 Bearer token for DHL API authentication
     * Uses cached token if available and not expired
     */
    public String getAccessToken() {
        if (cachedAccessToken != null && tokenExpiryTime != null && Instant.now().isBefore(tokenExpiryTime)) {
            log.debug("Using cached DHL access token");
            return cachedAccessToken;
        }

        log.info("Requesting new DHL OAuth token");
        return requestNewToken();
    }

    /**
     * Request a new OAuth token from DHL
     */
    private String requestNewToken() {
        try {
            // Create Basic Auth credentials
            String credentials = dhlConfig.getApiKey() + ":" + dhlConfig.getApiSecret();
            String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

            // Build headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Authorization", "Basic " + encodedCredentials);

            // Build request body
            String requestBody = "grant_type=client_credentials&scope=get:rates";

            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

            // Make token request
            ResponseEntity<Map> response = restTemplate.exchange(
                dhlConfig.getOauthUrl(),
                HttpMethod.POST,
                request,
                Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                cachedAccessToken = (String) responseBody.get("access_token");

                // Calculate expiry time (default to 1 hour if not provided)
                Integer expiresIn = (Integer) responseBody.get("expires_in");
                if (expiresIn != null) {
                    // Refresh token 5 minutes before actual expiry
                    tokenExpiryTime = Instant.now().plusSeconds(expiresIn - 300);
                } else {
                    tokenExpiryTime = Instant.now().plusSeconds(3300); // 55 minutes
                }

                log.info("Successfully obtained DHL OAuth token, expires at: {}", tokenExpiryTime);
                return cachedAccessToken;
            } else {
                log.error("Failed to obtain DHL OAuth token: {}", response.getStatusCode());
                throw new RuntimeException("Failed to obtain DHL OAuth token");
            }

        } catch (Exception e) {
            log.error("Error obtaining DHL OAuth token", e);
            throw new RuntimeException("Error obtaining DHL OAuth token: " + e.getMessage(), e);
        }
    }

    /**
     * Clear cached token (useful for testing or error recovery)
     */
    public void clearToken() {
        cachedAccessToken = null;
        tokenExpiryTime = null;
        log.info("Cleared cached DHL OAuth token");
    }

    /**
     * Create standard headers for DHL API requests
     */
    public HttpHeaders createDhlHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + getAccessToken());
        return headers;
    }
}
