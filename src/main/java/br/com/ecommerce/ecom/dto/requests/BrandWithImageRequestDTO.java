package br.com.ecommerce.ecom.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class BrandWithImageRequestDTO {

    @NotBlank(message = "Brand name is required")
    @Size(max = 100)
    private String name;

    @Size(max = 500)
    private String description;

    private MultipartFile image;
}
