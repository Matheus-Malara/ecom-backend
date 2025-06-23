package br.com.ecommerce.ecom.mappers;

import br.com.ecommerce.ecom.dto.responses.OrderItemDTO;
import br.com.ecommerce.ecom.dto.responses.OrderResponseDTO;
import br.com.ecommerce.ecom.entity.Order;
import br.com.ecommerce.ecom.entity.OrderItem;
import br.com.ecommerce.ecom.entity.Product;
import br.com.ecommerce.ecom.enums.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OrderMapperTest {

    private OrderMapper orderMapper;

    @BeforeEach
    void setUp() {
        orderMapper = Mappers.getMapper(OrderMapper.class);
    }

    @Test
    void toItemDTO_shouldMapItemToDTOCorrectly() {
        Product product = Product.builder()
                .id(1L)
                .name("Creatine")
                .price(new BigDecimal("89.90"))
                .build();

        OrderItem item = OrderItem.builder()
                .id(10L)
                .product(product)
                .quantity(3)
                .unitPrice(new BigDecimal("89.90"))
                .build();

        OrderItemDTO dto = orderMapper.toItemDTO(item);

        assertEquals("Creatine", dto.getProductName());
        assertEquals(3, dto.getQuantity());
        assertEquals(new BigDecimal("89.90"), dto.getUnitPrice());
    }

    @Test
    void toItemDTOList_shouldMapListCorrectly() {
        Product product = Product.builder()
                .id(2L)
                .name("Whey Protein")
                .price(new BigDecimal("129.90"))
                .build();

        OrderItem item = OrderItem.builder()
                .product(product)
                .quantity(2)
                .unitPrice(new BigDecimal("129.90"))
                .build();

        List<OrderItemDTO> dtoList = orderMapper.toItemDTOList(List.of(item));

        assertEquals(1, dtoList.size());

        OrderItemDTO dto = dtoList.getFirst();
        assertEquals("Whey Protein", dto.getProductName());
        assertEquals(2, dto.getQuantity());
        assertEquals(new BigDecimal("129.90"), dto.getUnitPrice());
    }

    @Test
    void toResponseDTO_shouldMapOrderToDTOIncludingItems() {
        Product product = Product.builder()
                .id(3L)
                .name("BCAA")
                .price(new BigDecimal("59.90"))
                .build();

        OrderItem item = OrderItem.builder()
                .product(product)
                .quantity(1)
                .unitPrice(new BigDecimal("59.90"))
                .build();

        Order order = Order.builder()
                .id(100L)
                .totalAmount(new BigDecimal("59.90"))
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.of(2025, 6, 23, 12, 0))
                .items(List.of(item))
                .build();

        OrderResponseDTO dto = orderMapper.toResponseDTO(order);

        assertEquals(100L, dto.getId());
        assertEquals(new BigDecimal("59.90"), dto.getTotalAmount());
        assertEquals(OrderStatus.PENDING, dto.getStatus());
        assertEquals(LocalDateTime.of(2025, 6, 23, 12, 0), dto.getCreatedAt());

        assertNotNull(dto.getItems());
        assertEquals(1, dto.getItems().size());

        OrderItemDTO itemDTO = dto.getItems().getFirst();
        assertEquals("BCAA", itemDTO.getProductName());
        assertEquals(1, itemDTO.getQuantity());
        assertEquals(new BigDecimal("59.90"), itemDTO.getUnitPrice());
    }
}
