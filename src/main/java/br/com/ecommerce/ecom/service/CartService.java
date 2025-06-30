package br.com.ecommerce.ecom.service;

import br.com.ecommerce.ecom.dto.requests.AddToCartRequestDTO;
import br.com.ecommerce.ecom.entity.Cart;
import br.com.ecommerce.ecom.entity.CartItem;
import br.com.ecommerce.ecom.entity.Product;
import br.com.ecommerce.ecom.entity.User;
import br.com.ecommerce.ecom.exception.ActiveCartNotFoundException;
import br.com.ecommerce.ecom.exception.CartItemNotFoundException;
import br.com.ecommerce.ecom.repository.CartItemRepository;
import br.com.ecommerce.ecom.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {
    private static final String LOG_USER_CART_ACTION = "User {} cart action: {}";

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductService productService;

    @Transactional(readOnly = true)
    public Cart getOrCreateCart(User user) {
        return findCurrentCart(user)
                .orElseGet(() -> createNewCart(user));
    }

    @Transactional(readOnly = true)
    public Optional<Cart> findCurrentCart(User user) {
        return cartRepository.findByUserAndCheckedOutFalse(user)
                .map(this::initializeCartItems);
    }

    @Transactional
    public Cart addItemToCart(AddToCartRequestDTO dto, User user) {
        Cart cart = getOrCreateCart(user);
        Product product = productService.getExistingProduct(dto.getProductId());

        cartItemRepository.findByCartAndProduct(cart, product)
                .ifPresentOrElse(
                        item -> updateExistingCartItem(item, dto.getQuantity()),
                        () -> addNewCartItem(cart, product, dto.getQuantity())
                );

        logCartAction(user, "Added/Updated product " + product.getId());
        return updateCartTimestamp(cart);
    }

    @Transactional
    public Cart updateItemQuantity(User user, Long productId, int quantity) {
        Cart cart = getOrCreateCart(user);
        Product product = productService.getExistingProduct(productId);

        CartItem item = findCartItemOrThrow(cart, product);
        item.setQuantity(quantity);
        cartItemRepository.save(item);

        logCartAction(user, "Updated quantity of product " + productId);
        return updateCartTimestamp(cart);
    }


    @Transactional
    public Cart removeItem(User user, Long productId) {
        Cart cart = getOrCreateCart(user);
        Product product = productService.getExistingProduct(productId);

        CartItem item = findCartItemOrThrow(cart, product);
        cartItemRepository.delete(item);

        logCartAction(user, "Removed product " + productId);
        return updateCartTimestamp(cart);
    }

    private Cart createNewCart(User user) {
        Cart newCart = Cart.builder()
                .user(user)
                .checkedOut(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();
        logCartAction(user, "Created new cart");
        return cartRepository.save(newCart);
    }

    private Cart initializeCartItems(Cart cart) {
        if (cart.getItems() == null) {
            cart.setItems(new ArrayList<>());
        }
        return cart;
    }

    private void updateExistingCartItem(CartItem item, int quantityToAdd) {
        item.setQuantity(item.getQuantity() + quantityToAdd);
        cartItemRepository.save(item);
    }

    private void addNewCartItem(Cart cart, Product product, int quantity) {
        CartItem newItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(quantity)
                .build();
        cartItemRepository.save(newItem);
        cart.getItems().add(newItem);
    }

    private CartItem findCartItemOrThrow(Cart cart, Product product) {
        return cartItemRepository.findByCartAndProduct(cart, product)
                .orElseThrow(() -> new CartItemNotFoundException(product.getId()));
    }

    private Cart updateCartTimestamp(Cart cart) {
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }

    private void logCartAction(User user, String action) {
        log.info(LOG_USER_CART_ACTION, user.getEmail(), action);
    }

    public Cart findActiveCartByUser(User user) {
        return cartRepository.findByUserAndCheckedOutFalse(user)
                .orElseThrow(() -> new ActiveCartNotFoundException(user.getEmail()));
    }

    @Transactional
    public void setCheckoutCartToTrue(Cart cart) {
        cart.setCheckedOut(true);
        cartRepository.save(cart);
    }

}