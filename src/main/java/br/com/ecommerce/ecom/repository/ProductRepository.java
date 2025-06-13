package br.com.ecommerce.ecom.repository;

import br.com.ecommerce.ecom.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}