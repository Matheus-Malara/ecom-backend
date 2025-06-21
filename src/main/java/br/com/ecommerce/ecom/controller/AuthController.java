package br.com.ecommerce.ecom.controller;

import br.com.ecommerce.ecom.dto.requests.LoginRequestDTO;
import br.com.ecommerce.ecom.dto.requests.RegisterUserRequestDTO;
import br.com.ecommerce.ecom.dto.requests.UpdateUserStatusRequest;
import br.com.ecommerce.ecom.dto.responses.ApiResponse;
import br.com.ecommerce.ecom.dto.responses.LoginResponseDTO;
import br.com.ecommerce.ecom.factory.ResponseFactory;
import br.com.ecommerce.ecom.service.LocalUserService;
import br.com.ecommerce.ecom.service.keycloack.KeycloakAdminClientService;
import br.com.ecommerce.ecom.service.keycloack.KeycloakLoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@RequestBody @Valid RegisterUserRequestDTO request) {
        keycloakAdminClientService.createUser(request, "USER");
        return responseFactory.createdResponse(null, "User registered successfully", AUTH_BASE_PATH + "/register");
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(@RequestBody @Valid LoginRequestDTO request) {
        LoginResponseDTO loginResponse = keycloakLoginService.loginUser(request);
        return responseFactory.okResponse(loginResponse, "User logged in successfully", AUTH_BASE_PATH + "/login");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/users/status")
    public ResponseEntity<ApiResponse<Void>> updateUserStatusByEmail(@RequestBody @Valid UpdateUserStatusRequest request) {
        userService.updateUserStatusByEmail(request.getEmail(), request.isActive());
        String message = request.isActive() ? "User activated" : "User deactivated";
        return responseFactory.okResponse(null, message, AUTH_BASE_PATH + "/users/status");
    }
}
