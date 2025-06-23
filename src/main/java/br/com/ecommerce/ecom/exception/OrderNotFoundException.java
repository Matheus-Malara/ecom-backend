package br.com.ecommerce.ecom.exception;

public class OrderNotFoundException extends ResourceNotFoundException{
    public OrderNotFoundException(Long id) {
        super("Order with ID " + id + " not found");
    }
}
