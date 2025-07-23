package br.com.ecommerce.ecom.controller;

import br.com.ecommerce.ecom.dto.responses.OrderResponseDTO;
import br.com.ecommerce.ecom.dto.responses.StandardResponse;
import br.com.ecommerce.ecom.entity.Order;
import br.com.ecommerce.ecom.entity.User;
import br.com.ecommerce.ecom.factory.ResponseFactory;
import br.com.ecommerce.ecom.mappers.OrderMapper;
import br.com.ecommerce.ecom.service.LocalUserService;
import br.com.ecommerce.ecom.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
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
@Tag(name = "Orders", description = "Endpoints for managing orders for authenticated users")
public class OrderController {

    private static final String ORDER_BASE_PATH = "/api/orders";

    private final OrderService orderService;
    private final LocalUserService userService;
    private final ResponseFactory responseFactory;
    private final OrderMapper orderMapper;

    @Operation(
            summary = "Checkout",
            description = "Converts the current cart into an order for the authenticated user."
    )
    @ApiResponse(responseCode = "201", description = "Order created successfully",
            content = @Content(schema = @Schema(implementation = OrderResponseDTO.class)))
    @PostMapping("/checkout")
    public ResponseEntity<StandardResponse<OrderResponseDTO>> checkout(
            @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt) {

        User user = userService.getUserByEmail(jwt.getClaim("email"));
        Order order = orderService.checkout(user);
        OrderResponseDTO dto = orderMapper.toResponseDTO(order);

        return responseFactory.createdResponse(
                dto,
                "Order created successfully",
                ORDER_BASE_PATH + "/" + order.getId()
        );
    }

    @Operation(
            summary = "Cancel order",
            description = "Cancels an order if it is still in a cancellable state."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order cancelled successfully",
                    content = @Content(schema = @Schema(implementation = OrderResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Order cannot be cancelled", content = @Content),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content)
    })
    @PutMapping("/{id}/cancel")
    public ResponseEntity<StandardResponse<OrderResponseDTO>> cancelOrder(
            @Parameter(description = "Order ID", example = "1") @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt) {

        User user = userService.getUserByEmail(jwt.getClaim("email"));
        Order order = orderService.cancelOrderIfAllowed(id, user);
        OrderResponseDTO response = orderMapper.toResponseDTO(order);

        return responseFactory.okResponse(
                response,
                "Order cancelled",
                ORDER_BASE_PATH + "/" + id + "/cancel"
        );
    }

    @Operation(
            summary = "List user orders",
            description = "Returns a paginated list of orders for the authenticated user."
    )
    @ApiResponse(responseCode = "200", description = "Orders fetched successfully")
    @GetMapping
    public ResponseEntity<StandardResponse<Page<OrderResponseDTO>>> getUserOrders(
            @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
            @ParameterObject @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        User user = userService.getUserByEmail(jwt.getClaim("email"));
        Page<Order> page = orderService.getOrdersByUserFiltered(user, pageable);
        Page<OrderResponseDTO> response = page.map(orderMapper::toResponseDTO);

        return responseFactory.okResponse(
                response,
                "Orders fetched successfully",
                ORDER_BASE_PATH
        );
    }

    @Operation(
            summary = "Get order by ID",
            description = "Retrieves a single order for the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order fetched successfully",
                    content = @Content(schema = @Schema(implementation = OrderResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse<OrderResponseDTO>> getOrderById(
            @Parameter(description = "Order ID", example = "1") @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt) {

        User user = userService.getUserByEmail(jwt.getClaim("email"));
        Order order = orderService.getOrderByIdAndUser(id, user);
        OrderResponseDTO response = orderMapper.toResponseDTO(order);

        return responseFactory.okResponse(
                response,
                "Order fetched successfully",
                ORDER_BASE_PATH + "/" + id
        );
    }
}
