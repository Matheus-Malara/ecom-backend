package br.com.ecommerce.ecom.exception;


public class CartItemNotFoundException extends ResourceNotFoundException {
    public CartItemNotFoundException(Long productId) {
        super("Cart item with product ID " + productId + " not found in cart");
    }
}
