package br.com.ecommerce.ecom.controller;

import br.com.ecommerce.ecom.dto.requests.LoginRequestDTO;
import br.com.ecommerce.ecom.dto.requests.RefreshTokenRequestDTO;
import br.com.ecommerce.ecom.dto.requests.RegisterUserRequestDTO;
import br.com.ecommerce.ecom.dto.responses.LoginResponseDTO;
import br.com.ecommerce.ecom.dto.responses.StandardResponse;
import br.com.ecommerce.ecom.factory.ResponseFactory;
import br.com.ecommerce.ecom.service.CartService;
import br.com.ecommerce.ecom.service.LocalUserService;
import br.com.ecommerce.ecom.service.keycloack.KeycloakAdminClientService;
import br.com.ecommerce.ecom.service.keycloack.KeycloakLoginService;
import br.com.ecommerce.ecom.util.TraceIdGenerator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Endpoints for user registration, login, and token refresh")
public class AuthController {

    private static final String AUTH_BASE_PATH = "/api/auth";

    private final KeycloakAdminClientService keycloakAdminClientService;
    private final KeycloakLoginService keycloakLoginService;
    private final ResponseFactory responseFactory;
    private final LocalUserService userService;
    private final CartService cartService;

    @Operation(summary = "Register a new user", description = "Registers a user with the USER role")
    @PostMapping("/register")
    public ResponseEntity<StandardResponse<Void>> register(
            @RequestBody @Valid RegisterUserRequestDTO request) {
        keycloakAdminClientService.createUser(request, "USER");
        return responseFactory.createdResponse(null, "User registered successfully", AUTH_BASE_PATH + "/register");
    }

    @Operation(summary = "Login a user", description = "Authenticates a user and returns access/refresh tokens")
    @PostMapping("/login")
    public ResponseEntity<StandardResponse<LoginResponseDTO>> login(
            @RequestBody @Valid LoginRequestDTO request,
            @Parameter(description = "Anonymous ID used to track cart before login")
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

    @Operation(summary = "Refresh access token", description = "Generates a new access token using a valid refresh token")
    @PostMapping("/refresh")
    public ResponseEntity<StandardResponse<LoginResponseDTO>> refreshToken(
            @RequestBody @Valid RefreshTokenRequestDTO request) {
        LoginResponseDTO response = keycloakLoginService.refreshAccessToken(request.getRefreshToken());
        return responseFactory.okResponse(response, "Token refreshed successfully", AUTH_BASE_PATH + "/refresh");
    }
}
