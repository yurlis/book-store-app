package com.bookstoreapp.repository.order;

import com.bookstoreapp.model.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o "
            + "LEFT JOIN FETCH o.user user "
            + "LEFT JOIN FETCH o.orderItems items "
            + "WHERE user.id = :userId")
    List<Order> findAllByUserId(Long userId, Pageable pageable);

    @Query("SELECT o FROM Order o "
            + "LEFT JOIN FETCH o.user user "
            + "LEFT JOIN FETCH o.orderItems items "
            + "WHERE user.id = :userId "
            + "AND o.id = :orderId")
    Optional<Order> findByIdAndUserId(Long orderId, Long userId);
}
