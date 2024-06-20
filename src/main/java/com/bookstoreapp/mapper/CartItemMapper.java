package com.bookstoreapp.mapper;

import com.bookstoreapp.config.MapperConfig;
import com.bookstoreapp.dto.shoppingcart.AddCartItemRequestDto;
import com.bookstoreapp.dto.shoppingcart.CartItemDto;
import com.bookstoreapp.dto.shoppingcart.UpdateCartItemRequestDto;
import com.bookstoreapp.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.springframework.stereotype.Component;

@Mapper(config = MapperConfig.class, uses = BookMapper.class)
@Component
public interface CartItemMapper {
    @Mappings({
            @Mapping(target = "bookId", source = "book.id"),
            @Mapping(target = "bookTitle", source = "book.title")
    })
    CartItemDto toDto(CartItem cartItem);

    @Mapping(target = "book", source = "bookId", qualifiedByName = "bookFromId")
    CartItem toModel(AddCartItemRequestDto requestDto);

    @Mapping(target = "book", ignore = true)
    void updateFromDto(UpdateCartItemRequestDto requestDto, @MappingTarget CartItem itemFromDb);
}
