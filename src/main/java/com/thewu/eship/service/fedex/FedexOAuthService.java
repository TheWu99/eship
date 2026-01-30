package com.thewu.eship.service.fedex;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thewu.eship.config.FedexApiConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

/**
 * Service for managing FedEx OAuth 2.0 authentication.
 * Handles token acquisition, caching, and automatic refresh.
 */
@Service
public class FedexOAuthService {

    private static final Logger log = LoggerFactory.getLogger(FedexOAuthService.class);

    @Autowired
    private FedexApiConfig config;

    @Autowired
    @Qualifier("fedexRestTemplate")
    private RestTemplate restTemplate;

    private String cachedAccessToken;
    private Instant tokenExpiryTime;

    /**
     * Get a valid OAuth access token.
     * Uses cached token if available and valid, otherwise requests a new one.
     */
    public String getAccessToken() {
        if (isTokenValid()) {
            log.debug("Using cached FedEx OAuth token");
            return cachedAccessToken;
        }

        log.info("Requesting new FedEx OAuth token");
        return requestNewToken();
    }

    /**
     * Check if the cached token is still valid.
     * Token is considered invalid if it doesn't exist or expires within 5 minutes.
     */
    private boolean isTokenValid() {
        if (cachedAccessToken == null || tokenExpiryTime == null) {
            return false;
        }

        // Consider token invalid if it expires in less than 5 minutes
        Instant fiveMinutesFromNow = Instant.now().plusSeconds(300);
        return tokenExpiryTime.isAfter(fiveMinutesFromNow);
    }

    /**
     * Request a new OAuth token from FedEx.
     * FedEx uses client_credentials grant type.
     */
    private String requestNewToken() {
        try {
            // Build request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            TokenRequest tokenRequest = new TokenRequest();
            tokenRequest.setGrantType("client_credentials");
            tokenRequest.setClientId(config.getClientId());
            tokenRequest.setClientSecret(config.getClientSecret());

            HttpEntity<TokenRequest> request = new HttpEntity<>(tokenRequest, headers);

            // Call FedEx OAuth endpoint
            ResponseEntity<TokenResponse> response = restTemplate.postForEntity(
                    config.getOauthUrl(),
                    request,
                    TokenResponse.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                TokenResponse tokenResponse = response.getBody();
                cachedAccessToken = tokenResponse.getAccessToken();

                // Calculate expiry time (default to 1 hour if not specified)
                int expiresIn = tokenResponse.getExpiresIn() != null ? tokenResponse.getExpiresIn() : 3600;
                tokenExpiryTime = Instant.now().plusSeconds(expiresIn);

                log.info("Successfully obtained FedEx OAuth token, expires in {} seconds", expiresIn);
                return cachedAccessToken;
            } else {
                throw new RuntimeException("Failed to obtain FedEx OAuth token: " + response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("Error obtaining FedEx OAuth token", e);
            throw new RuntimeException("Failed to authenticate with FedEx API", e);
        }
    }

    /**
     * Clear cached token (useful for testing or forced refresh)
     */
    public void clearToken() {
        log.info("Clearing cached FedEx OAuth token");
        cachedAccessToken = null;
        tokenExpiryTime = null;
    }

    /**
     * Create HTTP headers with OAuth token for FedEx API calls
     */
    public HttpHeaders createFedexHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(getAccessToken());
        return headers;
    }

    // DTOs for OAuth
    public static class TokenRequest {
        @JsonProperty("grant_type")
        private String grantType;

        @JsonProperty("client_id")
        private String clientId;

        @JsonProperty("client_secret")
        private String clientSecret;

        public String getGrantType() {
            return grantType;
        }

        public void setGrantType(String grantType) {
            this.grantType = grantType;
        }

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public void setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
        }
    }

    public static class TokenResponse {
        @JsonProperty("access_token")
        private String accessToken;

        @JsonProperty("token_type")
        private String tokenType;

        @JsonProperty("expires_in")
        private Integer expiresIn;

        @JsonProperty("scope")
        private String scope;

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public String getTokenType() {
            return tokenType;
        }

        public void setTokenType(String tokenType) {
            this.tokenType = tokenType;
        }

        public Integer getExpiresIn() {
            return expiresIn;
        }

        public void setExpiresIn(Integer expiresIn) {
            this.expiresIn = expiresIn;
        }

        public String getScope() {
            return scope;
        }

        public void setScope(String scope) {
            this.scope = scope;
        }
    }
}
