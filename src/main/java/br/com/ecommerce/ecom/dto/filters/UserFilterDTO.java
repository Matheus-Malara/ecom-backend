package br.com.ecommerce.ecom.dto.filters;

import lombok.Data;

@Data
public class UserFilterDTO {
    private String email;
    private String firstName;
    private String lastName;
    private Boolean active;
}