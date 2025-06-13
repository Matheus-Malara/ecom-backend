package br.com.ecommerce.ecom.controller;

import br.com.ecommerce.ecom.dto.requests.BrandRequestDTO;
import br.com.ecommerce.ecom.dto.responses.ApiResponse;
import br.com.ecommerce.ecom.dto.responses.BrandResponseDTO;
import br.com.ecommerce.ecom.factory.ResponseFactory;
import br.com.ecommerce.ecom.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;
    private final ResponseFactory responseFactory;

    @PostMapping
    public ResponseEntity<ApiResponse<BrandResponseDTO>> createBrand(@RequestBody BrandRequestDTO dto) {
        BrandResponseDTO response = brandService.createBrand(dto);
        return responseFactory.createdResponse(response, "Brand created successfully", "/api/brands");
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BrandResponseDTO>>> getAllBrands() {
        List<BrandResponseDTO> brands = brandService.getAllBrands();
        return responseFactory.okResponse(brands, "Brands retrieved successfully", "/api/brands");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BrandResponseDTO>> getBrandById(@PathVariable Long id) {
        BrandResponseDTO brand = brandService.getBrandById(id);
        return responseFactory.okResponse(brand, "Brand retrieved successfully", "/api/brands/" + id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BrandResponseDTO>> updateBrand(@PathVariable Long id,
                                                                     @RequestBody BrandRequestDTO dto) {
        BrandResponseDTO response = brandService.updateBrand(id, dto);
        return responseFactory.okResponse(response, "Brand updated successfully", "/api/brands/" + id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBrand(@PathVariable Long id) {
        brandService.deleteBrand(id);
        return responseFactory.noContentResponse("Brand deleted successfully", "/api/brands/" + id);
    }
}