package br.com.giovanniramos.movierandomizer.controllers;

import br.com.giovanniramos.movierandomizer.controllers.requests.LoginRequest;
import br.com.giovanniramos.movierandomizer.exceptions.UnauthorizedException;
import br.com.giovanniramos.movierandomizer.handlers.GlobalExceptionHandler;
import br.com.giovanniramos.movierandomizer.models.LoginModel;
import br.com.giovanniramos.movierandomizer.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {
    private static final String BASE_URL = "/v1/login";
    private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthenticationController authenticationController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(authenticationController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @SneakyThrows
    void shouldLogIn() {
        final var username = "giovanni.ramos";
        final var password = "12345";

        when(userService.login(any())).thenReturn(new LoginModel(username, password, "Token", 12456L));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(new LoginRequest(username, password))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.expiresIn").exists());
    }

    @SneakyThrows
    @ParameterizedTest
    @CsvSource(value = { "' '", "''", "null" }, nullValues = { "null" })
    void shouldTryToLogInAndReturn400WhenUsernameIsInvalid(final String username) {
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(new LoginRequest(username, "12345"))))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @ParameterizedTest
    @CsvSource(value = { "' '", "''", "null" }, nullValues = { "null" })
    void shouldTryToLogInAndReturn400WhenPasswordIsInvalid(final String password) {
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(new LoginRequest("giovanni.ramos", password))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void shouldTryToLogInAndReturn401WhenUnauthorized() {
        when(userService.login(any())).thenThrow(new UnauthorizedException("Username or password invalid"));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(new LoginRequest("giovanni.ramos", "12345"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    void shouldTryToLogInAndReturn500WhenThrowUnexpectedException() {
        when(userService.login(any())).thenThrow(new RuntimeException());

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(new LoginRequest("giovanni.ramos", "12345"))))
                .andExpect(status().isInternalServerError());
    }
}