package br.com.ecommerce.ecom.model.keycloack;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CredentialRepresentation {

    private String type; // sempre "password"
    private String value; // a senha desejada
    private Boolean temporary; // se for true, o usuário é forçado a mudar a senha no primeiro login
}