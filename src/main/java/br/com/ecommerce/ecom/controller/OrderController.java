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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final LocalUserService userService;
    private final ResponseFactory responseFactory;
    private final OrderMapper orderMapper;

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> checkout(@AuthenticationPrincipal Jwt jwt) {
        User user = userService.getUserByEmail(jwt.getClaim("email"));
        Order order = orderService.checkout(user);
        OrderResponseDTO dto = orderMapper.toResponseDTO(order);
        return responseFactory.createdResponse(dto, "Order created successfully", "/api/orders/" + order.getId());
    }
}