package com.bookstoreapp.service;

import com.bookstoreapp.dto.user.UserRegistrationRequestDto;
import com.bookstoreapp.dto.user.UserRegistrationResponseDto;
import com.bookstoreapp.exception.RegistrationException;
import com.bookstoreapp.mapper.UserMapper;
import com.bookstoreapp.model.Role;
import com.bookstoreapp.model.User;
import com.bookstoreapp.repository.role.RoleRepository;
import com.bookstoreapp.repository.user.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserMapper userMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("User registration successfully returns user DTO")
    void register_UserRegistrationRequest_ReturnsUserResponseDto() {
        // Given
        UserRegistrationRequestDto request = new UserRegistrationRequestDto();
        request.setEmail("test@test.com");
        request.setPassword("password1234!");
        request.setRepeatPassword("password1234!");
        request.setFirstName("TestName");
        request.setLastName("TestLastName");

        Role userRole = new Role();
        userRole.setRole(Role.RoleType.ROLE_USER);

        User user = new User();
        user.setDeleted(false);
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPassword("hashedPassword");

        UserRegistrationResponseDto userResponseDto = new UserRegistrationResponseDto();
        userResponseDto.setEmail(request.getEmail());
        userResponseDto.setId(1L);

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(userMapper.toUserModel(request)).thenReturn(user);
        Mockito.lenient().when(passwordEncoder.encode(any())).thenReturn("hashedPassword");
        Mockito.lenient().when(roleRepository.findRoleByRole(Role.RoleType.ROLE_USER)).thenReturn(Optional.of(userRole));
        when(userRepository.save(any(User.class))).thenReturn(user);
        Mockito.lenient().when(userMapper.toRegistrationResponseDto(user)).thenReturn(userResponseDto);

        // When
        UserRegistrationResponseDto result = null;
        try {
            result = userService.register(request);
        } catch (RegistrationException e) {
            fail("RegistrationException was thrown, but it should not have been.");
        }

        // Then
        Assertions.assertEquals(userResponseDto, result);
    }

    @Test
    @DisplayName("User registration for an existing user throws RegistrationException")
    void register_UserAlreadyExists_ThrowsRegistrationException() {
        // Given
        UserRegistrationRequestDto request = new UserRegistrationRequestDto();
        request.setEmail("test@test.com");
        request.setPassword("password1234!");
        request.setRepeatPassword("password1234!");
        request.setFirstName("TestName");
        request.setLastName("TestLastName");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(new User()));

        // When
        RegistrationException exception = assertThrows(RegistrationException.class, () -> {
            userService.register(request);
        });

        // Then
        assertNotNull(exception);
        verify(userRepository).findByEmail(request.getEmail());
    }

//    @Test
//    @DisplayName("Finding user by email returns the user")
//    void findByEmail_ValidEmail_ReturnsUser() {
//        // Given
//        Role userRole = new Role();
//        userRole.setRole(Role.RoleType.ROLE_USER);
//
//        User user = new User();
//        user.setDeleted(false);
//        user.setPassword("hashedPassword");
//        user.setEmail("test@test.com");
//        user.setFirstName("TestName");
//        user.setLastName("TestLastName");
//        user.setRoles(Set.of(userRole));
//
//        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
//
//        // When
//        User result = userService.findByEmail(user.getEmail());
//
//        // Then
//        Assertions.assertEquals(user, result);
//    }
}
