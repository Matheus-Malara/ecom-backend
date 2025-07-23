package br.com.ecommerce.ecom.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Payload to activate or deactivate a user")
public class UpdateUserStatusRequest {

    @Schema(description = "True to activate, false to deactivate", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean active;
}
