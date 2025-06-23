package br.com.ecommerce.ecom.repository;

import br.com.ecommerce.ecom.entity.Order;
import br.com.ecommerce.ecom.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    List<Order> findByUserOrderByCreatedAtDesc(User user);

    Optional<Order> findByIdAndUser(Long id, User user);

    List<Order> findAllByOrderByCreatedAtDesc();


}
