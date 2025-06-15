package br.com.ecommerce.ecom.repository;

import br.com.ecommerce.ecom.entity.Category;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {
    boolean existsByNameIgnoreCase(String categoryName);

    @NonNull
    Page<Category> findAll(@NonNull Pageable pageable);

}