package br.com.ecommerce.ecom.exception;

import br.com.ecommerce.ecom.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public class InvalidOrderStatusException extends BusinessException {
    public InvalidOrderStatusException(String message) {
        super(message, ErrorCode.INVALID_ORDER_STATUS, HttpStatus.BAD_REQUEST);
    }
}
