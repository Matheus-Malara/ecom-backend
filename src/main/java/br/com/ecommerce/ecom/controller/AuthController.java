package br.com.ecommerce.ecom.controller;

import br.com.ecommerce.ecom.dto.RegisterRequestDTO;
import br.com.ecommerce.ecom.service.KeycloakAdminClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final KeycloakAdminClientService keycloakAdminClientService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequestDTO request) {
        try {
            keycloakAdminClientService.createUser(request);
            log.info("User {} registered successfully", request.getUsername());
            return ResponseEntity.ok("User registered successfully");
        } catch (Exception e) {
            log.error("Error registering user {}: {}", request.getUsername(), e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error registering user: " + e.getMessage());
        }
    }
}
