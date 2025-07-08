package br.com.ecommerce.ecom.dto.responses;

import lombok.Data;

@Data
public class UserResponseDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
}