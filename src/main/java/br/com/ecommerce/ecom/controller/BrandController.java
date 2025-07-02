package br.com.ecommerce.ecom.controller;

import br.com.ecommerce.ecom.dto.filters.BrandFilterDTO;
import br.com.ecommerce.ecom.dto.requests.BrandRequestDTO;
import br.com.ecommerce.ecom.dto.responses.BrandResponseDTO;
import br.com.ecommerce.ecom.dto.responses.StandardResponse;
import br.com.ecommerce.ecom.factory.ResponseFactory;
import br.com.ecommerce.ecom.service.BrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {

    public static final String BRAND_BASE_PATH = "/api/brands";

    private final BrandService brandService;
    private final ResponseFactory responseFactory;

    @Operation(
            summary = "Create a new brand",
            description = "Creates a brand and returns the created resource with its generated ID."
    )
    @ApiResponse(
            responseCode = "201",
            description = "Brand successfully created",
            content = @Content(schema = @Schema(implementation = StandardResponse.class))
    )
    @PostMapping
    public ResponseEntity<StandardResponse<BrandResponseDTO>> createBrand(
            @RequestBody @Valid BrandRequestDTO dto) {

        BrandResponseDTO response = brandService.createBrand(dto);
        return responseFactory.createdResponse(
                response,
                "Brand created successfully",
                BRAND_BASE_PATH
        );
    }


    @Operation(
            summary = "Upload brand logo",
            description = "Uploads a new logo to S3 for the brand. If an logo already exists, it will be replaced."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Image uploaded successfully"),
            @ApiResponse(responseCode = "404", description = "Brand not found", content = @Content)
    })
    @PostMapping("/{id}/upload-logo")
    public ResponseEntity<StandardResponse<BrandResponseDTO>> uploadBrandImage(
            @Parameter(description = "Brand ID", example = "1") @PathVariable Long id,
            @Parameter(description = "Image file") @RequestParam("file") MultipartFile file
    ) throws IOException {
        BrandResponseDTO response = brandService.uploadImage(id, file);
        return responseFactory.okResponse(response, "Image uploaded successfully", "/api/brands/" + id + "/logoUrl");
    }


    @Operation(
            summary = "List brands (paged + filters)",
            description = "Returns a paginated list of brands matching the provided filters."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Brands fetched successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid filter parameters", content = @Content)
    })
    @GetMapping
    public ResponseEntity<StandardResponse<Page<BrandResponseDTO>>> getBrandFiltered(
            @ParameterObject @Valid BrandFilterDTO filter,
            @ParameterObject @PageableDefault(size = 5) Pageable pageable) {

        Page<BrandResponseDTO> page = brandService.getBrandFiltered(filter, pageable);
        return responseFactory.okResponse(
                page,
                "All brands fetched",
                BRAND_BASE_PATH
        );
    }


    @Operation(
            summary = "Get brand by ID",
            description = "Returns a single brand by its identifier."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Brand found"),
            @ApiResponse(responseCode = "404", description = "Brand not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse<BrandResponseDTO>> getBrandById(
            @Parameter(description = "Brand ID", example = "1") @PathVariable Long id) {

        BrandResponseDTO brand = brandService.getBrandById(id);
        return responseFactory.okResponse(
                brand,
                "Brand retrieved successfully",
                BRAND_BASE_PATH + "/" + id
        );
    }


    @Operation(
            summary = "Update brand",
            description = "Updates all editable fields of a brand."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Brand updated successfully"),
            @ApiResponse(responseCode = "404", description = "Brand not found", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<StandardResponse<BrandResponseDTO>> updateBrand(
            @Parameter(description = "Brand ID", example = "1") @PathVariable Long id,
            @RequestBody @Valid BrandRequestDTO dto) {

        BrandResponseDTO response = brandService.updateBrand(id, dto);
        return responseFactory.okResponse(
                response,
                "Brand updated successfully",
                BRAND_BASE_PATH + "/" + id
        );
    }


    @Operation(
            summary = "Delete brand",
            description = "Deletes a brand by ID. This operation is irreversible."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Brand deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Brand not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<StandardResponse<Void>> deleteBrand(
            @Parameter(description = "Brand ID", example = "1") @PathVariable Long id) {

        brandService.deleteBrand(id);
        return responseFactory.noContentResponse("Brand deleted successfully", BRAND_BASE_PATH + "/" + id);
    }


    @Operation(
            summary = "Delete brand image",
            description = "Deletes the brand's image from S3 and clears the logoUrl"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Image deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Brand not found", content = @Content)
    })
    @DeleteMapping("/{id}/image")
    public ResponseEntity<StandardResponse<Void>> deleteBrandImage(
            @Parameter(description = "Brand ID", example = "1") @PathVariable Long id
    ) {
        brandService.deleteImage(id);
        return responseFactory.noContentResponse("Brand image deleted successfully", "/api/brands/" + id + "/image");
    }


    @Operation(
            summary = "Toggle brand status",
            description = "Activates or deactivates a brand."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Brand status updated"),
            @ApiResponse(responseCode = "404", description = "Brand not found", content = @Content)
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<StandardResponse<Void>> updateBrandStatus(
            @Parameter(description = "Brand ID", example = "1") @PathVariable Long id,
            @Parameter(description = "New status", example = "true") @RequestParam boolean active) {

        brandService.updateBrandStatus(id, active);
        return responseFactory.okResponse(
                null,
                "Brand status updated",
                BRAND_BASE_PATH + "/" + id + "/status"
        );
    }
}
