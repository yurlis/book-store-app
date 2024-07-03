package com.bookstoreapp.mapper;

import com.bookstoreapp.config.MapperConfig;
import com.bookstoreapp.dto.order.OrderItemDto;
import com.bookstoreapp.model.Book;
import com.bookstoreapp.model.CartItem;
import com.bookstoreapp.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

@Mapper(config = MapperConfig.class)
public interface OrderItemMapper {
    @Mapping(target = "price", ignore = true)
    @Mapping(target = "order", ignore = true)
    OrderItem toOrderItemModel(CartItem cartItem);

    @Mapping(target = "bookId", source = "book.id")
    OrderItemDto toDto(OrderItem orderItem);

    List<OrderItemDto> toDto(Set<OrderItem> orderItems);
}
