package br.com.ecommerce.ecom.mappers;

import br.com.ecommerce.ecom.dto.requests.CategoryRequestDTO;
import br.com.ecommerce.ecom.dto.responses.CategoryResponseDTO;
import br.com.ecommerce.ecom.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toEntity(CategoryRequestDTO dto);

    CategoryResponseDTO toResponseDTO(Category entity);

    void updateEntityFromDTO(CategoryRequestDTO dto, @MappingTarget Category entity);
}