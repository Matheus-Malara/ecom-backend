package br.com.ecommerce.ecom.controller;

import br.com.ecommerce.ecom.dto.filters.CategoryFilterDTO;
import br.com.ecommerce.ecom.dto.requests.CategoryRequestDTO;
import br.com.ecommerce.ecom.dto.requests.CategoryWithImageRequestDTO;
import br.com.ecommerce.ecom.dto.responses.CategoryResponseDTO;
import br.com.ecommerce.ecom.dto.responses.StandardResponse;
import br.com.ecommerce.ecom.factory.ResponseFactory;
import br.com.ecommerce.ecom.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    public static final String CATEGORY_BASE_PATH = "/api/categories";

    private final CategoryService categoryService;
    private final ResponseFactory responseFactory;

    @Operation(summary = "Create a new category", description = "Creates a category with optional image upload")
    @ApiResponse(responseCode = "201", description = "Category created successfully")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StandardResponse<CategoryResponseDTO>> createCategory(
            @ModelAttribute @Valid CategoryWithImageRequestDTO dto) throws IOException {

        CategoryResponseDTO response = categoryService.createCategory(dto);
        return responseFactory.createdResponse(
                response,
                "Category created successfully",
                CATEGORY_BASE_PATH
        );
    }


    @Operation(
            summary = "Upload category image",
            description = "Uploads an image to S3 and saves the imageUrl for the specified category"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Image uploaded successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid file", content = @Content)
    })
    @PostMapping("/{id}/upload-image")
    public ResponseEntity<StandardResponse<CategoryResponseDTO>> uploadCategoryImage(
            @Parameter(description = "Category ID", example = "1") @PathVariable Long id,
            @Parameter(description = "Image file to upload") @RequestParam("file") MultipartFile file
    ) throws IOException {
        CategoryResponseDTO response = categoryService.uploadImage(id, file);
        return responseFactory.okResponse(response, "Image uploaded successfully", CATEGORY_BASE_PATH + "/" + id + "/upload-image");
    }


    @Operation(summary = "Get categories with filter and pagination", description = "Returns paginated and filtered list of categories.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categories fetched successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid filters", content = @Content)
    })
    @GetMapping
    public ResponseEntity<StandardResponse<Page<CategoryResponseDTO>>> getCategoryFiltered(
            @ParameterObject @Valid CategoryFilterDTO filter,
            @ParameterObject @PageableDefault(size = 5) Pageable pageable) {

        Page<CategoryResponseDTO> page = categoryService.getCategoryFiltered(filter, pageable);
        return responseFactory.okResponse(page, "All categories fetched", CATEGORY_BASE_PATH);
    }


    @Operation(summary = "Get category by ID", description = "Returns a category by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse<CategoryResponseDTO>> getCategoryById(
            @Parameter(description = "Category ID", example = "1") @PathVariable Long id) {

        CategoryResponseDTO category = categoryService.getCategoryById(id);
        return responseFactory.okResponse(category, "Category retrieved successfully", CATEGORY_BASE_PATH + "/" + id);
    }


    @Operation(summary = "Update category", description = "Updates an existing category by ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category updated successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<StandardResponse<CategoryResponseDTO>> updateCategory(
            @Parameter(description = "Category ID", example = "1") @PathVariable Long id,
            @RequestBody @Valid CategoryRequestDTO dto) {

        CategoryResponseDTO response = categoryService.updateCategory(id, dto);
        return responseFactory.okResponse(response, "Category updated successfully", CATEGORY_BASE_PATH + "/" + id);
    }


    @Operation(summary = "Delete category", description = "Deletes a category by ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<StandardResponse<Void>> deleteCategory(
            @Parameter(description = "Category ID", example = "1") @PathVariable Long id) {

        categoryService.deleteCategory(id);
        return responseFactory.noContentResponse("Category deleted successfully", CATEGORY_BASE_PATH + "/" + id);
    }


    @Operation(
            summary = "Delete category image",
            description = "Deletes the category image from S3 and clears the imageUrl field"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Image deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found", content = @Content)
    })
    @DeleteMapping("/{id}/image")
    public ResponseEntity<StandardResponse<Void>> deleteCategoryImage(
            @Parameter(description = "Category ID", example = "1") @PathVariable Long id) {
        categoryService.deleteImage(id);
        return responseFactory.noContentResponse("Category image deleted successfully", CATEGORY_BASE_PATH + "/" + id + "/image");
    }


    @Operation(summary = "Update category status", description = "Activates or deactivates a category.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category status updated"),
            @ApiResponse(responseCode = "404", description = "Category not found", content = @Content)
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<StandardResponse<Void>> updateCategoryStatus(
            @Parameter(description = "Category ID", example = "1") @PathVariable Long id,
            @Parameter(description = "New status", example = "true") @RequestParam boolean active) {

        categoryService.updateCategoryStatus(id, active);
        return responseFactory.okResponse(null, "Category status updated", CATEGORY_BASE_PATH + "/" + id + "/status");
    }
}
