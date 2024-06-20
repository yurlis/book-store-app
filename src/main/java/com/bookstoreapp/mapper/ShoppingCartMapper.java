package com.bookstoreapp.mapper;

import com.bookstoreapp.config.MapperConfig;
import com.bookstoreapp.dto.shoppingcart.ShoppingCartDto;
import com.bookstoreapp.model.ShoppingCart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Mapper(config = MapperConfig.class, uses = CartItemMapper.class)
@Component
public interface ShoppingCartMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "cartItems", target = "cartItems")
    ShoppingCartDto toDto(ShoppingCart shoppingCart);

    @Mapping(source = "userId", target = "user.id")
    @Mapping(source = "cartItems", target = "cartItems")
    ShoppingCart toModel(ShoppingCartDto shoppingCartResponseDto);
}
