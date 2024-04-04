package com.bookstoreapp.controller;

import com.bookstoreapp.dto.user.UserRegistrationRequestDto;
import com.bookstoreapp.dto.user.UserRegistrationResponseDto;
import com.bookstoreapp.exception.RegistrationException;
import com.bookstoreapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Authentication management",
        description = "Endpoints for managing online store authentication processes")
public class AuthenticationController {
    private final UserService userService;

    @PostMapping("/registration")
    @Operation(summary = "Register a new user",
            description = "Performs a registration of a new user and add a new user to the DB")
    public UserRegistrationResponseDto register(@RequestBody @Valid UserRegistrationRequestDto requestDto) throws RegistrationException {
        return userService.register(requestDto);
    }
}
