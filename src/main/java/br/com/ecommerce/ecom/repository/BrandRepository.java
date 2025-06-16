package br.com.ecommerce.ecom.repository;

import br.com.ecommerce.ecom.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BrandRepository extends JpaRepository<Brand, Long>, JpaSpecificationExecutor<Brand> {
    boolean existsByNameIgnoreCase(String brandName);
}
