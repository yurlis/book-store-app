package com.bookstoreapp.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@SQLRestriction("is_deleted = FALSE")
@SQLDelete(sql = "UPDATE orders SET is_deleted = TRUE WHERE id = ?")
@Table(name = "orders")
@NoArgsConstructor
@Getter
@Setter
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderItem> orderItems;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "total", nullable = false)
    private BigDecimal total;

    @Column(name = "status",
            nullable = false,
            columnDefinition = "varchar")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "shipping_address", nullable = false)
    private String shippingAddress;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    public enum Status {
        COMPLETED,
        PENDING,
        DELIVERED,
        CANCELED,
        POSTPONED
    }
}
