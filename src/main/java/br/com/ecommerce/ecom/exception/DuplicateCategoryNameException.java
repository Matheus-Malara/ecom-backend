package br.com.ecommerce.ecom.exception;

import br.com.ecommerce.ecom.enums.ErrorCode;
import org.springframework.http.HttpStatus;

public class DuplicateCategoryNameException extends BusinessException {

    public DuplicateCategoryNameException(String name) {
        super("A category with the name '" + name + "' already exists.",
                ErrorCode.DUPLICATE_RESOURCE,
                HttpStatus.CONFLICT);
    }
}