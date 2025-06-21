package br.com.ecommerce.ecom.mappers;

import br.com.ecommerce.ecom.dto.responses.CartItemDTO;
import br.com.ecommerce.ecom.dto.responses.CartResponseDTO;
import br.com.ecommerce.ecom.entity.Cart;
import br.com.ecommerce.ecom.entity.CartItem;
import br.com.ecommerce.ecom.entity.ProductImage;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CartMapper {

    public CartResponseDTO toResponse(Cart cart) {
        List<CartItemDTO> itemDTOs = cart.getItems().stream()
                .map(this::toItemDTO)
                .collect(Collectors.toList());

        int totalItems = cart.getItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        BigDecimal totalAmount = cart.getItems().stream()
                .map(this::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponseDTO.builder()
                .cartId(cart.getId())
                .items(itemDTOs)
                .totalItems(totalItems)
                .totalAmount(totalAmount)
                .build();
    }

    private CartItemDTO toItemDTO(CartItem item) {
        return CartItemDTO.builder()
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .quantity(item.getQuantity())
                .pricePerUnit(item.getProduct().getPrice())
                .totalPrice(getTotalPrice(item))
                .imageUrl(getFirstImageUrl(item))
                .build();
    }

    private BigDecimal getTotalPrice(CartItem item) {
        return item.getProduct().getPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()));
    }

    private String getFirstImageUrl(CartItem item) {
        List<ProductImage> images = item.getProduct().getImages();
        if (images != null && !images.isEmpty()) {
            return images.getFirst().getImageUrl();
        }
        return null;
    }
}
