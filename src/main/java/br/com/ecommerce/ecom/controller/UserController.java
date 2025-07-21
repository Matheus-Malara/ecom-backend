package br.com.ecommerce.ecom.controller;

import br.com.ecommerce.ecom.dto.requests.UpdateUserRequestDTO;
import br.com.ecommerce.ecom.dto.requests.UpdateUserStatusRequest;
import br.com.ecommerce.ecom.dto.responses.StandardResponse;
import br.com.ecommerce.ecom.dto.responses.UserResponseDTO;
import br.com.ecommerce.ecom.factory.ResponseFactory;
import br.com.ecommerce.ecom.service.LocalUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private static final String ACCOUNT_BASE_PATH = "/api/account";

    private final LocalUserService userService;
    private final ResponseFactory responseFactory;

    @GetMapping("/me")
    public ResponseEntity<StandardResponse<UserResponseDTO>> getCurrentUser() {
        UserResponseDTO dto = userService.getCurrentUserInfo();
        return responseFactory.okResponse(dto, "User info fetched", ACCOUNT_BASE_PATH + "/me");
    }

    @PutMapping("/me")
    public ResponseEntity<StandardResponse<UserResponseDTO>> updateCurrentUser(
            @RequestBody @Valid UpdateUserRequestDTO dto) {
        UserResponseDTO updated = userService.updateCurrentUser(dto);
        return responseFactory.okResponse(updated, "User info updated", ACCOUNT_BASE_PATH + "/me");
    }

    @PatchMapping("/{email}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponse<Void>> updateUserStatus(
            @PathVariable String email,
            @RequestBody @Valid UpdateUserStatusRequest request) {

        userService.updateUserStatusByEmail(email, request.isActive());
        String message = request.isActive() ? "User activated" : "User deactivated";
        return responseFactory.okResponse(null, message, ACCOUNT_BASE_PATH + "/" + email + "/status");
    }

    @GetMapping("/count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponse<Long>> getUserCount() {
        long count = userService.getUserCount();
        return responseFactory.okResponse(
                count,
                "User count retrieved successfully",
                "/api/users/count"
        );
    }
}