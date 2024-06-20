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

import java.util.Set;

@Entity
@SQLRestriction("is_deleted=false")
@SQLDelete(sql = "UPDATE shoppingcarts SET is_deleted = true WHERE id=?")
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

    @OneToMany(mappedBy = "shoppingCart")
    private Set<CartItem> cartItems;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;
}
