package br.com.ecommerce.ecom.dto.responses;


import lombok.Data;

@Data
public class CategoryResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private Boolean active;
}