package com.bookstoreapp.repository.shoppingcart;

import com.bookstoreapp.model.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {

    @Query("SELECT cart FROM ShoppingCart cart "
            + "LEFT JOIN FETCH cart.user "
            + "LEFT JOIN FETCH cart.cartItems items "
            + "LEFT JOIN FETCH items.book "
            + "WHERE :userId = cart.user.id ")
    Optional<ShoppingCart> findByUserId(Long userId);
}
