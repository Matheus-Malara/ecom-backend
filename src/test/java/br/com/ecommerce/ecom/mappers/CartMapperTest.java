package br.com.ecommerce.ecom.mappers;

import br.com.ecommerce.ecom.dto.responses.CartItemDTO;
import br.com.ecommerce.ecom.dto.responses.CartResponseDTO;
import br.com.ecommerce.ecom.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CartMapperTest {

    private CartMapper cartMapper;

    @BeforeEach
    void setUp() {
        cartMapper = new CartMapper();
    }

    @Test
    void toResponse_shouldMapCartToDtoCorrectly() {
        ProductImage image = ProductImage.builder()
                .id(1L)
                .imageUrl("https://example.com/image.png")
                .build();

        Product product = Product.builder()
                .id(100L)
                .name("Whey Protein")
                .price(new BigDecimal("129.90"))
                .images(List.of(image))
                .build();

        // Create a cart item
        CartItem item = CartItem.builder()
                .id(10L)
                .product(product)
                .quantity(2)
                .build();

        // Create the cart
        Cart cart = Cart.builder()
                .id(1L)
                .items(List.of(item))
                .build();

        // Act
        CartResponseDTO dto = cartMapper.toResponse(cart);

        // Assert
        assertEquals(1L, dto.getCartId());
        assertEquals(2, dto.getTotalItems());
        assertEquals(new BigDecimal("259.80"), dto.getTotalAmount());

        assertNotNull(dto.getItems());
        assertEquals(1, dto.getItems().size());

        CartItemDTO itemDTO = dto.getItems().getFirst();
        assertEquals(100L, itemDTO.getProductId());
        assertEquals("Whey Protein", itemDTO.getProductName());
        assertEquals(2, itemDTO.getQuantity());
        assertEquals(new BigDecimal("129.90"), itemDTO.getPricePerUnit());
        assertEquals(new BigDecimal("259.80"), itemDTO.getTotalPrice());
        assertEquals("https://example.com/image.png", itemDTO.getImageUrl());
    }

    @Test
    void toResponse_shouldHandleEmptyCartGracefully() {
        Cart emptyCart = Cart.builder()
                .id(2L)
                .items(List.of())
                .build();

        CartResponseDTO dto = cartMapper.toResponse(emptyCart);

        assertEquals(2L, dto.getCartId());
        assertEquals(0, dto.getTotalItems());
        assertEquals(BigDecimal.ZERO, dto.getTotalAmount());
        assertTrue(dto.getItems().isEmpty());
    }

    @Test
    void toItemDTO_shouldReturnNullImageWhenNoImagesPresent() {
        Product productWithoutImages = Product.builder()
                .id(101L)
                .name("Creatine")
                .price(new BigDecimal("89.90"))
                .images(List.of())
                .build();

        CartItem item = CartItem.builder()
                .product(productWithoutImages)
                .quantity(1)
                .build();

        Cart cart = Cart.builder()
                .id(3L)
                .items(List.of(item))
                .build();

        CartResponseDTO dto = cartMapper.toResponse(cart);
        CartItemDTO itemDTO = dto.getItems().getFirst();

        assertNull(itemDTO.getImageUrl());
    }
}
