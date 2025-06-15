package br.com.ecommerce.ecom.exception;

public class CategoryNotFoundException extends ResourceNotFoundException {
    public CategoryNotFoundException(Long id) {
        super("Category with ID " + id + " not found");
    }
}