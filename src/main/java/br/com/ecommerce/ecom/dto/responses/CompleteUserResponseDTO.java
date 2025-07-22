package br.com.ecommerce.ecom.dto.responses;

import lombok.Data;

@Data
public class CompleteUserResponseDTO {
    private Long id;
    private String keycloakUserId;
    private String email;
    private String firstName;
    private String lastName;
    private boolean active;
}