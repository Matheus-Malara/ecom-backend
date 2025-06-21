package br.com.ecommerce.ecom.exception;

import br.com.ecommerce.ecom.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public class InvalidCartOperationException extends BusinessException {
    public InvalidCartOperationException(String message) {
        super(message, ErrorCode.INVALID_CART_OPERATION, HttpStatus.BAD_REQUEST);
    }
}