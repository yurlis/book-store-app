package com.bookstoreapp.service;

import com.bookstoreapp.dto.order.OrderDto;
import com.bookstoreapp.dto.order.OrderItemDto;
import com.bookstoreapp.dto.order.PlaceOrderRequestDto;
import com.bookstoreapp.dto.order.UpdateOrderStatusRequestDto;
import com.bookstoreapp.exception.EntityNotFoundException;
import com.bookstoreapp.mapper.OrderItemMapper;
import com.bookstoreapp.mapper.OrderMapper;
import com.bookstoreapp.model.Book;
import com.bookstoreapp.model.Order;
import com.bookstoreapp.model.OrderItem;
import com.bookstoreapp.model.ShoppingCart;
import com.bookstoreapp.repository.order.OrderRepository;
import com.bookstoreapp.repository.shoppingcart.CartItemRepository;
import com.bookstoreapp.repository.shoppingcart.ShoppingCartRepository;
import com.bookstoreapp.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final ShoppingCartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public OrderDto save(Long userId, PlaceOrderRequestDto requestDto) {
        Order order = new Order();

        order.setUser(userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Cannot find user by user id: " + userId)));
        order.setShippingAddress(requestDto.shippingAddress());
        order.setStatus(Order.Status.PENDING);
        order.setOrderDate(LocalDateTime.now());
        order.setTotal(BigDecimal.ZERO);
        orderRepository.save(order);

        ShoppingCart cartFromDB = getCartFromDB(userId);
        Set<OrderItem> orderItems = moveOrderItemsFromShoppingCart(order, cartFromDB);
        order.setOrderItems(orderItems);

        BigDecimal total = orderItems.stream()
                .map(orderItem -> orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotal(total);

        orderRepository.save(order);

        cartItemRepository.deleteAll(cartFromDB.getCartItems());
        cartFromDB.getCartItems().clear();
        return orderMapper.toDto(order);
    }

    @Override
    public List<OrderDto> findAll(Long userId, Pageable pageable) {
        List<Order> orders = orderRepository.findAllByUserId(userId, pageable);
        return orderMapper.toDto(orders);
    }

    @Override
    public List<OrderItemDto> findAllOrderItemsByOrderId(Long userId, Long orderId) {
        Order orderFromDb = getOrderFromDbByUserIdAndOrderId(userId, orderId);
        Set<OrderItem> orderItems = orderFromDb.getOrderItems();
        return orderItemMapper.toDto(orderItems);
    }

    @Override
    public OrderItemDto findOrderItemById(Long userId, Long itemId, Long orderId) {
        Order orderFromDb = getOrderFromDbByUserIdAndOrderId(userId, orderId);
        return orderFromDb.getOrderItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .map(orderItemMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cannot find item by item id: " + itemId));
    }

    @Override
    public void update(UpdateOrderStatusRequestDto requestDto, Long orderId) {
        Order orderFromDb = getOrderFromDbByOrderId(orderId);
        orderFromDb.setStatus(requestDto.status());
        orderRepository.save(orderFromDb);
    }

    private ShoppingCart getCartFromDB(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cannot find order by user id: " + userId));
    }

    private Order getOrderFromDbByOrderId(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cannot find order by order id: " + orderId));
    }

    private Order getOrderFromDbByUserIdAndOrderId(Long userId, Long orderId) {
        return orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cannot find order by user id: " + userId + " and order id: " + orderId));
    }

    private Set<OrderItem> moveOrderItemsFromShoppingCart(Order order, ShoppingCart cartFromDB) {
        final Order finalOrder = order;
        Set<OrderItem> orderItems = cartFromDB.getCartItems().stream()
                .map(cartItem -> {
                    OrderItem orderItem = orderItemMapper.toOrderItemModel(cartItem);
                    orderItem.setOrder(finalOrder);
                    Book book = cartItem.getBook();
                    orderItem.setPrice(book.getPrice());
                    orderItem.setQuantity(cartItem.getQuantity());
                    return orderItem;
                })
                .collect(Collectors.toSet());
        return orderItems;
    }
}
