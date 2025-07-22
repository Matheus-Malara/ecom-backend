package br.com.ecommerce.ecom.repository;

import br.com.ecommerce.ecom.entity.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(@NotBlank(message = "Email is required") String email);

    Optional<User> findByKeycloakUserId(String keycloakId);
}
