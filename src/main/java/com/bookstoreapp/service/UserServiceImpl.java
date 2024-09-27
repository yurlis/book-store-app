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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
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
        User savedUser = userRepository.save(userMapper.toUserModel(requestDto));

        Optional<Role> roleFromDb = roleRepository.findRoleByRole(Role.RoleType.ROLE_USER);

        Set<Role> userRoles = new HashSet<>();
        userRoles.add(roleFromDb.get());
        savedUser.setRoles(userRoles);

        return userMapper.toRegistrationResponseDto(savedUser);
    }
}
