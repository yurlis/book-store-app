package com.bookstoreapp.repository.shoppingcart;

import com.bookstoreapp.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByShoppingCartIdAndBookId(Long idShoppingCart,
                                                     Long idBook);
}
