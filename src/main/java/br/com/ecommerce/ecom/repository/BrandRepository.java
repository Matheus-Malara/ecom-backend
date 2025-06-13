package br.com.ecommerce.ecom.repository;

import br.com.ecommerce.ecom.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<Brand, Long> {
}