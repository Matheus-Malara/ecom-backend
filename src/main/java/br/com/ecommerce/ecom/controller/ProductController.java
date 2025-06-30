package br.com.ecommerce.ecom.controller;

import br.com.ecommerce.ecom.dto.filters.ProductFilterDTO;
import br.com.ecommerce.ecom.dto.requests.ProductRequestDTO;
import br.com.ecommerce.ecom.dto.responses.ProductImageResponseDTO;
import br.com.ecommerce.ecom.dto.responses.ProductResponseDTO;
import br.com.ecommerce.ecom.dto.responses.StandardResponse;
import br.com.ecommerce.ecom.factory.ResponseFactory;
import br.com.ecommerce.ecom.service.ProductService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    public static final String PRODUCT_BASE_PATH = "/api/products";

    private final ProductService productService;
    private final ResponseFactory responseFactory;

    @Operation(summary = "Create a new product", description = "Creates a new product and returns the created entity.")
    @ApiResponse(responseCode = "201", description = "Product created successfully")
    @PostMapping
    public ResponseEntity<StandardResponse<ProductResponseDTO>> createProduct(
            @RequestBody @Valid ProductRequestDTO dto) {
        ProductResponseDTO response = productService.createProduct(dto);
        return responseFactory.createdResponse(response, "Product created successfully", PRODUCT_BASE_PATH);
    }

    @Operation(summary = "Upload product image", description = "Uploads an image for the specified product.")
    @PostMapping("/{id}/upload-image")
    public ResponseEntity<StandardResponse<ProductImageResponseDTO>> uploadProductImage(
            @Parameter(description = "Product ID", example = "1") @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws IOException {

        ProductImageResponseDTO response = productService.uploadImage(id, file);
        return responseFactory.okResponse(response, "Image uploaded successfully", PRODUCT_BASE_PATH + "/" + id + "/upload-image");
    }

    @Operation(summary = "Get filtered products", description = "Returns a paginated and filtered list of products.")
    @GetMapping
    public ResponseEntity<StandardResponse<Page<ProductResponseDTO>>> getProductFiltered(
            @ParameterObject @Valid ProductFilterDTO filter,
            @ParameterObject @PageableDefault(size = 5) Pageable pageable) {

        Page<ProductResponseDTO> response = productService.getProductFiltered(filter, pageable);
        return responseFactory.okResponse(response, "All products fetched", PRODUCT_BASE_PATH);
    }

    @Operation(summary = "Get product by ID", description = "Retrieves a product by its unique identifier.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse<ProductResponseDTO>> getProductById(
            @Parameter(description = "Product ID", example = "1") @PathVariable Long id) {
        ProductResponseDTO product = productService.getProductById(id);
        return responseFactory.okResponse(product, "Product retrieved successfully", PRODUCT_BASE_PATH + "/" + id);
    }

    @Operation(summary = "Update product", description = "Updates an existing product by ID.")
    @PutMapping("/{id}")
    public ResponseEntity<StandardResponse<ProductResponseDTO>> updateProduct(
            @Parameter(description = "Product ID", example = "1") @PathVariable Long id,
            @RequestBody @Valid ProductRequestDTO dto) {
        ProductResponseDTO response = productService.updateProduct(id, dto);
        return responseFactory.okResponse(response, "Product updated successfully", PRODUCT_BASE_PATH + "/" + id);
    }

    @Operation(summary = "Delete product", description = "Deletes a product by its ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<StandardResponse<Void>> deleteProduct(
            @Parameter(description = "Product ID", example = "1") @PathVariable Long id) {
        productService.deleteProduct(id);
        return responseFactory.noContentResponse("Product deleted successfully", PRODUCT_BASE_PATH + "/" + id);
    }

    @Operation(summary = "Delete product image", description = "Removes an image from a product and deletes it from S3.")
    @DeleteMapping("/{productId}/images/{imageId}")
    public ResponseEntity<StandardResponse<Void>> deleteProductImage(
            @PathVariable Long productId,
            @PathVariable Long imageId) {

        productService.deleteProductImage(productId, imageId);
        return responseFactory.noContentResponse("Image deleted successfully", PRODUCT_BASE_PATH + "/" + productId + "/images/" + imageId);
    }

    @Operation(summary = "Update product status", description = "Activates or deactivates a product.")
    @PatchMapping("/{id}/status")
    public ResponseEntity<StandardResponse<Void>> updateProductStatus(
            @Parameter(description = "Product ID", example = "1") @PathVariable Long id,
            @Parameter(description = "New status", example = "true") @RequestParam boolean active) {
        productService.updateProductStatus(id, active);
        return responseFactory.okResponse(null, "Product status updated", PRODUCT_BASE_PATH + "/" + id + "/status");
    }
}