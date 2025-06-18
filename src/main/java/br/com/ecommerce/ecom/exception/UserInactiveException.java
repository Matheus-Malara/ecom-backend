package br.com.ecommerce.ecom.exception;

import br.com.ecommerce.ecom.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public class UserInactiveException extends BusinessException {

    public UserInactiveException() {
        super("User is inactive", ErrorCode.USER_INACTIVE, HttpStatus.FORBIDDEN);
    }
}