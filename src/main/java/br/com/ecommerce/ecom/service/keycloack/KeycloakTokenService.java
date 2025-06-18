package br.com.ecommerce.ecom.service.keycloack;

import br.com.ecommerce.ecom.config.keycloack.KeycloakProperties;
import br.com.ecommerce.ecom.dto.responses.KeycloakTokenResponse;
import br.com.ecommerce.ecom.exception.keycloack.KeycloakAuthenticationException;
import br.com.ecommerce.ecom.util.TraceIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakTokenService {

    private static final String TOKEN_ENDPOINT = "/protocol/openid-connect/token";
    private final KeycloakProperties keycloakProperties;
    private final RestTemplate restTemplate;

    public KeycloakTokenResponse getAdminToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        form.add("client_id", keycloakProperties.getClientId());
        form.add("client_secret", keycloakProperties.getClientSecret());

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);
        String tokenUrl = keycloakProperties.getUrl() + "/realms/" + keycloakProperties.getRealm() + TOKEN_ENDPOINT;

        try {
            ResponseEntity<KeycloakTokenResponse> response = restTemplate.exchange(
                    tokenUrl,
                    HttpMethod.POST,
                    entity,
                    KeycloakTokenResponse.class
            );

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                log.error("[{}] Failed to get admin token from Keycloak: {}", TraceIdGenerator.getTraceId(), response.getStatusCode());
                throw new KeycloakAuthenticationException("Failed to get admin token from Keycloak: " + response.getStatusCode());
            }

            log.info("[{}] Successfully obtained admin token from Keycloak", TraceIdGenerator.getTraceId());
            return response.getBody();
        } catch (Exception ex) {
            log.error("[{}] Exception while getting admin token from Keycloak: {}", TraceIdGenerator.getTraceId(), ex.getMessage(), ex);
            throw new KeycloakAuthenticationException("Exception while getting admin token from Keycloak: " + ex.getMessage());
        }
    }
}