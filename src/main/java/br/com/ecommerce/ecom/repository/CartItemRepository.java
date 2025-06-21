package br.com.ecommerce.ecom.repository;

import br.com.ecommerce.ecom.entity.Cart;
import br.com.ecommerce.ecom.entity.CartItem;
import br.com.ecommerce.ecom.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
}