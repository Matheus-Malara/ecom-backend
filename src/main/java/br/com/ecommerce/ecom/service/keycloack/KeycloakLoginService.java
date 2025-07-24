package br.com.ecommerce.ecom.service.keycloack;

import br.com.ecommerce.ecom.config.keycloack.KeycloakProperties;
import br.com.ecommerce.ecom.dto.requests.LoginRequestDTO;
import br.com.ecommerce.ecom.dto.responses.KeycloakTokenResponse;
import br.com.ecommerce.ecom.dto.responses.LoginResponseDTO;
import br.com.ecommerce.ecom.entity.User;
import br.com.ecommerce.ecom.enums.ErrorCode;
import br.com.ecommerce.ecom.exception.BusinessException;
import br.com.ecommerce.ecom.exception.UserInactiveException;
import br.com.ecommerce.ecom.exception.keycloack.KeycloakAuthenticationException;
import br.com.ecommerce.ecom.repository.UserRepository;
import br.com.ecommerce.ecom.util.TraceIdGenerator;
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
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakLoginService {
    private static final String TOKEN_ENDPOINT = "/protocol/openid-connect/token";
    private static final String GRANT_TYPE_PASSWORD = "password";
    private static final String GRANT_TYPE_PARAM = "grant_type";
    private static final String CLIENT_ID_PARAM = "client_id";
    private static final String CLIENT_SECRET_PARAM = "client_secret";
    private static final String USERNAME_PARAM = "username";
    private static final String PASSWORD_PARAM = "password";
    private static final String REALM_PATH = "/realms/";

    private final UserRepository userRepository;
    private final KeycloakProperties keycloakProperties;
    private final RestTemplate restTemplate;

    public LoginResponseDTO loginUser(LoginRequestDTO request) {
        checkUserActive(request);

        HttpEntity<MultiValueMap<String, String>> requestEntity = createRequestEntity(request);
        String tokenUrl = buildTokenUrl();

        try {
            ResponseEntity<KeycloakTokenResponse> response = executeTokenRequest(tokenUrl, requestEntity);
            return processResponse(response, request.getEmail());
        } catch (HttpStatusCodeException ex) {
            return handleHttpStatusException(request.getEmail(), ex);
        } catch (Exception ex) {
            handleLoginError(request.getEmail(), ex);
            throw new KeycloakAuthenticationException("Failed to login via Keycloak: " + ex.getMessage());
        }
    }

    public LoginResponseDTO refreshAccessToken(String refreshToken) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add(GRANT_TYPE_PARAM, "refresh_token");
        form.add("refresh_token", refreshToken);
        form.add(CLIENT_ID_PARAM, keycloakProperties.getClientId());
        form.add(CLIENT_SECRET_PARAM, keycloakProperties.getClientSecret());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);

        String tokenUrl = buildTokenUrl();

        try {
            ResponseEntity<KeycloakTokenResponse> response = executeTokenRequest(tokenUrl, entity);
            return processResponse(response, "refresh_token");
        } catch (HttpStatusCodeException ex) {
            throw new BusinessException("Invalid refresh token", ErrorCode.INVALID_CREDENTIALS, HttpStatus.UNAUTHORIZED);
        } catch (Exception ex) {
            throw new KeycloakAuthenticationException("Failed to refresh token: " + ex.getMessage());
        }
    }

    private void checkUserActive(LoginRequestDTO request) {
        Optional<User> user = userRepository.findByEmail(request.getEmail());
        if (user.isPresent() && !user.get().isActive()) {
            log.warn("[{}] Login attempt for inactive user {}", TraceIdGenerator.getTraceId(), request.getEmail());
            throw new UserInactiveException();
        }
    }

    private HttpEntity<MultiValueMap<String, String>> createRequestEntity(LoginRequestDTO request) {
        return new HttpEntity<>(createRequestBody(request), createHeaders());
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return headers;
    }

    private MultiValueMap<String, String> createRequestBody(LoginRequestDTO request) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add(GRANT_TYPE_PARAM, GRANT_TYPE_PASSWORD);
        form.add(CLIENT_ID_PARAM, keycloakProperties.getClientId());
        form.add(CLIENT_SECRET_PARAM, keycloakProperties.getClientSecret());
        form.add(USERNAME_PARAM, request.getEmail());
        form.add(PASSWORD_PARAM, request.getPassword());
        return form;
    }

    private String buildTokenUrl() {
        return keycloakProperties.getUrl() + REALM_PATH + keycloakProperties.getRealm() + TOKEN_ENDPOINT;
    }

    private ResponseEntity<KeycloakTokenResponse> executeTokenRequest(String tokenUrl,
                                                                      HttpEntity<MultiValueMap<String, String>> entity) {
        return restTemplate.exchange(
                tokenUrl,
                HttpMethod.POST,
                entity,
                KeycloakTokenResponse.class
        );
    }

    private LoginResponseDTO processResponse(ResponseEntity<KeycloakTokenResponse> response, String email) {
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            log.error("[{}] Failed to login user {}: {}", TraceIdGenerator.getTraceId(),
                    email, response.getStatusCode());
            throw new BusinessException("Invalid credentials", ErrorCode.INVALID_CREDENTIALS,
                    HttpStatus.UNAUTHORIZED);
        }

        log.info("[{}] User {} logged in successfully", TraceIdGenerator.getTraceId(), email);
        KeycloakTokenResponse token = response.getBody();
        return convertToLoginResponse(token);
    }

    private LoginResponseDTO convertToLoginResponse(KeycloakTokenResponse token) {
        return LoginResponseDTO.builder()
                .accessToken(token.getAccessToken())
                .refreshToken(token.getRefreshToken())
                .expiresIn(token.getExpiresIn())
                .build();
    }

    private LoginResponseDTO handleHttpStatusException(String email, HttpStatusCodeException ex) {
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());


        if (status == HttpStatus.UNAUTHORIZED || status == HttpStatus.BAD_REQUEST) {
            log.warn("[{}] Invalid credentials for user {}: {}", TraceIdGenerator.getTraceId(), email, status);
            throw new BusinessException("Invalid credentials", ErrorCode.INVALID_CREDENTIALS, HttpStatus.UNAUTHORIZED);
        } else {
            log.error("[{}] Keycloak authentication failed for user {}: {}", TraceIdGenerator.getTraceId(), email, status);
            throw new KeycloakAuthenticationException("Failed to login via Keycloak: " + status);
        }
    }

    private void handleLoginError(String email, Exception ex) {
        log.error("[{}] Exception while logging in user {}: {}",
                TraceIdGenerator.getTraceId(), email, ex.getMessage(), ex);
    }
}
