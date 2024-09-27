package com.bookstoreapp.repository;

import com.bookstoreapp.model.Role;
import com.bookstoreapp.model.User;
import com.bookstoreapp.repository.role.RoleRepository;
import com.bookstoreapp.repository.user.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;
import java.util.Set;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {"classpath:database/users/repository/add-users-data.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"classpath:database/users/repository/delete-users-data.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;

    @Test
    @DisplayName("Find user by email when user exists, should return the user")
    void findByEmail_UserExists_ReturnsUser() {
        // given
        String email = "testuser1@test.com";

        // when
        Optional<User> result = userRepository.findByEmail(email);

        // then
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(email, result.get().getEmail());
    }

    @Test
    @DisplayName("Find user by ID when user exists, should return the user")
    void findById_UserExists_ReturnsUser() {
        // given
        Long userId = 1L;

        // when
        Optional<User> result = userRepository.findById(userId);

        // then
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(userId, result.get().getId());
    }

    @Test
    @DisplayName("Find user by ID when user does not exist, should return empty")
    void findByUserId_WhenUserDoesNotExist_ShouldReturnEmpty()  {
        // given
        Long userId = 999L;

        // when
        Optional<User> result = userRepository.findById(userId);

        // then
        Assertions.assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Find roles by username when user exists, should return a set of roles")
    void findRolesByUsername_UserExists_ReturnsSetOfRoles()  {
        // given
        String email = "testuser1@test.com";
        Set<Role> expectedRoles = Set.of(
                roleRepository.findRoleByRole(Role.RoleType.ROLE_USER).get(),
                roleRepository.findRoleByRole(Role.RoleType.ROLE_ADMIN).get()
        );

        // when
        Set<Role> result = userRepository.findRolesByUsername(email);

        // then
        Assertions.assertEquals(expectedRoles, result);
    }
}
