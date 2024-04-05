package com.bookstoreapp.dto.user;

import com.bookstoreapp.controller.model.User;
import lombok.Data;

@Data
public class UserRegistrationResponseDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String shippingAddress;
}
