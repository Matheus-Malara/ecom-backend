package br.com.ecommerce.ecom.mappers;

import br.com.ecommerce.ecom.dto.requests.CategoryRequestDTO;
import br.com.ecommerce.ecom.dto.responses.CategoryResponseDTO;
import br.com.ecommerce.ecom.entity.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class CategoryMapperTest {

    private CategoryMapper categoryMapper;

    @BeforeEach
    void setUp() {
        categoryMapper = Mappers.getMapper(CategoryMapper.class);
    }

    @Test
    void toEntity_shouldMapDtoToEntityIgnoringIdAndUsingDefaultActive() {
        CategoryRequestDTO dto = new CategoryRequestDTO();
        dto.setName("Pre-Workout");
        dto.setDescription("Boosts energy before training.");
        dto.setImageUrl("https://example.com/preworkout.png");

        Category entity = categoryMapper.toEntity(dto);

        assertNull(entity.getId());
        assertEquals("Pre-Workout", entity.getName());
        assertEquals("Boosts energy before training.", entity.getDescription());
        assertEquals("https://example.com/preworkout.png", entity.getImageUrl());
        assertEquals(true, entity.getActive());
    }

    @Test
    void toResponseDTO_shouldMapEntityToDtoCorrectly() {
        Category entity = Category.builder()
                .id(10L)
                .name("Protein")
                .description("Essential for muscle growth.")
                .imageUrl("https://example.com/protein.png")
                .active(true)
                .build();

        CategoryResponseDTO dto = categoryMapper.toResponseDTO(entity);

        assertEquals(10L, dto.getId());
        assertEquals("Protein", dto.getName());
        assertEquals("Essential for muscle growth.", dto.getDescription());
        assertEquals("https://example.com/protein.png", dto.getImageUrl());
        assertTrue(dto.getActive());
    }

    @Test
    void updateEntityFromDTO_shouldUpdateFieldsAndPreserveIdAndActive() {
        Category existing = Category.builder()
                .id(5L)
                .name("Old Category")
                .description("Old description")
                .imageUrl("https://old.com/img.png")
                .active(false)
                .build();

        CategoryRequestDTO dto = new CategoryRequestDTO();
        dto.setName("Updated Category");
        dto.setDescription("New description for the category.");
        dto.setImageUrl("https://new.com/img.png");

        categoryMapper.updateEntityFromDTO(dto, existing);

        assertEquals(5L, existing.getId());
        assertEquals("Updated Category", existing.getName());
        assertEquals("New description for the category.", existing.getDescription());
        assertEquals("https://new.com/img.png", existing.getImageUrl());
        assertFalse(existing.getActive());
    }
}
