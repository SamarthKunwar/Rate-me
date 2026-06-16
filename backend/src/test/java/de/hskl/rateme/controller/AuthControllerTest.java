package de.hskl.rateme.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.hskl.rateme.dto.LoginDtoIn;
import de.hskl.rateme.dto.LoginDtoOut;
import de.hskl.rateme.dto.UserDtoIn;
import de.hskl.rateme.dto.UserDtoOut;
import de.hskl.rateme.service.AuthService;

class AuthControllerTest {

    private AuthService authService;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        authService = mock(AuthService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(authService)).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void registerReturnsCreatedWithLoginData() throws Exception {
        UserDtoOut user = new UserDtoOut(1, "testuser", "test@example.com", "Test", "User");
        LoginDtoOut response = new LoginDtoOut("token-123", user);
        UserDtoIn request = new UserDtoIn(
                "testuser",
                "Password123",
                "test@example.com",
                "Test",
                "User",
                "Main Street",
                "1",
                "66482",
                "Zweibruecken");

        when(authService.registerUser(any(UserDtoIn.class))).thenReturn(response);

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sessionToken").value("token-123"))
                .andExpect(jsonPath("$.user.username").value("testuser"));
    }

    @Test
    void loginReturnsOkWithLoginData() throws Exception {
        UserDtoOut user = new UserDtoOut(1, "testuser", "test@example.com", "Test", "User");
        LoginDtoOut response = new LoginDtoOut("token-123", user);
        LoginDtoIn request = new LoginDtoIn("testuser", "Password123");

        when(authService.loginUser(any(LoginDtoIn.class))).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionToken").value("token-123"));
    }

    @Test
    void logoutReturnsNoContent() throws Exception {
        mockMvc.perform(post("/auth/logout")
                .header("Authorization", "token-123"))
                .andExpect(status().isNoContent());

        verify(authService).logoutUser(eq("token-123"));
    }

    @Test
    void deleteCurrentUserReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/auth/me")
                .header("Authorization", "token-123"))
                .andExpect(status().isNoContent());

        verify(authService).deleteCurrentUser(eq("token-123"));
    }
}
