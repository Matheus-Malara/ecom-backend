package br.com.ecommerce.ecom.dto.responses;


import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String categoryName;
    private String brandName;
    private BigDecimal price;
    private Integer stock;
    private Integer weightGrams;
    private String flavor;
    private List<String> imageUrls;
    private Boolean active;
}