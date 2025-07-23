package br.com.ecommerce.ecom.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Response containing login token information")
public class LoginResponseDTO {

    @Schema(description = "Access token for authentication", example = "eyJhbGciOi...")
    private String accessToken;

    @Schema(description = "Refresh token for renewing access", example = "eyJhbGciOi...")
    private String refreshToken;

    @Schema(description = "Token expiration time in seconds", example = "3600")
    private int expiresIn;
}
