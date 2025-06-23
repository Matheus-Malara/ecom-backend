package br.com.ecommerce.ecom.mappers;

import br.com.ecommerce.ecom.dto.requests.BrandRequestDTO;
import br.com.ecommerce.ecom.dto.responses.BrandResponseDTO;
import br.com.ecommerce.ecom.entity.Brand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class BrandMapperTest {

    private BrandMapper brandMapper;

    @BeforeEach
    void setUp() {
        brandMapper = Mappers.getMapper(BrandMapper.class);
    }

    @Test
    void toEntity_shouldMapDtoToEntityIgnoringIdAndPreservingDefaultActive() {
        BrandRequestDTO dto = new BrandRequestDTO();
        dto.setName("Alpha Nutrition");
        dto.setDescription("High-quality sports supplements.");
        dto.setLogoUrl("https://example.com/logo.png");

        Brand brand = brandMapper.toEntity(dto);

        assertNull(brand.getId());
        assertEquals(true, brand.getActive()); // uses @Builder.Default = true
        assertEquals("Alpha Nutrition", brand.getName());
        assertEquals("High-quality sports supplements.", brand.getDescription());
        assertEquals("https://example.com/logo.png", brand.getLogoUrl());
    }

    @Test
    void toResponseDTO_shouldMapEntityToDtoCorrectly() {
        Brand brand = Brand.builder()
                .id(1L)
                .name("Titanium Labs")
                .description("Performance-focused nutrition.")
                .logoUrl("https://cdn.titaniumlabs.com/logo.png")
                .active(true)
                .build();

        BrandResponseDTO dto = brandMapper.toResponseDTO(brand);

        assertEquals(1L, dto.getId());
        assertEquals("Titanium Labs", dto.getName());
        assertEquals("Performance-focused nutrition.", dto.getDescription());
        assertEquals("https://cdn.titaniumlabs.com/logo.png", dto.getLogoUrl());
        assertTrue(dto.getActive());
    }

    @Test
    void updateEntityFromDTO_shouldUpdateFieldsExceptIdAndActive() {
        Brand existing = Brand.builder()
                .id(5L)
                .name("Legacy Brand")
                .description("Old description")
                .logoUrl("https://legacy.com/logo.png")
                .active(false)
                .build();

        BrandRequestDTO dto = new BrandRequestDTO();
        dto.setName("Updated Brand");
        dto.setDescription("Updated brand description");
        dto.setLogoUrl("https://updated.com/logo.png");

        brandMapper.updateEntityFromDTO(dto, existing);

        assertEquals(5L, existing.getId()); // remains unchanged
        assertEquals("Updated Brand", existing.getName());
        assertEquals("Updated brand description", existing.getDescription());
        assertEquals("https://updated.com/logo.png", existing.getLogoUrl());
        assertFalse(existing.getActive()); // remains unchanged
    }
}
