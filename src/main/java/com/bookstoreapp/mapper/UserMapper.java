package com.bookstoreapp.mapper;

import com.bookstoreapp.config.MapperConfig;
import com.bookstoreapp.dto.user.UserRegistrationRequestDto;
import com.bookstoreapp.dto.user.UserRegistrationResponseDto;
import com.bookstoreapp.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserRegistrationResponseDto toUserResponse(User user);

    User toUserModel(UserRegistrationRequestDto userRegistrationRequestDto);
}
