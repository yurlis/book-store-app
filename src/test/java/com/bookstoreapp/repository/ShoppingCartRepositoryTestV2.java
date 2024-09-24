package com.bookstoreapp.repository;

import com.bookstoreapp.model.ShoppingCart;
import com.bookstoreapp.model.User;
import com.bookstoreapp.repository.shoppingcart.ShoppingCartRepository;
import com.bookstoreapp.repository.user.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {
        "classpath:database/shopping-carts/repository/add-repository-data.sql",
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {
        "classpath:database/shopping-carts/repository/delete-repository-data.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class ShoppingCartRepositoryTestV2 {

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("Find shopping cart by user ID when user exists")
    @Test
    void findByUserId_WhenUserExists_ShouldReturnShoppingCart() {
        // given
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setEmail("testuser@example.com");
        user.setFirstName("TestUserName");
        user.setLastName("TestUserLastName");
        user.setPassword("password123");
        user.setDeleted(false);
        userRepository.save(user);

        // when
        Optional<ShoppingCart> result = shoppingCartRepository.findByUserId(userId);

        // then
        Assertions.assertTrue(result.isPresent());
        ShoppingCart shoppingCart = result.get();
        Assertions.assertNotNull(shoppingCart);
        Assertions.assertEquals(userId, shoppingCart.getUser().getId());
    }

    @DisplayName("Find shopping cart by user ID when user does not exist")
    @Test
    void findByUserId_WhenUserDoesNotExist_ShouldReturnEmpty()  {
        // given
        Long userId = 999L;

        // when
        Optional<ShoppingCart> result = shoppingCartRepository.findByUserId(userId);

        // then
        Assertions.assertTrue(result.isEmpty());
    }
}
