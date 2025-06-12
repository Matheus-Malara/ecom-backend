package br.com.ecommerce.ecom.service.keycloack;

import br.com.ecommerce.ecom.client.feign.keycloack.KeycloakUserClient;
import br.com.ecommerce.ecom.config.exception.BusinessException;
import br.com.ecommerce.ecom.config.exception.EmailAlreadyInUseException;
import br.com.ecommerce.ecom.config.exception.keycloack.KeycloakAuthenticationException;
import br.com.ecommerce.ecom.config.exception.keycloack.KeycloakUserNotFoundException;
import br.com.ecommerce.ecom.config.keycloack.KeycloakProperties;
import br.com.ecommerce.ecom.dto.requests.RegisterUserRequestDTO;
import br.com.ecommerce.ecom.dto.responses.KeycloakTokenResponse;
import br.com.ecommerce.ecom.enums.ErrorCode;
import br.com.ecommerce.ecom.model.keycloack.CredentialRepresentation;
import br.com.ecommerce.ecom.model.keycloack.RoleRepresentation;
import br.com.ecommerce.ecom.model.keycloack.UserRepresentation;
import br.com.ecommerce.ecom.util.TraceIdGenerator;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakAdminClientService {
    private static final String PASSWORD_TYPE = "password";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String TOKEN_ENDPOINT = "/protocol/openid-connect/token";
    private static final String GRANT_TYPE = "client_credentials";

    private final KeycloakUserClient keycloakUserClient;
    private final KeycloakProperties keycloakProperties;
    private final RestTemplate restTemplate;

    public void createUser(RegisterUserRequestDTO request, String roleName) {
        log.info("[{}] Starting user registration: {}", TraceIdGenerator.getTraceId(), request.getEmail());

        String accessToken = createAuthorizationHeader(getAdminToken());
        UserRepresentation user = createUserRepresentation(request);

        String userId = createKeycloakUser(user, accessToken);
        setUserPassword(userId, request.getPassword(), accessToken);
        assignUserRole(userId, roleName, accessToken);

        log.info("[{}] User registration completed: {}", TraceIdGenerator.getTraceId(), request.getEmail());
    }

    private UserRepresentation createUserRepresentation(RegisterUserRequestDTO request) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.getEmail());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEnabled(true);
        user.setEmailVerified(true);
        return user;
    }

    private String createKeycloakUser(UserRepresentation user, String accessToken) {
        try {
            keycloakUserClient.createUser(user, accessToken);
            log.info("[{}] User {} created in Keycloak", TraceIdGenerator.getTraceId(), user.getEmail());

            return findCreatedUserId(user.getEmail(), accessToken);
        } catch (FeignException.Conflict ex) {
            throw new EmailAlreadyInUseException();
        } catch (FeignException ex) {
            log.error("[{}] Error calling Keycloak to create user {}: {}", TraceIdGenerator.getTraceId(), user.getEmail(), ex.getMessage(), ex);
            throw new BusinessException(
                    "Error calling Keycloak to create user: " + ex.getMessage(),
                    ErrorCode.KEYCLOAK_CREATE_USER_FAILED,
                    HttpStatus.valueOf(ex.status())
            );
        }
    }

    private String findCreatedUserId(String email, String accessToken) {
        return keycloakUserClient.getUsers(email, accessToken).stream()
                .filter(u -> u.getUsername().equals(email))
                .findFirst()
                .map(UserRepresentation::getId)
                .orElseThrow(() -> {
                    log.error("[{}] User {} not found after creation in Keycloak!", TraceIdGenerator.getTraceId(), email);
                    return new KeycloakUserNotFoundException("User not found after creation");
                });
    }

    private void setUserPassword(String userId, String password, String accessToken) {
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(PASSWORD_TYPE);
        passwordCred.setValue(password);

        keycloakUserClient.resetPassword(userId, passwordCred, accessToken);
        log.info("[{}] Password set for user ID {}", TraceIdGenerator.getTraceId(), userId);
    }

    private void assignUserRole(String userId, String roleName, String accessToken) {
        RoleRepresentation role = keycloakUserClient.getRoleByName(roleName, accessToken);
        keycloakUserClient.addRealmRole(userId, List.of(role), accessToken);
        log.info("[{}] Role {} assigned to user ID {}", TraceIdGenerator.getTraceId(), roleName, userId);
    }

    private String createAuthorizationHeader(KeycloakTokenResponse tokenResponse) {
        return BEARER_PREFIX + tokenResponse.getAccessToken();
    }

    private KeycloakTokenResponse getAdminToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = createTokenRequestForm();
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);

        return executeTokenRequest(entity);
    }

    private MultiValueMap<String, String> createTokenRequestForm() {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", GRANT_TYPE);
        form.add("client_id", keycloakProperties.getClientId());
        form.add("client_secret", keycloakProperties.getClientSecret());
        return form;
    }

    private KeycloakTokenResponse executeTokenRequest(HttpEntity<MultiValueMap<String, String>> entity) {
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