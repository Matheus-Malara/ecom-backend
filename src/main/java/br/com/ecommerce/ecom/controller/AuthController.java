package br.com.ecommerce.ecom.controller;

import br.com.ecommerce.ecom.dto.requests.LoginRequestDTO;
import br.com.ecommerce.ecom.dto.requests.RegisterUserRequestDTO;
import br.com.ecommerce.ecom.dto.responses.ApiResponse;
import br.com.ecommerce.ecom.dto.responses.LoginResponseDTO;
import br.com.ecommerce.ecom.factory.ResponseFactory;
import br.com.ecommerce.ecom.service.LocalUserService;
import br.com.ecommerce.ecom.service.keycloack.KeycloakAdminClientService;
import br.com.ecommerce.ecom.service.keycloack.KeycloakLoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final KeycloakAdminClientService keycloakAdminClientService;
    private final KeycloakLoginService keycloakLoginService;
    private final ResponseFactory responseFactory;
    private final LocalUserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@RequestBody @Valid RegisterUserRequestDTO request, HttpServletRequest httpRequest) {

        keycloakAdminClientService.createUser(request, "USER");

        return responseFactory.okResponse(null, "User registered successfully", httpRequest.getRequestURI());
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(@RequestBody @Valid LoginRequestDTO request, HttpServletRequest httpRequest) {

        LoginResponseDTO loginResponse = keycloakLoginService.loginUser(request);

        return responseFactory.okResponse(loginResponse, "User logged in successfully", httpRequest.getRequestURI());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/users/status")
    public ResponseEntity<ApiResponse<Void>> updateUserStatusByEmail(@RequestParam String email,
                                                                     @RequestParam boolean active,
                                                                     HttpServletRequest request) {
        userService.updateUserStatusByEmail(email, active);
        String message = active ? "User activated" : "User deactivated";
        return responseFactory.okResponse(null, message, request.getRequestURI());
    }
}
