package br.com.ecommerce.ecom.controller;

import br.com.ecommerce.ecom.dto.requests.AddToCartRequestDTO;
import br.com.ecommerce.ecom.dto.requests.UpdateQuantityDTO;
import br.com.ecommerce.ecom.dto.responses.CartResponseDTO;
import br.com.ecommerce.ecom.dto.responses.StandardResponse;
import br.com.ecommerce.ecom.entity.Cart;
import br.com.ecommerce.ecom.entity.User;
import br.com.ecommerce.ecom.factory.ResponseFactory;
import br.com.ecommerce.ecom.mappers.CartMapper;
import br.com.ecommerce.ecom.service.CartService;
import br.com.ecommerce.ecom.service.LocalUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {

    private static final String CART_BASE_PATH = "/api/cart";

    private final CartService cartService;
    private final LocalUserService userService;
    private final ResponseFactory responseFactory;
    private final CartMapper cartMapper;

    @Operation(summary = "Add item to cart", description = "Adds a product to the authenticated user's cart.")
    @PostMapping("/items")
    public ResponseEntity<StandardResponse<CartResponseDTO>> addToCart(
            @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid AddToCartRequestDTO dto) {

        User user = userService.getUserByEmail(jwt.getClaim("email"));
        Cart cart = cartService.addItemToCart(dto, user);
        CartResponseDTO response = cartMapper.toResponse(cart);
        return responseFactory.okResponse(response, "Item added to cart", CART_BASE_PATH + "/items");
    }

    @Operation(summary = "Update cart item quantity", description = "Updates the quantity of a product in the user's cart.")
    @PutMapping("/items/{productId}")
    public ResponseEntity<StandardResponse<CartResponseDTO>> updateQuantity(
            @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long productId,
            @RequestBody @Valid UpdateQuantityDTO dto) {

        User user = userService.getUserByEmail(jwt.getClaim("email"));
        Cart cart = cartService.updateItemQuantity(user, productId, dto.getQuantity());
        CartResponseDTO response = cartMapper.toResponse(cart);
        return responseFactory.okResponse(response, "Item quantity updated", CART_BASE_PATH + "/items/" + productId);
    }

    @Operation(summary = "Remove item from cart", description = "Removes a product from the user's cart.")
    @DeleteMapping("/items/{productId}")
    public ResponseEntity<StandardResponse<CartResponseDTO>> removeFromCart(
            @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
            @Parameter(description = "ID of the product to remove", example = "1") @PathVariable Long productId) {

        User user = userService.getUserByEmail(jwt.getClaim("email"));
        Cart cart = cartService.removeItem(user, productId);
        CartResponseDTO response = cartMapper.toResponse(cart);
        return responseFactory.okResponse(response, "Item removed from cart", CART_BASE_PATH + "/items/" + productId);
    }

    @Operation(summary = "Get current cart", description = "Retrieves the current cart of the authenticated user.")
    @GetMapping
    public ResponseEntity<StandardResponse<CartResponseDTO>> getCart(
            @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt) {

        User user = userService.getUserByEmail(jwt.getClaim("email"));
        Optional<Cart> cartOpt = cartService.findCurrentCart(user);

        if (cartOpt.isEmpty()) {
            return responseFactory.noContentResponse("Cart is empty", CART_BASE_PATH);
        }

        CartResponseDTO response = cartMapper.toResponse(cartOpt.get());
        return responseFactory.okResponse(response, "Cart fetched successfully", CART_BASE_PATH);
    }
}