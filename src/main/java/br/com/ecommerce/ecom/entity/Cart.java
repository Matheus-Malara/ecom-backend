package br.com.ecommerce.ecom.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@Entity
@Table(name = "carts")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "user_id")
    @OneToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private User user;


    @Column(name = "anonymous_id", length = 64)
    private String anonymousId;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    private List<CartItem> items = new ArrayList<>();

    @Builder.Default
    private boolean checkedOut = false;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        String itemSummary = items != null
                ? items.stream()
                .map(i -> String.format("{productId=%d, qty=%d}",
                        i.getProduct() != null ? i.getProduct().getId() : null,
                        i.getQuantity()))
                .toList()
                .toString()
                : "[]";

        return "Cart(id=" + id +
                ", userEmail=" + (user != null ? user.getEmail() : "null") +
                ", checkedOut=" + checkedOut +
                ", items=" + itemSummary +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ")";
    }

}