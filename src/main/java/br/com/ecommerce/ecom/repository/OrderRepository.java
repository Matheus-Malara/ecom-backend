package br.com.ecommerce.ecom.repository;

import br.com.ecommerce.ecom.entity.Order;
import br.com.ecommerce.ecom.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    Page<Order> findByUser(User user, Pageable pageable);


    Optional<Order> findByIdAndUser(Long id, User user);

}
