package com.bookstoreapp.service;

import com.bookstoreapp.dto.user.UserRegistrationRequestDto;
import com.bookstoreapp.dto.user.UserRegistrationResponseDto;
import com.bookstoreapp.exception.RegistrationException;
import com.bookstoreapp.mapper.UserMapper;
import com.bookstoreapp.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserRegistrationResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new RegistrationException("Can't register user");
        }
        return userMapper.toUserResponse(userRepository.save(userMapper.toUserModel(requestDto)));
    }
}
