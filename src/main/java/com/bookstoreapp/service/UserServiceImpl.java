package com.bookstoreapp.service;

import com.bookstoreapp.dto.user.UserRegistrationRequestDto;
import com.bookstoreapp.dto.user.UserRegistrationResponseDto;
import com.bookstoreapp.exception.EntityNotFoundException;
import com.bookstoreapp.exception.RegistrationException;
import com.bookstoreapp.mapper.UserMapper;
import com.bookstoreapp.model.Role;
import com.bookstoreapp.model.User;
import com.bookstoreapp.repository.role.RoleRepository;
import com.bookstoreapp.repository.user.UserRepository;
import com.bookstoreapp.validator.password.PasswordConstraint;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public UserRegistrationResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new RegistrationException("User with email: "
                    + requestDto.getEmail()
                    + " already exist");
        }
        User user = new User();
        user.setEmail(requestDto.getEmail());
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setFirstName(requestDto.getFirstName());
        user.setLastName(requestDto.getLastName());
        user.setShippingAddress(requestDto.getShippingAddress());
        User savedUser = userRepository.save(user);

        Role roleFromDb = roleRepository.findRoleByRole(Role.RoleType.ROLE_USER)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cannot find role 'ROLE_USER'"));
        Set<Role> userRoles = user.getRoles();
        userRoles = new HashSet<>();
        userRoles.add(roleFromDb);
        user.setRoles(userRoles);

        return userMapper.toRegistrationResponseDto(savedUser);
    }
}
