package br.com.ecommerce.ecom.repository;

import br.com.ecommerce.ecom.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
}