package br.com.ecommerce.ecom.service;

import br.com.ecommerce.ecom.dto.filters.CategoryFilterDTO;
import br.com.ecommerce.ecom.dto.requests.CategoryRequestDTO;
import br.com.ecommerce.ecom.dto.responses.CategoryResponseDTO;
import br.com.ecommerce.ecom.entity.Category;
import br.com.ecommerce.ecom.enums.UploadType;
import br.com.ecommerce.ecom.exception.CategoryNotFoundException;
import br.com.ecommerce.ecom.exception.DuplicateCategoryNameException;
import br.com.ecommerce.ecom.mappers.CategoryMapper;
import br.com.ecommerce.ecom.repository.CategoryRepository;
import br.com.ecommerce.ecom.specification.CategorySpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final S3Service s3Service;

    @Transactional
    public CategoryResponseDTO createCategory(CategoryRequestDTO dto) {
        log.info("Creating category with name: {}", dto.getName());
        validateCategoryNameUniqueness(dto.getName());

        Category category = categoryMapper.toEntity(dto);
        Category savedCategory = categoryRepository.save(category);

        log.info("Category created successfully with ID: {}", savedCategory.getId());
        return categoryMapper.toResponseDTO(savedCategory);
    }

    @Transactional
    public CategoryResponseDTO uploadImage(Long categoryId, MultipartFile file) throws IOException {
        Category category = getExistingCategory(categoryId);

        if (category.getImageUrl() != null) {
            String previousKey = s3Service.extractKeyFromUrl(category.getImageUrl());
            s3Service.deleteFile(previousKey);
        }

        String newImageUrl = s3Service.uploadFile(file, UploadType.CATEGORIES, categoryId, category.getName());
        category.setImageUrl(newImageUrl);
        categoryRepository.save(category);

        log.info("Replaced category image for ID {} with new image", categoryId);
        return categoryMapper.toResponseDTO(category);
    }

    public Page<CategoryResponseDTO> getCategoryFiltered(CategoryFilterDTO filter, Pageable pageable) {
        Specification<Category> spec = CategorySpecification.withFilters(filter);

        log.debug("Fetching categories with filters: name='{}', active={}", filter.getName(), filter.getActive());

        return categoryRepository.findAll(spec, pageable)
                .map(categoryMapper::toResponseDTO);
    }

    public CategoryResponseDTO getCategoryById(Long id) {
        log.debug("Fetching category by ID: {}", id);
        Category category = getExistingCategory(id);
        return categoryMapper.toResponseDTO(category);
    }

    @Transactional
    public CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO dto) {
        log.info("Updating category with ID: {}", id);
        Category category = getExistingCategory(id);

        categoryMapper.updateEntityFromDTO(dto, category);
        Category updatedCategory = categoryRepository.save(category);

        log.info("Category with ID {} updated successfully", id);
        return categoryMapper.toResponseDTO(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        log.info("Deleting category with ID: {}", id);
        Category category = getExistingCategory(id);
        categoryRepository.delete(category);
        log.info("Category with ID {} deleted successfully", id);
    }

    @Transactional
    public void deleteImage(Long categoryId) {
        Category category = getExistingCategory(categoryId);

        if (category.getImageUrl() != null) {
            String key = s3Service.extractKeyFromUrl(category.getImageUrl());
            s3Service.deleteFile(key);
            category.setImageUrl(null);
            categoryRepository.save(category);
            log.info("Image deleted for category with ID: {}", categoryId);
        }
    }

    @Transactional
    public void updateCategoryStatus(Long id, boolean active) {
        log.info("Updating status of category ID {} to: {}", id, active);
        Category category = getExistingCategory(id);
        category.setActive(active);
        categoryRepository.save(category);
        log.info("Category ID {} status updated to {}", id, active);
    }

    // ========= Helpers =========


    public Category getExistingCategory(Long id) {
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
