package br.com.ecommerce.ecom.exception;

public class ActiveCartNotFoundException extends ResourceNotFoundException {

    public ActiveCartNotFoundException(String userName) {
        super("A Cart for the user '" + userName + "' does not exists");
    }
}