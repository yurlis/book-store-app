package com.bookstoreapp.controller;

import com.bookstoreapp.dto.order.OrderDto;
import com.bookstoreapp.dto.order.OrderItemDto;
import com.bookstoreapp.dto.order.PlaceOrderRequestDto;
import com.bookstoreapp.model.Order;
import com.bookstoreapp.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class OrderControllerIntegrationTest {
    @Autowired
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private User user;
    private Authentication authentication;

    @BeforeAll
    static void beforeAll(
            @Autowired WebApplicationContext applicationContext
    ) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    void setUp(@Autowired DataSource dataSource) throws SQLException {
        user = new User();
        user.setId(1L);
        user.setEmail("testuser@test.com");

        authentication = getAuthorisation(user);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/order/controller"
                            + "/add-data.sql")
            );
        }
    }

    @AfterEach
    void tearDown(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/order/controller"
                            + "/delete-data.sql")
            );
        }
    }

    @Test
    @DisplayName("Create an order - Valid PlaceOrderRequestDto should return OrderDto")
    void createOrder_ValidRequestDto_ReturnsOrderDto() throws Exception {
        // given
        PlaceOrderRequestDto requestDto = new PlaceOrderRequestDto("test address");

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        String requestJson = objectMapper.writeValueAsString(requestDto);

        OrderDto expectedResponse = getOrderDtoByUser(user);

        // when
        MvcResult result = mockMvc.perform(
                        post("/orders")
                                .with(authentication(authentication))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        OrderDto actualResponse = objectMapper.readValue(result.getResponse().getContentAsString(), OrderDto.class);

        // then
        Assertions.assertNotNull(actualResponse, "The returned OrderDto should not be null");
        Assertions.assertEquals(expectedResponse.orderItems().size(), actualResponse.orderItems().size(),
                "The number of items in the order should match the expected value");
    }

    @Test
    @DisplayName("Get order history - Valid request should return list of OrderDto")
    void getOrderHistory_ValidUser_ReturnsOrderHistory() throws Exception {
        // given
        OrderDto expectedOrder = getOrderDtoByUser(user);
        List<OrderDto> expectedResponseList = List.of(expectedOrder);

        // when
        MvcResult result = mockMvc.perform(get("/orders")
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        List<OrderDto> actualResponseList = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, OrderDto.class)
        );

        // then
        Assertions.assertNotNull(actualResponseList, "The returned order history should not be null");
        Assertions.assertEquals(expectedResponseList.size(), actualResponseList.size(),
                "The number of orders in the response should match the expected value");
    }

    @NotNull
    private static OrderDto getOrderDtoByUser(User user) {
        OrderItemDto orderItemDto1 = new OrderItemDto();
        orderItemDto1.setId(1L);
        orderItemDto1.setBookId(1L);
        orderItemDto1.setQuantity(3);

        OrderItemDto orderItemDto2 = new OrderItemDto();
        orderItemDto2.setId(2L);
        orderItemDto2.setBookId(2L);
        orderItemDto2.setQuantity(4);

        Set<OrderItemDto> orderItems = Set.of(orderItemDto1, orderItemDto2);

        return new OrderDto(
                1L,
                user.getId(),
                orderItems,
                LocalDateTime.now(),
                BigDecimal.valueOf(1799.66),
                Order.Status.PENDING
        );
    }

    private Authentication getAuthorisation(User user) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        return new UsernamePasswordAuthenticationToken(
                user, "password", authorities);
    }
}
