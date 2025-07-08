package br.com.ecommerce.ecom.controller;

import br.com.ecommerce.ecom.dto.requests.LoginRequestDTO;
import br.com.ecommerce.ecom.dto.requests.RefreshTokenRequestDTO;
import br.com.ecommerce.ecom.dto.requests.RegisterUserRequestDTO;
import br.com.ecommerce.ecom.dto.requests.UpdateUserStatusRequest;
import br.com.ecommerce.ecom.dto.responses.LoginResponseDTO;
import br.com.ecommerce.ecom.dto.responses.StandardResponse;
import br.com.ecommerce.ecom.factory.ResponseFactory;
import br.com.ecommerce.ecom.service.CartService;
import br.com.ecommerce.ecom.service.LocalUserService;
import br.com.ecommerce.ecom.service.keycloack.KeycloakAdminClientService;
import br.com.ecommerce.ecom.service.keycloack.KeycloakLoginService;
import br.com.ecommerce.ecom.util.TraceIdGenerator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private static final String AUTH_BASE_PATH = "/api/auth";

    private final KeycloakAdminClientService keycloakAdminClientService;
    private final KeycloakLoginService keycloakLoginService;
    private final ResponseFactory responseFactory;
    private final LocalUserService userService;
    private final CartService cartService;

    @PostMapping("/register")
    public ResponseEntity<StandardResponse<Void>> register(@RequestBody @Valid RegisterUserRequestDTO request) {
        keycloakAdminClientService.createUser(request, "USER");
        return responseFactory.createdResponse(null, "User registered successfully", AUTH_BASE_PATH + "/register");
    }

    @PostMapping("/login")
    public ResponseEntity<StandardResponse<LoginResponseDTO>> login(
            @RequestBody @Valid LoginRequestDTO request,
            @RequestHeader(value = "X-Anonymous-Id", required = false) String anonymousId) {

        LoginResponseDTO loginResponse = keycloakLoginService.loginUser(request);

        if (anonymousId != null && !anonymousId.isBlank()) {
            userService.findByEmail(request.getEmail()).ifPresent(user -> {
                try {
                    cartService.mergeAnonymousCartIfExists(anonymousId, user);
                } catch (Exception e) {
                    log.error("[{}] Failed to merge anonymous cart on login: {}", TraceIdGenerator.getTraceId(), e.getMessage(), e);
                }
            });
        }

        return responseFactory.okResponse(loginResponse, "User logged in successfully", AUTH_BASE_PATH + "/login");
    }

    @PostMapping("/refresh")
    public ResponseEntity<StandardResponse<LoginResponseDTO>> refreshToken(
            @RequestBody @Valid RefreshTokenRequestDTO request) {

        LoginResponseDTO response = keycloakLoginService.refreshAccessToken(request.getRefreshToken());
        return responseFactory.okResponse(response, "Token refreshed successfully", AUTH_BASE_PATH + "/refresh");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/users/status")
    public ResponseEntity<StandardResponse<Void>> updateUserStatusByEmail(@RequestBody @Valid UpdateUserStatusRequest request) {
        userService.updateUserStatusByEmail(request.getEmail(), request.isActive());
        String message = request.isActive() ? "User activated" : "User deactivated";
        return responseFactory.okResponse(null, message, AUTH_BASE_PATH + "/users/status");
    }
}
