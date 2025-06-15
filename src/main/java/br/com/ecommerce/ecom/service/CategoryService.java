package br.com.ecommerce.ecom.service;

import br.com.ecommerce.ecom.dto.requests.CategoryRequestDTO;
import br.com.ecommerce.ecom.dto.responses.CategoryResponseDTO;
import br.com.ecommerce.ecom.entity.Category;
import br.com.ecommerce.ecom.exception.CategoryNotFoundException;
import br.com.ecommerce.ecom.exception.DuplicateCategoryNameException;
import br.com.ecommerce.ecom.mappers.CategoryMapper;
import br.com.ecommerce.ecom.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryResponseDTO createCategory(CategoryRequestDTO dto) {
        log.info("Creating category with name: {}", dto.getName());
        validateCategoryNameUniqueness(dto.getName());

        Category category = categoryMapper.toEntity(dto);
        Category savedCategory = categoryRepository.save(category);

        log.info("Category created successfully with ID: {}", savedCategory.getId());
        return categoryMapper.toResponseDTO(savedCategory);
    }

    public List<CategoryResponseDTO> getAllCategories() {
        log.debug("Fetching all categories");
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public CategoryResponseDTO getCategoryById(Long id) {
        log.debug("Fetching category by ID: {}", id);
        Category category = findByIdOrThrow(id);
        return categoryMapper.toResponseDTO(category);
    }

    public CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO dto) {
        log.info("Updating category with ID: {}", id);
        Category category = findByIdOrThrow(id);

        categoryMapper.updateEntityFromDTO(dto, category);
        Category updatedCategory = categoryRepository.save(category);

        log.info("Category with ID {} updated successfully", id);
        return categoryMapper.toResponseDTO(updatedCategory);
    }

    public void deleteCategory(Long id) {
        log.info("Deleting category with ID: {}", id);
        Category category = findByIdOrThrow(id);
        categoryRepository.delete(category);
        log.info("Category with ID {} deleted successfully", id);
    }

    public void updateCategoryStatus(Long id, boolean active) {
        log.info("Updating status of category ID {} to: {}", id, active);
        Category category = findByIdOrThrow(id);
        category.setActive(active);
        categoryRepository.save(category);
        log.info("Category ID {} status updated to {}", id, active);
    }

    // ========= Helpers =========

    private Category findByIdOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Category with ID {} not found", id);
                    return new CategoryNotFoundException(id);
                });
    }

    private void validateCategoryNameUniqueness(String categoryName) {
        if (categoryRepository.existsByNameIgnoreCase(categoryName)) {
            log.warn("Category name '{}' already exists", categoryName);
            throw new DuplicateCategoryNameException(categoryName);
        }
    }
}
