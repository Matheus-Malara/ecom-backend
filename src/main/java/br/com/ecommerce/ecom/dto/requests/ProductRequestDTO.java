package br.com.ecommerce.ecom.dto.requests;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductRequestDTO {
    private String name;
    private String description;
    private Long categoryId;
    private Long brandId;
    private BigDecimal price;
    private Integer stock;
    private Integer weightGrams;
    private String flavor;
    private List<String> imageUrls;
}