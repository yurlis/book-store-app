package com.bookstoreapp.mapper;

import com.bookstoreapp.config.MapperConfig;
import com.bookstoreapp.dto.order.OrderDto;
import com.bookstoreapp.dto.order.OrderItemDto;
import com.bookstoreapp.model.Book;
import com.bookstoreapp.model.Order;
import com.bookstoreapp.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = OrderItemMapper.class)
public interface OrderMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "orderItems", source = "orderItems")
    @Mapping(target = "orderDate", source = "orderDate")
    @Mapping(target = "total", source = "total")
    @Mapping(target = "status", source = "status")
    OrderDto toDto(Order order);
}
