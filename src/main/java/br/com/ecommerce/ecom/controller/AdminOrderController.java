package br.com.ecommerce.ecom.controller;

import br.com.ecommerce.ecom.dto.filters.OrderFilterDTO;
import br.com.ecommerce.ecom.dto.requests.UpdateOrderStatusRequestDTO;
import br.com.ecommerce.ecom.dto.responses.StandardResponse;
import br.com.ecommerce.ecom.dto.responses.OrderResponseDTO;
import br.com.ecommerce.ecom.entity.Order;
import br.com.ecommerce.ecom.factory.ResponseFactory;
import br.com.ecommerce.ecom.mappers.OrderMapper;
import br.com.ecommerce.ecom.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
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
@Tag(name = "Admin Orders", description = "Endpoints for managing orders from the admin panel")
public class AdminOrderController {

    private static final String ADMIN_ORDERS_BASE_PATH = "/api/admin/orders";

    private final OrderService orderService;
    private final ResponseFactory responseFactory;
    private final OrderMapper orderMapper;

    @Operation(summary = "Get filtered orders", description = "Returns a paginated list of orders based on filter criteria")
    @GetMapping
    public ResponseEntity<StandardResponse<Page<OrderResponseDTO>>> getOrdersFiltered(
            @Valid @ModelAttribute OrderFilterDTO filter,
            @ParameterObject @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<Order> page = orderService.getOrdersFiltered(filter, pageable);
        Page<OrderResponseDTO> response = page.map(orderMapper::toResponseDTO);
        return responseFactory.okResponse(response, "Orders fetched successfully", ADMIN_ORDERS_BASE_PATH);
    }

    @Operation(summary = "Get order by ID", description = "Returns the order with the given ID")
    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse<OrderResponseDTO>> getOrder(
            @Parameter(description = "Order ID", required = true) @PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        OrderResponseDTO response = orderMapper.toResponseDTO(order);
        return responseFactory.okResponse(response, "Order fetched", ADMIN_ORDERS_BASE_PATH + "/" + id);
    }

    @Operation(summary = "Update order status", description = "Updates the status of the given order")
    @PutMapping("/{id}/status")
    public ResponseEntity<StandardResponse<OrderResponseDTO>> updateOrderStatus(
            @Parameter(description = "Order ID", required = true) @PathVariable Long id,
            @RequestBody @Valid UpdateOrderStatusRequestDTO dto) {
        Order order = orderService.updateOrderStatus(id, dto.getStatus());
        OrderResponseDTO response = orderMapper.toResponseDTO(order);
        return responseFactory.okResponse(response, "Order status updated", ADMIN_ORDERS_BASE_PATH + "/" + id);
    }

    @Operation(summary = "Get total order count", description = "Returns the total number of orders in the system")
    @GetMapping("/count")
    public ResponseEntity<StandardResponse<Long>> getOrderCount() {
        long count = orderService.getOrderCount();
        return responseFactory.okResponse(count, "Order count retrieved successfully", ADMIN_ORDERS_BASE_PATH + "/count");
    }
}
