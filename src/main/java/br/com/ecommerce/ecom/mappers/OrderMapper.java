package br.com.ecommerce.ecom.mappers;

import br.com.ecommerce.ecom.dto.responses.OrderItemDTO;
import br.com.ecommerce.ecom.dto.responses.OrderResponseDTO;
import br.com.ecommerce.ecom.entity.Order;
import br.com.ecommerce.ecom.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "items", target = "items")
    OrderResponseDTO toResponseDTO(Order order);

    @Mapping(source = "product.name", target = "productName")
    @Mapping(target = "imageUrl", expression = "java(getFirstImageUrl(item))")
    OrderItemDTO toItemDTO(OrderItem item);

    List<OrderItemDTO> toItemDTOList(List<OrderItem> items);

    default String getFirstImageUrl(OrderItem item) {
        if (item.getProduct() != null && !item.getProduct().getImages().isEmpty()) {
            return item.getProduct().getImages().getFirst().getImageUrl();
        }
        return null;
    }
}