package com.bookstoreapp.repository;

import com.bookstoreapp.model.Order;
import com.bookstoreapp.model.User;
import com.bookstoreapp.repository.order.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {"classpath:database/orders/repository/add-orders-data.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"classpath:database/orders/repository/delete-orders-data.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    @DisplayName("Find all orders by user ID - If user exists, should return a list of orders")
    void findAllByUserId_UserExists_ReturnsListOfOrders() {
        // given
        User user = new User();
        user.setId(1L);
        user.setEmail("testuser@example.com");
        user.setPassword("password123");
        user.setFirstName("TestName");
        user.setLastName("TestLastName");
        user.setDeleted(false);
        int expectedOrdersCount = 1;

        // when
        List<Order> resultList = orderRepository.findAllByUserId(user.getId(), Pageable.unpaged());

        // then
        Assertions.assertEquals(expectedOrdersCount, resultList.size(),
                "If the user exists, the method should return a list with the expected number of orders");
    }
}
