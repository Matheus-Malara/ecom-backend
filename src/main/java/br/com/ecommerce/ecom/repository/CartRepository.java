package br.com.ecommerce.ecom.repository;

import br.com.ecommerce.ecom.entity.Cart;
import br.com.ecommerce.ecom.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserAndCheckedOutFalse(User user);

    Optional<Cart> findByAnonymousIdAndCheckedOutFalse(String anonymousId);
}