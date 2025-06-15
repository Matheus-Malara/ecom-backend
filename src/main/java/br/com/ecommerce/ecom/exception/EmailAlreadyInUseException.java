package br.com.ecommerce.ecom.exception;

import br.com.ecommerce.ecom.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public class EmailAlreadyInUseException extends BusinessException {

    public EmailAlreadyInUseException() {
        super("Email already in use", ErrorCode.EMAIL_ALREADY_IN_USE, HttpStatus.CONFLICT);
    }
}
