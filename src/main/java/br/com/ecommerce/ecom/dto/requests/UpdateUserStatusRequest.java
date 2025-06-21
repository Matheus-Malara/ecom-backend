package br.com.ecommerce.ecom.dto.requests;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UpdateUserStatusRequest {
    @Email
    private String email;
    private boolean active;
}