package br.com.ecommerce.ecom.controller;

import br.com.ecommerce.ecom.dto.filters.BrandFilterDTO;
import br.com.ecommerce.ecom.dto.requests.BrandRequestDTO;
import br.com.ecommerce.ecom.dto.responses.ApiResponse;
import br.com.ecommerce.ecom.dto.responses.BrandResponseDTO;
import br.com.ecommerce.ecom.factory.ResponseFactory;
import br.com.ecommerce.ecom.service.BrandService;
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
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {

    public static final String BRAND_BASE_PATH = "/api/brands/";
    private final BrandService brandService;
    private final ResponseFactory responseFactory;

    @PostMapping
    public ResponseEntity<ApiResponse<BrandResponseDTO>> createBrand(@RequestBody @Valid BrandRequestDTO dto) {
        BrandResponseDTO response = brandService.createBrand(dto);
        return responseFactory.createdResponse(response, "Brand created successfully", BRAND_BASE_PATH);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<BrandResponseDTO>>> getAllBrands(
            @Valid @ModelAttribute BrandFilterDTO filter,
            @PageableDefault(size = 5) Pageable pageable) {

        Page<BrandResponseDTO> page = brandService.getBrandFiltered(filter, pageable);
        return responseFactory.okResponse(page, "All brands fetched", "/api/brands");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BrandResponseDTO>> getBrandById(@PathVariable Long id) {
        BrandResponseDTO brand = brandService.getBrandById(id);
        return responseFactory.okResponse(brand, "Brand retrieved successfully", BRAND_BASE_PATH + id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BrandResponseDTO>> updateBrand(@PathVariable Long id,
                                                                     @RequestBody @Valid BrandRequestDTO dto) {
        BrandResponseDTO response = brandService.updateBrand(id, dto);
        return responseFactory.okResponse(response, "Brand updated successfully", BRAND_BASE_PATH + id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBrand(@PathVariable Long id) {
        brandService.deleteBrand(id);
        return responseFactory.noContentResponse("Brand deleted successfully", BRAND_BASE_PATH + id);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> updateBrandStatus(@PathVariable Long id,
                                                               @RequestParam boolean active) {
        brandService.updateBrandStatus(id, active);
        return responseFactory.okResponse(null, "Brand status updated", BRAND_BASE_PATH + id);
    }

}