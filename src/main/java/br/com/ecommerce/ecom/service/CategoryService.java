package br.com.ecommerce.ecom.service;

import br.com.ecommerce.ecom.dto.requests.CategoryRequestDTO;
import br.com.ecommerce.ecom.dto.responses.CategoryResponseDTO;
import br.com.ecommerce.ecom.entity.Category;
import br.com.ecommerce.ecom.exception.CategoryNotFoundException;
import br.com.ecommerce.ecom.mappers.CategoryMapper;
import br.com.ecommerce.ecom.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryResponseDTO createCategory(CategoryRequestDTO dto) {
        Category category = categoryMapper.toEntity(dto);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toResponseDTO(savedCategory);
    }

    public List<CategoryResponseDTO> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public CategoryResponseDTO getCategoryById(Long id) {
        Category category = findByIdOrThrow(id);
        return categoryMapper.toResponseDTO(category);
    }

    public CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO dto) {
        Category category = findByIdOrThrow(id);

        categoryMapper.updateEntityFromDTO(dto, category);
        Category updatedCategory = categoryRepository.save(category);

        return categoryMapper.toResponseDTO(updatedCategory);
    }

    public void deleteCategory(Long id) {
        Category category = findByIdOrThrow(id);
        categoryRepository.delete(category);
    }

    private Category findByIdOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }
}
