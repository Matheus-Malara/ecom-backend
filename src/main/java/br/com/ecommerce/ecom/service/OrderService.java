package br.com.ecommerce.ecom.service;

import br.com.ecommerce.ecom.entity.Cart;
import br.com.ecommerce.ecom.entity.CartItem;
import br.com.ecommerce.ecom.entity.Order;
import br.com.ecommerce.ecom.entity.OrderItem;
import br.com.ecommerce.ecom.entity.Product;
import br.com.ecommerce.ecom.entity.User;
import br.com.ecommerce.ecom.enums.OrderStatus;
import br.com.ecommerce.ecom.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;

    @Transactional
    public Order checkout(User user) {
        Cart cart = cartService.findActiveCartByUser(user);

        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
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

        Order order = Order.builder()
                .user(user)
                .totalAmount(total)
                .status(OrderStatus.PENDING)
                .build();

        order.setItems(orderItems);
        orderItems.forEach(item -> item.setOrder(order));
        orderRepository.save(order);

        cartService.setCheckoutCartToTrue(cart);

        log.info("Order {} created for user {}", order.getId(), user.getEmail());

        return order;
    }
}