package com.bookstoreapp.repository;

import com.bookstoreapp.model.OrderItem;
import com.bookstoreapp.repository.order.OrderItemRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {"classpath:database/orders/repository/add-orders-data.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"classpath:database/orders/repository/delete-orders-data.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class OrderItemRepositoryTest {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Test
    @DisplayName("Find all order items by order ID - should return a list of OrderItems")
    void findAllByOrderId_OrderExists_ReturnsListOfOrderItems() {
        // Given
        Long orderId = 1L;
        int expectedOrderItemCount = 3;

        // When
        List<OrderItem> resultList = orderItemRepository.findAllByOrderId(orderId);

        // Then
        Assertions.assertNotNull(resultList, "The result list should not be null");
        Assertions.assertEquals(expectedOrderItemCount, resultList.size(),
                "The number of order items should match the expected value.");
    }
}
