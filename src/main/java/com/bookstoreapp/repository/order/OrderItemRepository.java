package com.bookstoreapp.repository.order;

import com.bookstoreapp.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findAllByOrderId(@Param("orderId") Long orderId);

    Optional<OrderItem> findByIdAndOrderId(@Param("orderId") Long orderId,
                                           @Param("itemId") Long itemId);
}
