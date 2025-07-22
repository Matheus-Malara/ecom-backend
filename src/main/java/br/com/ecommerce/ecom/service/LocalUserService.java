package br.com.ecommerce.ecom.service;

import br.com.ecommerce.ecom.client.feign.keycloack.KeycloakUserClient;
import br.com.ecommerce.ecom.dto.filters.UserFilterDTO;
import br.com.ecommerce.ecom.dto.requests.UpdateUserRequestDTO;
import br.com.ecommerce.ecom.dto.responses.CompleteUserResponseDTO;
import br.com.ecommerce.ecom.dto.responses.UserResponseDTO;
import br.com.ecommerce.ecom.entity.User;
import br.com.ecommerce.ecom.exception.UserNotFoundException;
import br.com.ecommerce.ecom.mappers.UserMapper;
import br.com.ecommerce.ecom.model.keycloack.UserRepresentation;
import br.com.ecommerce.ecom.repository.UserRepository;
import br.com.ecommerce.ecom.service.keycloack.KeycloakTokenService;
import br.com.ecommerce.ecom.specification.UserSpecification;
import br.com.ecommerce.ecom.util.TraceIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocalUserService {

    private static final String BEARER_PREFIX = "Bearer ";

    private final UserRepository userRepository;
    private final KeycloakUserClient keycloakUserClient;
    private final KeycloakTokenService keycloakTokenService;
    private final UserMapper userMapper;

    @Transactional
    public void updateUserStatusByEmail(String email, boolean active) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email '" + email + "' not found"));

        user.setActive(active);
        userRepository.save(user);

        updateKeycloakUserEnabledStatus(user.getKeycloakUserId(), active);

        log.info("[{}] User {} status set to {}", TraceIdGenerator.getTraceId(), email, active);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found"));
    }

    private void updateKeycloakUserEnabledStatus(String keycloakUserId, boolean enabled) {
        String accessToken = BEARER_PREFIX + keycloakTokenService.getAdminToken().getAccessToken();

        UserRepresentation update = new UserRepresentation();
        update.setEnabled(enabled);

        keycloakUserClient.updateUserEnabledStatus(keycloakUserId, update, accessToken);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public UserResponseDTO getCurrentUserInfo() {
        String keycloakId = getCurrentKeycloakUserId();
        User user = userRepository.findByKeycloakUserId(keycloakId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponseDTO updateCurrentUser(UpdateUserRequestDTO dto) {
        String keycloakId = getCurrentKeycloakUserId();
        User user = userRepository.findByKeycloakUserId(keycloakId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        userMapper.updateUserFromDto(dto, user);
        return userMapper.toResponse(userRepository.save(user));
    }

    private String getCurrentKeycloakUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public long getUserCount() {
        return userRepository.count();
    }

    public CompleteUserResponseDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toCompleteUserResponse)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public Page<CompleteUserResponseDTO> getUsers(UserFilterDTO filter, Pageable pageable) {
        Specification<User> spec = UserSpecification.build(filter);
        return userRepository.findAll(spec, pageable)
                .map(userMapper::toCompleteUserResponse);
    }

}