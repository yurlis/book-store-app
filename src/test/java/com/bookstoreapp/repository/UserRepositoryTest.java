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
@Sql(scripts = {"classpath:database/users/repository/add-data.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"classpath:database/users/repository/delete-data.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Test
    @DisplayName("Find user by email when user exists, should return the user")
    void findUserByEmail_UserExists_ReturnsUser() {
        // given
        String email = "testuser@example.com";

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
        Long userId = 17L;

        // when
        Optional<User> result = userRepository.findById(userId);

        // then
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(userId, result.get().getId());
    }

    @Test
    @DisplayName("Find user by ID when user does not exist, should return empty")
    void findById_UserDoesNotExist_ReturnsEmpty() {
        // given
        Long userId = 100L;

        // when
        Optional<User> result = userRepository.findById(userId);

        // then
        Assertions.assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Find roles by username when user exists, should return a set of roles")
    void findRolesByUsername_UserExists_ReturnsSetOfRoles() {
        // given
        String email = "testuser@example.com";
        Set<Role> expectedRoles = Set.of(
                roleRepository.getRoleByRoleType(Role.RoleType.ROLE_USER),
                roleRepository.getRoleByRoleType(Role.RoleType.ROLE_ADMIN)
        );

        // when
        Set<Role> result = userRepository.findRolesByUsername(email);

        // then
        Assertions.assertEquals(expectedRoles, result);
    }
}