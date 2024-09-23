package com.bookstoreapp.repository;

import com.bookstoreapp.model.ShoppingCart;
import com.bookstoreapp.model.User;
import com.bookstoreapp.repository.shoppingcart.ShoppingCartRepository;
import com.bookstoreapp.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.Optional;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ShoppingCartRepositoryTest {
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;
    @Autowired
    private UserRepository userRepository;
    private ShoppingCart shoppingCart;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword("password");
        user.setEmail("test@mail.com");
        user = userRepository.save(user);

        shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCart.setCartItems(Set.of());
        shoppingCartRepository.save(shoppingCart);
    }

    @Test
    @DisplayName("Find shopping cart by user ID - Found")
    void findByUserId_UserExists_ReturnsShoppingCart() {
        // Given
        Long userId = user.getId();

        // When
        Optional<ShoppingCart> result = shoppingCartRepository.findByUserId(userId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getUser().getId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("Find shopping cart by user ID - Not Found")
    void findByUserId_UserDoesNotExist_ReturnsEmpty() {
        // Given
        Long userNotFoundId = 999L;

        // When
        Optional<ShoppingCart> result = shoppingCartRepository.findByUserId(userNotFoundId);

        // Then
        assertThat(result).isNotPresent();
    }
}