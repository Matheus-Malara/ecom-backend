package br.com.ecommerce.ecom.service;

import br.com.ecommerce.ecom.entity.Cart;
import br.com.ecommerce.ecom.entity.CartItem;
import br.com.ecommerce.ecom.entity.Order;
import br.com.ecommerce.ecom.entity.OrderItem;
import br.com.ecommerce.ecom.entity.Product;
import br.com.ecommerce.ecom.entity.User;
import br.com.ecommerce.ecom.enums.OrderStatus;
import br.com.ecommerce.ecom.exception.EmptyCartException;
import br.com.ecommerce.ecom.exception.InvalidOrderStatusException;
import br.com.ecommerce.ecom.exception.OrderNotFoundException;
import br.com.ecommerce.ecom.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;

    // Define the valid transitions for each status
    private static final Map<OrderStatus, Set<OrderStatus>> VALID_TRANSITIONS = Map.of(
            OrderStatus.PENDING, Set.of(OrderStatus.PAID, OrderStatus.CANCELLED),
            OrderStatus.PAID, Set.of(OrderStatus.PROCESSING, OrderStatus.CANCELLED),
            OrderStatus.PROCESSING, Set.of(OrderStatus.SHIPPED),
            OrderStatus.SHIPPED, Set.of(OrderStatus.DELIVERED),
            OrderStatus.DELIVERED, Set.of(),
            OrderStatus.CANCELLED, Set.of()
    );

    /**
     * Checkout: Converts the user's active cart into an order.
     */
    @Transactional
    public Order checkout(User user) {
        Cart cart = cartService.findActiveCartByUser(user);

        if (cart.getItems().isEmpty()) {
            throw new EmptyCartException();
        }

        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            BigDecimal unitPrice = product.getPrice();
            int quantity = cartItem.getQuantity();

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(quantity)
                    .unitPrice(unitPrice)
                    .build();

            total = total.add(unitPrice.multiply(BigDecimal.valueOf(quantity)));
            orderItems.add(orderItem);
        }


        LocalDateTime now = LocalDateTime.now();

        Order order = Order.builder()
                .user(user)
                .totalAmount(total)
                .status(OrderStatus.PENDING)
                .createdAt(now)
                .updatedAt(now)
                .build();

        order.setItems(orderItems);
        orderItems.forEach(item -> item.setOrder(order));
        orderRepository.save(order);

        cartService.setCheckoutCartToTrue(cart);

        log.info("Order {} created for user {}", order.getId(), user.getEmail());

        return order;
    }

    /**
     * Admin or system-level update of order status (no user check).
     */
    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = getOrderById(orderId);
        validateTransition(order.getStatus(), newStatus);

        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());

        log.info("Order {} status changed from {} to {}", order.getId(), order.getStatus(), newStatus);

        return orderRepository.save(order);
    }

    /**
     * List all orders of a specific user.
     */
    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUserOrderByCreatedAtDesc(user);
    }

    /**
     * Get a single order by ID, verifying ownership.
     */
    public Order getOrderByIdAndUser(Long orderId, User user) {
        return orderRepository.findByIdAndUser(orderId, user)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    /**
     * Admin fetch of any order.
     */
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    /**
     * Admin fetch of all orders.
     */
    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * Validates if a status change is allowed.
     */
    private void validateTransition(OrderStatus current, OrderStatus target) {
        Set<OrderStatus> allowed = VALID_TRANSITIONS.getOrDefault(current, Set.of());
        if (!allowed.contains(target)) {
            throw new InvalidOrderStatusException(
                    "Cannot change order status from " + current + " to " + target
            );
        }
    }

    @Transactional
    public Order cancelOrderIfAllowed(Long orderId, User user) {
        Order order = getOrderByIdAndUser(orderId, user);
        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.PAID) {
            throw new InvalidOrderStatusException("You can only cancel orders that are PENDING or PAID.");
        }
        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }
}
