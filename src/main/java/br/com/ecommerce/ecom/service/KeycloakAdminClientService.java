package br.com.ecommerce.ecom.service;

import br.com.ecommerce.ecom.client.feign.KeycloakAdminUserClient;
import br.com.ecommerce.ecom.client.feign.KeycloakTokenClient;
import br.com.ecommerce.ecom.dto.RegisterRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakAdminClientService {

    private final KeycloakTokenClient keycloakTokenClient;
    private final KeycloakAdminUserClient keycloakAdminUserClient;

    @Value("${keycloak.client-id}")
    private String keycloakClientId;

    @Value("${keycloak.client-secret}")
    private String keycloakClientSecret;

    public void createUser(RegisterRequestDTO request) {

        // Step 1 - Get access token using client_credentials
        Map<String, Object> tokenResponse = keycloakTokenClient.getToken(
                "client_credentials",
                keycloakClientId,
                keycloakClientSecret
        );

        String accessToken = (String) tokenResponse.get("access_token");

        // Step 2 - Build user payload
        Map<String, Object> userPayload = Map.of(
                "username", request.getUsername(),
                "enabled", true,
                "firstName", request.getFirstName(),
                "lastName", request.getLastName(),
                "email", request.getEmail(),
                "credentials", Collections.singletonList(
                        Map.of(
                                "type", "password",
                                "value", request.getPassword(),
                                "temporary", false
                        )
                )
        );

        // Step 3 - Call Admin API to create user
        keycloakAdminUserClient.createUser("Bearer " + accessToken, userPayload);

        log.info("User created successfully in Keycloak: {}", request.getUsername());
    }
}