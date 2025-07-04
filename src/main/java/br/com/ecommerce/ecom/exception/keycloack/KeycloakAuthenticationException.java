package br.com.ecommerce.ecom.exception.keycloack;

import br.com.ecommerce.ecom.exception.BusinessException;
import br.com.ecommerce.ecom.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public class KeycloakAuthenticationException extends BusinessException {

    public KeycloakAuthenticationException(String message) {
        super(message, ErrorCode.KEYCLOAK_AUTHENTICATION_FAILED, HttpStatus.UNAUTHORIZED);
    }
}