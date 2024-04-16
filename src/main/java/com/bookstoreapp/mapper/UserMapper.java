package com.bookstoreapp.mapper;

import com.bookstoreapp.config.MapperConfig;
import com.bookstoreapp.dto.user.UserRegistrationRequestDto;
import com.bookstoreapp.dto.user.UserRegistrationResponseDto;
import com.bookstoreapp.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(config = MapperConfig.class, uses = PasswordEncoderHelper.class)
public interface UserMapper {
    UserRegistrationResponseDto toRegistrationResponseDto(User user);

    @Mappings({
            @Mapping(target = "password", source = "password", qualifiedByName = "encodePassword")
    })
    User toUserModel(UserRegistrationRequestDto userRegistrationRequestDto);
}
