package com.bookstoreapp.controller;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import com.bookstoreapp.dto.user.UserLoginRequestDto;
import com.bookstoreapp.dto.user.UserLoginResponseDto;
import com.bookstoreapp.dto.user.UserRegistrationRequestDto;
import com.bookstoreapp.dto.user.UserRegistrationResponseDto;
import com.bookstoreapp.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AuthenticationControllerIntegrationTest {

    @Autowired
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private AuthenticationManager authenticationManager;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    void setUp(
            @Autowired DataSource dataSource
    ) throws SQLException {
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/authentication/add-auth-users.sql")
            );
        }
    }

    @Test
    @DisplayName("Login with valid credentials should return token")
    void login_ValidCredentials_ReturnsToken() throws Exception {
        // Given
        UserLoginRequestDto requestDto = new UserLoginRequestDto("testuser1@test.com", "password123");

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                requestDto.email(), requestDto.password());

        when(authenticationManager.authenticate(authentication)).thenReturn(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult result = mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andReturn();

        UserLoginResponseDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                UserLoginResponseDto.class);

        // Then
        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.token(), "Token should not be null");
    }

    @Test
    @DisplayName("Register new user with valid request should return created status and user response")
    void register_ValidRequest_ReturnsCreatedAndUserResponse() throws Exception {
        // Given
        UserRegistrationRequestDto registrationRequest = new UserRegistrationRequestDto();
        registrationRequest.setEmail("test@test.com");
        registrationRequest.setFirstName("TestName");
        registrationRequest.setLastName("TestLastName");
        registrationRequest.setPassword("password12345!");
        registrationRequest.setRepeatPassword("password12345!");

        UserRegistrationResponseDto expected = new UserRegistrationResponseDto();
        expected.setId(2L);
        expected.setEmail(registrationRequest.getEmail());

        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(registrationRequest);

        // When
        MvcResult result = mockMvc.perform(
                        post("/auth/registration")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        UserRegistrationResponseDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                UserRegistrationResponseDto.class);

        // Then
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected.getEmail(), actual.getEmail(), "Тут треба повідомлення англійською");
    }

    @AfterEach
    void afterEach(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/authentication/delete-auth-users.sql")
            );
        }
    }
}