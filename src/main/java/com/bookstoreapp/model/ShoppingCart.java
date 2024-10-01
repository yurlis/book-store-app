package com.bookstoreapp.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.HashSet;
import java.util.Set;

@Entity
@SQLRestriction("is_deleted = FALSE")
@SQLDelete(sql = "UPDATE shopping_carts SET is_deleted = TRUE WHERE id = ?")
@Table(name = "shopping_carts")
@NoArgsConstructor
@Getter
@Setter
public class ShoppingCart {
    @Id
    private Long id;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @MapsId
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "shoppingCart", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CartItem> cartItems = new HashSet<>();

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;
}
