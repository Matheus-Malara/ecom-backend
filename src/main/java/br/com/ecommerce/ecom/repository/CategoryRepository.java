package br.com.ecommerce.ecom.repository;

import br.com.ecommerce.ecom.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByNameIgnoreCase(String categoryName);
}