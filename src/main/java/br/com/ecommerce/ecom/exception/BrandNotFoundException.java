package br.com.ecommerce.ecom.exception;

public class BrandNotFoundException extends ResourceNotFoundException {
    public BrandNotFoundException(Long id) {
        super("Brand with ID " + id + " not found");
    }
}