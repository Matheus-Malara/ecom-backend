package br.com.ecommerce.ecom.controller;

import br.com.ecommerce.ecom.dto.filters.ProductFilterDTO;
import br.com.ecommerce.ecom.dto.requests.ProductRequestDTO;
import br.com.ecommerce.ecom.dto.responses.ApiResponse;
import br.com.ecommerce.ecom.dto.responses.ProductResponseDTO;
import br.com.ecommerce.ecom.factory.ResponseFactory;
import br.com.ecommerce.ecom.service.ProductService;
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
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    public static final String PRODUCT_BASE_PATH = "/api/products/";
    private final ProductService productService;
    private final ResponseFactory responseFactory;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponseDTO>> createProduct(@RequestBody @Valid ProductRequestDTO dto) {
        ProductResponseDTO response = productService.createProduct(dto);
        return responseFactory.createdResponse(response, "Product created successfully", PRODUCT_BASE_PATH);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductResponseDTO>>> getProductFiltered(
            @Valid @ModelAttribute ProductFilterDTO filter,
            @PageableDefault(size = 5) Pageable pageable) {

        Page<ProductResponseDTO> response = productService.getProductFiltered(filter, pageable);
        return responseFactory.okResponse(response, "All products fetched", PRODUCT_BASE_PATH);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> getProductById(@PathVariable Long id) {
        ProductResponseDTO product = productService.getProductById(id);
        return responseFactory.okResponse(product, "Product retrieved successfully", PRODUCT_BASE_PATH + id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> updateProduct(@PathVariable Long id,
                                                                         @RequestBody @Valid ProductRequestDTO dto) {
        ProductResponseDTO response = productService.updateProduct(id, dto);
        return responseFactory.okResponse(response, "Product updated successfully", PRODUCT_BASE_PATH + id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return responseFactory.noContentResponse("Product deleted successfully", PRODUCT_BASE_PATH + id);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> updateProductStatus(@PathVariable Long id,
                                                                 @RequestParam boolean active) {
        productService.updateProductStatus(id, active);
        return responseFactory.okResponse(null, "Product status updated", PRODUCT_BASE_PATH + id);
    }
}