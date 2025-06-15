package br.com.ecommerce.ecom.exception;

public class ProductNotFoundException extends ResourceNotFoundException {
    public ProductNotFoundException(Long id) {
        super("Product with ID " + id + " not found");
    }
}