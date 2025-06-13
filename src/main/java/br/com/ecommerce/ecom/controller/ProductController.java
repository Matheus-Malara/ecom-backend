package br.com.ecommerce.ecom.controller;

import br.com.ecommerce.ecom.dto.requests.ProductRequestDTO;
import br.com.ecommerce.ecom.dto.responses.ApiResponse;
import br.com.ecommerce.ecom.dto.responses.ProductResponseDTO;
import br.com.ecommerce.ecom.factory.ResponseFactory;
import br.com.ecommerce.ecom.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ResponseFactory responseFactory;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponseDTO>> createProduct(@RequestBody ProductRequestDTO dto) {
        ProductResponseDTO response = productService.createProduct(dto);
        return responseFactory.createdResponse(response, "Product created successfully", "/api/products");
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponseDTO>>> getAllProducts() {
        List<ProductResponseDTO> products = productService.getAllProducts();
        return responseFactory.okResponse(products, "Products retrieved successfully", "/api/products");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> getProductById(@PathVariable Long id) {
        ProductResponseDTO product = productService.getProductById(id);
        return responseFactory.okResponse(product, "Product retrieved successfully", "/api/products/" + id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> updateProduct(@PathVariable Long id,
                                                                         @RequestBody ProductRequestDTO dto) {
        ProductResponseDTO response = productService.updateProduct(id, dto);
        return responseFactory.okResponse(response, "Product updated successfully", "/api/products/" + id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return responseFactory.noContentResponse("Product deleted successfully", "/api/products/" + id);
    }
}