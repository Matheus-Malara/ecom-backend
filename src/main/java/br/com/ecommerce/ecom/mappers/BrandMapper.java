package br.com.ecommerce.ecom.mappers;

import br.com.ecommerce.ecom.dto.requests.BrandRequestDTO;
import br.com.ecommerce.ecom.dto.responses.BrandResponseDTO;
import br.com.ecommerce.ecom.entity.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BrandMapper {

    Brand toEntity(BrandRequestDTO dto);

    BrandResponseDTO toResponseDTO(Brand entity);

    void updateEntityFromDTO(BrandRequestDTO dto, @MappingTarget Brand entity);
}