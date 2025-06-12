package br.com.ecommerce.ecom.config.exception.keycloack;

import br.com.ecommerce.ecom.config.exception.BusinessException;
import br.com.ecommerce.ecom.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public class KeycloakUserNotFoundException extends BusinessException {

    public KeycloakUserNotFoundException(String message) {
        super(message, ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
}