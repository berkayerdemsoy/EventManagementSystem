package com.example.user_service_app.controller;

import com.example.ems_common.exceptions.AlreadyExistsException;
import com.example.ems_common.exceptions.GlobalExceptionHandler;
import com.example.ems_common.exceptions.InvalidCredentialsException;
import com.example.ems_common.exceptions.NotFoundException;
import com.example.ems_common.security.JwtAuthFilter;
import com.example.ems_common.security.JwtUtil;
import com.example.user_service_app.configs.security.SecurityConfig;
import com.example.user_service_app.service.UserService;
import com.example.user_service_client.dto.*;
import com.example.user_service_client.enums.Roles;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class UserControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private UserService userService;
    @MockitoBean private JwtUtil jwtUtil;
    @MockitoBean private JwtAuthFilter jwtAuthFilter;

    private UserResponseDto responseDto;

    @BeforeEach
    void setUp() throws ServletException, IOException {
        // Make the mocked JwtAuthFilter pass through to the next filter in the chain
        doAnswer(invocation -> {
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(invocation.getArgument(0), invocation.getArgument(1));
            return null;
        }).when(jwtAuthFilter).doFilter(any(), any(), any());

        responseDto = new UserResponseDto();
        responseDto.setId(1L);
        responseDto.setUsername("berkay");
        responseDto.setEmail("berkay@gmail.com");
        responseDto.setRole(Roles.USER);
        responseDto.setVerified(false);
        responseDto.setFirstName("Berkay");
        responseDto.setLastName("Erdemsoy");
        responseDto.setPhoneNumber("5551234567");
    }

    // ═══════════════════════════════════════════════════════════════
    //  PUBLIC ENDPOINTS (no auth required)
    // ═══════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("POST /users/create — Public")
    class CreateUserEndpoint {

        @Test
        @DisplayName("should create user and return 201")
        void shouldCreateUser() throws Exception {
            UserCreateDto dto = new UserCreateDto("newuser", "password123", "new@gmail.com",
                    "New", "User", "5559876543");

            when(userService.createUser(any(UserCreateDto.class))).thenReturn(responseDto);

            mockMvc.perform(post("/users/create")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.username").value("berkay"))
                    .andExpect(jsonPath("$.email").value("berkay@gmail.com"));
        }

        @Test
        @DisplayName("should return 409 for duplicate username")
        void shouldReturn409ForDuplicate() throws Exception {
            UserCreateDto dto = new UserCreateDto("existing", "password123", "e@gmail.com",
                    "E", "User", "5559876543");

            when(userService.createUser(any(UserCreateDto.class)))
                    .thenThrow(new AlreadyExistsException("User with this username already exists"));

            mockMvc.perform(post("/users/create")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.errorCode").value("ALREADY_EXISTS"));
        }
    }

    @Nested
    @DisplayName("POST /users/login — Public")
    class LoginEndpoint {

        @Test
        @DisplayName("should login and return 200 with token")
        void shouldLogin() throws Exception {
            UserLoginDto loginDto = new UserLoginDto("berkay", "password123");
            AuthResponseDto authResponse = new AuthResponseDto("jwt-token-here", responseDto);

            when(userService.login(any(UserLoginDto.class))).thenReturn(authResponse);

            mockMvc.perform(post("/users/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value("jwt-token-here"))
                    .andExpect(jsonPath("$.user.username").value("berkay"));
        }

        @Test
        @DisplayName("should return 401 for invalid credentials")
        void shouldReturn401ForBadCredentials() throws Exception {
            UserLoginDto loginDto = new UserLoginDto("berkay", "wrongpass");

            when(userService.login(any(UserLoginDto.class)))
                    .thenThrow(new InvalidCredentialsException("Invalid username or password"));

            mockMvc.perform(post("/users/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginDto)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.errorCode").value("INVALID_CREDENTIALS"));
        }
    }

    @Nested
    @DisplayName("GET /users/confirm-email — Public")
    class ConfirmEmailEndpoint {

        @Test
        @DisplayName("should confirm email and return 200")
        void shouldConfirmEmail() throws Exception {
            doNothing().when(userService).confirmEmail("valid-token");

            mockMvc.perform(get("/users/confirm-email")
                            .param("token", "valid-token"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Email verified successfully"));
        }

        @Test
        @DisplayName("should return 404 for invalid token")
        void shouldReturn404ForInvalidToken() throws Exception {
            doThrow(new NotFoundException("Invalid verification token"))
                    .when(userService).confirmEmail("bad-token");

            mockMvc.perform(get("/users/confirm-email")
                            .param("token", "bad-token"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"));
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  AUTHENTICATED ENDPOINTS
    // ═══════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("GET /users/id/{id} — Authenticated")
    class GetUserByIdEndpoint {

        @Test
        @DisplayName("should return 403 when not authenticated")
        void shouldReturn403WhenNotAuthenticated() throws Exception {
            mockMvc.perform(get("/users/id/1"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("should return user when authenticated")
        void shouldReturnUserWhenAuthenticated() throws Exception {
            when(userService.getUserById(1L)).thenReturn(responseDto);

            mockMvc.perform(get("/users/id/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("berkay"))
                    .andExpect(jsonPath("$.email").value("berkay@gmail.com"));
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("should return 404 when user not found")
        void shouldReturn404WhenNotFound() throws Exception {
            when(userService.getUserById(999L))
                    .thenThrow(new NotFoundException("User not found"));

            mockMvc.perform(get("/users/id/999"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"));
        }
    }

    @Nested
    @DisplayName("GET /users/username/{username} — Authenticated")
    class GetUserByUsernameEndpoint {

        @Test
        @DisplayName("should return 403 when not authenticated")
        void shouldReturn403() throws Exception {
            mockMvc.perform(get("/users/username/berkay"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("should return user when authenticated")
        void shouldReturnUser() throws Exception {
            when(userService.getUserByUsername("berkay")).thenReturn(responseDto);

            mockMvc.perform(get("/users/username/berkay"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("berkay"));
        }
    }

    @Nested
    @DisplayName("POST /users/update/{id} — Authenticated")
    class UpdateUserEndpoint {

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("should update user and return 200")
        void shouldUpdateUser() throws Exception {
            UserUpdateDto updateDto = new UserUpdateDto();
            updateDto.setFirstName("Updated");

            when(userService.updateUser(eq(1L), any(UserUpdateDto.class))).thenReturn(responseDto);

            mockMvc.perform(post("/users/update/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("berkay"));
        }

        @Test
        @DisplayName("should return 403 when not authenticated")
        void shouldReturn403() throws Exception {
            mockMvc.perform(post("/users/update/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("POST /users/owner/{id} — Authenticated")
    class BeOwnerEndpoint {

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("should upgrade to owner and return 200")
        void shouldUpgradeToOwner() throws Exception {
            doNothing().when(userService).beOwner(1L);

            mockMvc.perform(post("/users/owner/1"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("should return 403 when not authenticated")
        void shouldReturn403() throws Exception {
            mockMvc.perform(post("/users/owner/1"))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("POST /users/verify-email/{id} — Authenticated")
    class VerifyEmailEndpoint {

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("should send verification email and return 200")
        void shouldSendVerificationEmail() throws Exception {
            doNothing().when(userService).verifyUserEmail(1L);

            mockMvc.perform(post("/users/verify-email/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Verification email sent successfully"));
        }

        @Test
        @DisplayName("should return 403 when not authenticated")
        void shouldReturn403() throws Exception {
            mockMvc.perform(post("/users/verify-email/1"))
                    .andExpect(status().isForbidden());
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  ADMIN-ONLY ENDPOINTS
    // ═══════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("GET /users/all — ADMIN only")
    class GetAllUsersEndpoint {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("should return paginated users for ADMIN")
        void shouldReturnUsersForAdmin() throws Exception {
            Page<UserResponseDto> page = new PageImpl<>(List.of(responseDto), PageRequest.of(0, 20), 1);
            when(userService.getAllUsers(any())).thenReturn(page);

            mockMvc.perform(get("/users/all")
                            .param("page", "0")
                            .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].username").value("berkay"));
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("should return 403 for non-ADMIN user")
        void shouldReturn403ForNonAdmin() throws Exception {
            mockMvc.perform(get("/users/all"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("should return 403 when not authenticated")
        void shouldReturn403() throws Exception {
            mockMvc.perform(get("/users/all"))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("DELETE /users/{id} — ADMIN only")
    class DeleteUserEndpoint {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("should delete user and return 204 for ADMIN")
        void shouldDeleteForAdmin() throws Exception {
            doNothing().when(userService).deleteUserById(1L);

            mockMvc.perform(delete("/users/1"))
                    .andExpect(status().isNoContent());
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("should return 403 for non-ADMIN user")
        void shouldReturn403ForNonAdmin() throws Exception {
            mockMvc.perform(delete("/users/1"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("should return 403 when not authenticated")
        void shouldReturn403() throws Exception {
            mockMvc.perform(delete("/users/1"))
                    .andExpect(status().isForbidden());
        }
    }
}






