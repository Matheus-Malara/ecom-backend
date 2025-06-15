package br.com.ecommerce.ecom.model.keycloack;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRepresentation {

    private String id;
    private String username;
    private String email;
    private Boolean emailVerified;
    private Boolean enabled;
    private String firstName;
    private String lastName;

    private Map<String, List<String>> attributes;
}