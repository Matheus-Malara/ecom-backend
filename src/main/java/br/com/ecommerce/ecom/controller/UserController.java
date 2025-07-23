package br.com.ecommerce.ecom.controller;

import br.com.ecommerce.ecom.dto.filters.UserFilterDTO;
import br.com.ecommerce.ecom.dto.requests.UpdateUserRequestDTO;
import br.com.ecommerce.ecom.dto.requests.UpdateUserStatusRequest;
import br.com.ecommerce.ecom.dto.responses.CompleteUserResponseDTO;
import br.com.ecommerce.ecom.dto.responses.StandardResponse;
import br.com.ecommerce.ecom.dto.responses.UserResponseDTO;
import br.com.ecommerce.ecom.factory.ResponseFactory;
import br.com.ecommerce.ecom.service.LocalUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Endpoints for managing and querying user accounts")
public class UserController {

    private static final String USERS_BASE_PATH = "/api/users";

    private final LocalUserService userService;
    private final ResponseFactory responseFactory;

    @Operation(summary = "Get current user", description = "Returns information of the currently authenticated user")
    @GetMapping("/me")
    public ResponseEntity<StandardResponse<UserResponseDTO>> getCurrentUser() {
        UserResponseDTO dto = userService.getCurrentUserInfo();
        return responseFactory.okResponse(dto, "User info fetched", USERS_BASE_PATH + "/me");
    }

    @Operation(summary = "Update current user", description = "Updates first and last name of the current user")
    @PutMapping("/me")
    public ResponseEntity<StandardResponse<UserResponseDTO>> updateCurrentUser(
            @RequestBody @Valid UpdateUserRequestDTO dto) {
        UserResponseDTO updated = userService.updateCurrentUser(dto);
        return responseFactory.okResponse(updated, "User info updated", USERS_BASE_PATH + "/me");
    }

    @Operation(summary = "Toggle user status", description = "Activates or deactivates a user by email (admin only)")
    @PatchMapping("/{email}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponse<Void>> updateUserStatus(
            @Parameter(description = "User email", example = "user@example.com") @PathVariable String email,
            @RequestBody @Valid UpdateUserStatusRequest request) {

        userService.updateUserStatusByEmail(email, request.isActive());
        String message = request.isActive() ? "User activated" : "User deactivated";
        return responseFactory.okResponse(null, message, USERS_BASE_PATH + "/" + email + "/status");
    }

    @Operation(summary = "Get user count", description = "Returns the total number of users (admin only)")
    @GetMapping("/count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponse<Long>> getUserCount() {
        long count = userService.getUserCount();
        return responseFactory.okResponse(
                count,
                "User count retrieved successfully",
                USERS_BASE_PATH
        );
    }

    @Operation(summary = "Get user by ID", description = "Returns user details by ID (admin only)")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponse<CompleteUserResponseDTO>> getUserById(
            @Parameter(description = "User ID", example = "1") @PathVariable Long id) {
        CompleteUserResponseDTO dto = userService.getUserById(id);
        return responseFactory.okResponse(dto, "User fetched successfully", USERS_BASE_PATH + "/" + id);
    }

    @Operation(summary = "Get users with filter", description = "Returns a paginated list of users based on filter criteria (admin only)")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponse<Page<CompleteUserResponseDTO>>> getUsers(
            @Valid @ModelAttribute UserFilterDTO filter,
            @ParameterObject @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<CompleteUserResponseDTO> result = userService.getUsers(filter, pageable);
        return responseFactory.okResponse(result, "Users fetched successfully", USERS_BASE_PATH);
    }
}
