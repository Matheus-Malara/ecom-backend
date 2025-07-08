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

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {
    private static final String LOG_USER_CART_ACTION = "Cart (user={}, anonId={}) action: {}";

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductService productService;

    @Transactional(readOnly = true)
    public Cart getOrCreateCart(String anonymousId, User user) {
        if (user != null) {
            return findCurrentCart(user).orElseGet(() -> createNewCart(user));
        } else {
            return findCurrentCart(anonymousId).orElseGet(() -> createNewAnonymousCart(anonymousId));
        }
    }

    @Transactional(readOnly = true)
    public Optional<Cart> findCurrentCart(User user) {
        return cartRepository.findByUserAndCheckedOutFalse(user)
                .map(this::initializeCartItems);
    }

    @Transactional(readOnly = true)
    public Optional<Cart> findCurrentCart(String anonymousId) {
        return cartRepository.findByAnonymousIdAndCheckedOutFalse(anonymousId)
                .map(this::initializeCartItems);
    }

    @Transactional
    public Cart addItemToCart(AddToCartRequestDTO dto, String anonymousId, User user) {
        Cart cart = getOrCreateCart(anonymousId, user);
        Product product = productService.getExistingProduct(dto.getProductId());

        cartItemRepository.findByCartAndProduct(cart, product)
                .ifPresentOrElse(
                        item -> updateExistingCartItem(item, dto.getQuantity()),
                        () -> addNewCartItem(cart, product, dto.getQuantity())
                );

        logCartAction(user, anonymousId, "Added/Updated product " + product.getId());
        return cart;
    }

    @Transactional
    public Cart updateItemQuantity(String anonymousId, User user, Long productId, int quantity) {
        Cart cart = getOrCreateCart(anonymousId, user);
        Product product = productService.getExistingProduct(productId);

        CartItem item = findCartItemOrThrow(cart, product);
        item.setQuantity(quantity);

        logCartAction(user, anonymousId, "Updated quantity of product " + productId);
        return cart;
    }

    @Transactional
    public Cart removeItem(String anonymousId, User user, Long productId) {
        Cart cart = getOrCreateCart(anonymousId, user);
        Product product = productService.getExistingProduct(productId);

        CartItem item = findCartItemOrThrow(cart, product);
        cart.getItems().remove(item);

        logCartAction(user, anonymousId, "Removed product " + productId);
        return cart;
    }

    @Transactional
    public void clearCart(String anonymousId, User user) {
        Optional<Cart> optionalCart = user != null
                ? findCurrentCart(user)
                : findCurrentCart(anonymousId);

        optionalCart.ifPresent(cart -> {
            cart.getItems().clear();
            logCartAction(user, anonymousId, "Cart cleared");
        });
    }


    private Cart createNewCart(User user) {
        Cart newCart = Cart.builder()
                .user(user)
                .checkedOut(false)
                .items(new ArrayList<>())
                .build();
        logCartAction(user, null, "Created new cart");
        return cartRepository.save(newCart);
    }

    private Cart createNewAnonymousCart(String anonymousId) {
        Cart newCart = Cart.builder()
                .anonymousId(anonymousId)
                .checkedOut(false)
                .items(new ArrayList<>())
                .build();
        logCartAction(null, anonymousId, "Created new anonymous cart");
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
    }


    private void addNewCartItem(Cart cart, Product product, int quantity) {
        CartItem newItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(quantity)
                .build();

        cart.getItems().add(newItem);
    }

    private CartItem findCartItemOrThrow(Cart cart, Product product) {
        return cartItemRepository.findByCartAndProduct(cart, product)
                .orElseThrow(() -> new CartItemNotFoundException(product.getId()));
    }

    private void logCartAction(User user, String anonymousId, String action) {
        log.info(LOG_USER_CART_ACTION, user != null ? user.getEmail() : "null", anonymousId, action);
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

    @Transactional
    public void mergeAnonymousCartIfExists(String anonymousId, User user) {
        Optional<Cart> anonymousCartOpt = cartRepository.findByAnonymousIdAndCheckedOutFalse(anonymousId);
        Optional<Cart> userCartOpt = cartRepository.findByUserAndCheckedOutFalse(user);

        if (anonymousCartOpt.isEmpty()) {

            userCartOpt.orElseGet(() -> createNewCart(user));
            return;
        }

        Cart anonymousCart = anonymousCartOpt.get();

        if (userCartOpt.isEmpty()) {

            anonymousCart.setUser(user);
            anonymousCart.setAnonymousId(null);
            cartRepository.save(anonymousCart);
            return;
        }

        Cart userCart = userCartOpt.get();

        Map<Long, CartItem> userItemMap = userCart.getItems().stream()
                .collect(Collectors.toMap(
                        item -> item.getProduct().getId(),
                        item -> item
                ));

        for (CartItem anonItem : anonymousCart.getItems()) {
            Long productId = anonItem.getProduct().getId();

            if (userItemMap.containsKey(productId)) {

                CartItem existingItem = userItemMap.get(productId);
                existingItem.setQuantity(existingItem.getQuantity() + anonItem.getQuantity());
                cartItemRepository.save(existingItem);
            } else {

                CartItem newItem = CartItem.builder()
                        .cart(userCart)
                        .product(anonItem.getProduct())
                        .quantity(anonItem.getQuantity())
                        .build();
                cartItemRepository.save(newItem);
                userCart.getItems().add(newItem);
            }
        }

        cartRepository.delete(anonymousCart);
    }
}
