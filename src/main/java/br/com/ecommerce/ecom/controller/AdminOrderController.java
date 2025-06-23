package br.com.ecommerce.ecom.controller;

import br.com.ecommerce.ecom.dto.filters.OrderFilterDTO;
import br.com.ecommerce.ecom.dto.requests.UpdateOrderStatusRequestDTO;
import br.com.ecommerce.ecom.dto.responses.ApiResponse;
import br.com.ecommerce.ecom.dto.responses.OrderResponseDTO;
import br.com.ecommerce.ecom.entity.Order;
import br.com.ecommerce.ecom.factory.ResponseFactory;
import br.com.ecommerce.ecom.mappers.OrderMapper;
import br.com.ecommerce.ecom.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private static final String ADMIN_ORDERS_BASE_PATH = "/api/admin/orders";

    private final OrderService orderService;
    private final ResponseFactory responseFactory;
    private final OrderMapper orderMapper;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<OrderResponseDTO>>> getOrdersFiltered(
            @Valid @ModelAttribute OrderFilterDTO filter,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<Order> page = orderService.getOrdersFiltered(filter, pageable);
        Page<OrderResponseDTO> response = page.map(orderMapper::toResponseDTO);

        return responseFactory.okResponse(response, "Orders fetched successfully", ADMIN_ORDERS_BASE_PATH);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> getOrder(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        OrderResponseDTO response = orderMapper.toResponseDTO(order);

        return responseFactory.okResponse(response, "Order fetched", ADMIN_ORDERS_BASE_PATH + id);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> updateOrderStatus(@PathVariable Long id,
                                                                           @RequestBody @Valid UpdateOrderStatusRequestDTO dto) {
        Order order = orderService.updateOrderStatus(id, dto.getStatus());
        OrderResponseDTO response = orderMapper.toResponseDTO(order);
        return responseFactory.okResponse(response, "Order status updated", ADMIN_ORDERS_BASE_PATH + id);
    }
}
