package br.com.ecommerce.ecom.exception;

import br.com.ecommerce.ecom.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public class DuplicateBrandNameException extends BusinessException {

    public DuplicateBrandNameException(String name) {
        super("A brand with the name '" + name + "' already exists.",
                ErrorCode.DUPLICATE_RESOURCE,
                HttpStatus.CONFLICT);
    }
}