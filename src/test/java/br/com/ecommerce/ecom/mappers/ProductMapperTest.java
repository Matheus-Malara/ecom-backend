package br.com.ecommerce.ecom.mappers;

import br.com.ecommerce.ecom.dto.responses.ProductResponseDTO;
import br.com.ecommerce.ecom.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductMapperTest {

    private ProductMapper productMapper;

    @BeforeEach
    void setUp() {
        productMapper = Mappers.getMapper(ProductMapper.class);
    }

    @Test
    void toResponseDTO_shouldMapEntityToDTOCorrectly() {
        Category category = Category.builder()
                .id(1L)
                .name("Supplements")
                .build();

        Brand brand = Brand.builder()
                .id(2L)
                .name("Growth Nutrition")
                .build();

        ProductImage image1 = ProductImage.builder()
                .id(1L)
                .imageUrl("https://example.com/image1.jpg")
                .build();

        ProductImage image2 = ProductImage.builder()
                .id(2L)
                .imageUrl("https://example.com/image2.jpg")
                .build();

        Product product = Product.builder()
                .id(100L)
                .name("Whey Protein")
                .description("High-quality protein for muscle growth.")
                .category(category)
                .brand(brand)
                .price(new BigDecimal("139.90"))
                .stock(50)
                .weightGrams(1000)
                .flavor("Vanilla")
                .images(List.of(image1, image2))
                .active(true)
                .build();

        ProductResponseDTO dto = productMapper.toResponseDTO(product);

        assertEquals(100L, dto.getId());
        assertEquals("Whey Protein", dto.getName());
        assertEquals("High-quality protein for muscle growth.", dto.getDescription());
        assertEquals("Supplements", dto.getCategoryName());
        assertEquals("Growth Nutrition", dto.getBrandName());
        assertEquals(new BigDecimal("139.90"), dto.getPrice());
        assertEquals(50, dto.getStock());
        assertEquals(1000, dto.getWeightGrams());
        assertEquals("Vanilla", dto.getFlavor());
        assertTrue(dto.getActive());

        assertNotNull(dto.getImageUrls());
        assertEquals(2, dto.getImageUrls().size());
        assertTrue(dto.getImageUrls().contains("https://example.com/image1.jpg"));
        assertTrue(dto.getImageUrls().contains("https://example.com/image2.jpg"));
    }

    @Test
    void toResponseDTO_shouldHandleNullImageListGracefully() {
        Category category = Category.builder().name("Isolates").build();
        Brand brand = Brand.builder().name("Black Skull").build();

        Product product = Product.builder()
                .id(200L)
                .name("Isolate Protein")
                .description("Fast absorption protein.")
                .category(category)
                .brand(brand)
                .price(new BigDecimal("159.99"))
                .stock(30)
                .weightGrams(900)
                .flavor("Strawberry")
                .images(null) // edge case
                .active(true)
                .build();

        ProductResponseDTO dto = productMapper.toResponseDTO(product);

        assertEquals(200L, dto.getId());
        assertEquals("Isolate Protein", dto.getName());
        assertEquals("Isolates", dto.getCategoryName());
        assertEquals("Black Skull", dto.getBrandName());
        assertNotNull(dto.getImageUrls());
        assertTrue(dto.getImageUrls().isEmpty());
    }
}
