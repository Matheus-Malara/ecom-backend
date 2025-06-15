package br.com.ecommerce.ecom.controller;

import br.com.ecommerce.ecom.dto.filters.CategoryFilterDTO;
import br.com.ecommerce.ecom.dto.requests.CategoryRequestDTO;
import br.com.ecommerce.ecom.dto.responses.ApiResponse;
import br.com.ecommerce.ecom.dto.responses.CategoryResponseDTO;
import br.com.ecommerce.ecom.factory.ResponseFactory;
import br.com.ecommerce.ecom.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    public static final String CATEGORY_BASE_PATH = "/api/categories/";
    private final CategoryService categoryService;
    private final ResponseFactory responseFactory;

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponseDTO>> createCategory(@RequestBody @Valid CategoryRequestDTO dto) {
        CategoryResponseDTO response = categoryService.createCategory(dto);
        return responseFactory.createdResponse(response, "Category created successfully", CATEGORY_BASE_PATH);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<CategoryResponseDTO>>> getCategoryFiltered(
            @Valid @ModelAttribute CategoryFilterDTO filter,
            @PageableDefault(size = 5, sort = "name") Pageable pageable) {
        Page<CategoryResponseDTO> page = categoryService.getCategoryFiltered(filter, pageable);
        return responseFactory.okResponse(page, "All categories fetched", CATEGORY_BASE_PATH);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponseDTO>> getCategoryById(@PathVariable Long id) {
        CategoryResponseDTO category = categoryService.getCategoryById(id);
        return responseFactory.okResponse(category, "Category retrieved successfully", CATEGORY_BASE_PATH + id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponseDTO>> updateCategory(@PathVariable Long id,
                                                                           @RequestBody @Valid CategoryRequestDTO dto) {
        CategoryResponseDTO response = categoryService.updateCategory(id, dto);
        return responseFactory.okResponse(response, "Category updated successfully", CATEGORY_BASE_PATH + id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return responseFactory.noContentResponse("Category deleted successfully", CATEGORY_BASE_PATH);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> updateCategoryStatus(@PathVariable Long id,
                                                                  @RequestParam boolean active) {
        categoryService.updateCategoryStatus(id, active);
        return responseFactory.okResponse(null, "Category status updated", CATEGORY_BASE_PATH + id);
    }

}