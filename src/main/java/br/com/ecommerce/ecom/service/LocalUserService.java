package br.com.ecommerce.ecom.service;

import br.com.ecommerce.ecom.entity.User;
import br.com.ecommerce.ecom.exception.UserNotFoundException;
import br.com.ecommerce.ecom.model.keycloack.UserRepresentation;
import br.com.ecommerce.ecom.repository.UserRepository;
import br.com.ecommerce.ecom.service.keycloack.KeycloakTokenService;
import br.com.ecommerce.ecom.client.feign.keycloack.KeycloakUserClient;
import br.com.ecommerce.ecom.util.TraceIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocalUserService {

    private static final String BEARER_PREFIX = "Bearer ";

    private final UserRepository userRepository;
    private final KeycloakUserClient keycloakUserClient;
    private final KeycloakTokenService keycloakTokenService;

    @Transactional
    public void updateUserStatusByEmail(String email, boolean active) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email '" + email + "' not found"));

        user.setActive(active);
        userRepository.save(user);

        updateKeycloakUserEnabledStatus(user.getKeycloakUserId(), active);

        log.info("[{}] User {} status set to {}", TraceIdGenerator.getTraceId(), email, active);
    }

    private void updateKeycloakUserEnabledStatus(String keycloakUserId, boolean enabled) {
        String accessToken = BEARER_PREFIX + keycloakTokenService.getAdminToken().getAccessToken();

        UserRepresentation update = new UserRepresentation();
        update.setEnabled(enabled);

        keycloakUserClient.updateUserEnabledStatus(keycloakUserId, update, accessToken);
    }
}