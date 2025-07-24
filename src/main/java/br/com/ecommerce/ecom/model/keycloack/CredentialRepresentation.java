package br.com.ecommerce.ecom.model.keycloack;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CredentialRepresentation {

    private String type;
    private String value;
    private Boolean temporary;
}