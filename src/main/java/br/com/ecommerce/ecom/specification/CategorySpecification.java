package br.com.ecommerce.ecom.specification;

import br.com.ecommerce.ecom.dto.filters.CategoryFilterDTO;
import br.com.ecommerce.ecom.entity.Category;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class CategorySpecification {

    public static Specification<Category> withFilters(CategoryFilterDTO filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getName() != null && !filter.getName().isBlank()) {
                predicates.add(
                        cb.like(cb.lower(root.get("name")), "%" + filter.getName().toLowerCase() + "%")
                );
            }

            if (filter.getActive() != null) {
                predicates.add(cb.equal(root.get("active"), filter.getActive()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}