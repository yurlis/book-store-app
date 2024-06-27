package com.bookstoreapp.service;

import com.bookstoreapp.dto.order.OrderDto;
import com.bookstoreapp.dto.order.OrderItemDto;
import com.bookstoreapp.dto.order.PlaceOrderRequestDto;
import com.bookstoreapp.dto.order.UpdateOrderStatusRequestDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    OrderDto save(Long userId, PlaceOrderRequestDto requestDto);

    List<OrderDto> findAll(Long userId, Pageable pageable);

    List<OrderItemDto> findAllOrderItemsByOrderId(Long userId, Long orderId);

    OrderItemDto findOrderItemById(Long userId, Long id, Long orderId);

    void update(UpdateOrderStatusRequestDto requestDto, Long orderId);
}
