package br.com.ecommerce.ecom.exception;

import br.com.ecommerce.ecom.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public class EmptyCartException extends BusinessException {
    public EmptyCartException() {
        super("Cart is empty.", ErrorCode.EMPTY_CART, HttpStatus.BAD_REQUEST);
    }
}