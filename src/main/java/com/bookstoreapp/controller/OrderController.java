package com.bookstoreapp.controller;

import com.bookstoreapp.dto.order.OrderDto;
import com.bookstoreapp.dto.order.OrderItemDto;
import com.bookstoreapp.dto.order.PlaceOrderRequestDto;
import com.bookstoreapp.dto.order.UpdateOrderStatusRequestDto;
import com.bookstoreapp.model.User;
import com.bookstoreapp.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "BookStore. Order Management", description = "Endpoints for managing orders in BookStore")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Place a new order",
            description = "Create a new order for the authenticated user")
    OrderDto placeOrder(Authentication authentication,
                        @RequestBody @Valid PlaceOrderRequestDto requestDto) {
        return orderService.save(getUserId(authentication), requestDto);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Get all orders",
            description = "Get all orders for the authenticated user")
    List<OrderDto> getAllOrders(Authentication authentication, Pageable pageable) {
        return orderService.findAll(getUserId(authentication), pageable);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Update order status",
            description = "Update the status of the specific order")
    ResponseEntity<Void> updateOrderStatus(@RequestBody @Valid UpdateOrderStatusRequestDto requestDto,
                               @PathVariable Long id) {
        orderService.update(requestDto, id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{orderId}/items")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Get all order items",
            description = "Get all items for the specific order")
    List<OrderItemDto> getAllOrderItems(Authentication authentication,
                                        @PathVariable Long orderId) {
        return orderService.findAllOrderItemsByOrderId(getUserId(authentication), orderId);
    }

    @GetMapping("{orderId}/items/{itemId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Get order item by ID",
            description = "Get a specific order item by its ID")
    OrderItemDto getOrderItem(Authentication authentication,
                              @PathVariable Long itemId,
                              @PathVariable Long orderId) {
        return orderService.findOrderItemById(getUserId(authentication), itemId, orderId);
    }

    private Long getUserId(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return user.getId();
    }
}
