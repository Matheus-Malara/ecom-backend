package br.com.ecommerce.ecom.mappers;

import br.com.ecommerce.ecom.dto.requests.CategoryRequestDTO;
import br.com.ecommerce.ecom.dto.responses.CategoryResponseDTO;
import br.com.ecommerce.ecom.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    Category toEntity(CategoryRequestDTO dto);

    CategoryResponseDTO toResponseDTO(Category entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateEntityFromDTO(CategoryRequestDTO dto, @MappingTarget Category entity);
}
