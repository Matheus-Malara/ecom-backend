package br.com.ecommerce.ecom.mappers;

import br.com.ecommerce.ecom.dto.responses.ProductResponseDTO;
import br.com.ecommerce.ecom.entity.Product;
import br.com.ecommerce.ecom.entity.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    // Entity → ResponseDTO
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "brand.name", target = "brandName")
    @Mapping(source = "images", target = "imageUrls", qualifiedByName = "mapImageUrls")
    ProductResponseDTO toResponseDTO(Product entity);

    @Named("mapImageUrls")
    default List<String> mapImageUrls(List<ProductImage> images) {
        if (images == null) return List.of();
        return images.stream().map(ProductImage::getImageUrl).collect(Collectors.toList());
    }
}
