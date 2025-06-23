package br.com.ecommerce.ecom.controller;

import br.com.ecommerce.ecom.dto.responses.ApiResponse;
import br.com.ecommerce.ecom.dto.responses.OrderResponseDTO;
import br.com.ecommerce.ecom.entity.Order;
import br.com.ecommerce.ecom.entity.User;
import br.com.ecommerce.ecom.factory.ResponseFactory;
import br.com.ecommerce.ecom.mappers.OrderMapper;
import br.com.ecommerce.ecom.service.LocalUserService;
import br.com.ecommerce.ecom.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private static final String ORDER_BASE_PATH = "/api/orders";

    private final OrderService orderService;
    private final LocalUserService userService;
    private final ResponseFactory responseFactory;
    private final OrderMapper orderMapper;

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> checkout(@AuthenticationPrincipal Jwt jwt) {
        User user = userService.getUserByEmail(jwt.getClaim("email"));
        Order order = orderService.checkout(user);
        OrderResponseDTO dto = orderMapper.toResponseDTO(order);
        return responseFactory.createdResponse(dto, "Order created successfully", ORDER_BASE_PATH + order.getId());
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> cancelOrder(@PathVariable Long id,
                                                                     @AuthenticationPrincipal Jwt jwt) {
        User user = userService.getUserByEmail(jwt.getClaim("email"));
        Order order = orderService.cancelOrderIfAllowed(id, user);
        OrderResponseDTO response = orderMapper.toResponseDTO(order);
        return responseFactory.okResponse(response, "Order cancelled", ORDER_BASE_PATH + id);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<OrderResponseDTO>>> getUserOrders(
            @AuthenticationPrincipal Jwt jwt,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        User user = userService.getUserByEmail(jwt.getClaim("email"));
        Page<Order> page = orderService.getOrdersByUserFiltered(user, pageable);
        Page<OrderResponseDTO> response = page.map(orderMapper::toResponseDTO);

        return responseFactory.okResponse(response, "Orders fetched successfully", ORDER_BASE_PATH);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> getOrderById(@PathVariable Long id,
                                                                      @AuthenticationPrincipal Jwt jwt) {
        User user = userService.getUserByEmail(jwt.getClaim("email"));
        Order order = orderService.getOrderByIdAndUser(id, user);
        OrderResponseDTO response = orderMapper.toResponseDTO(order);

        return responseFactory.okResponse(response, "Order fetched successfully", ORDER_BASE_PATH + id);
    }
}
