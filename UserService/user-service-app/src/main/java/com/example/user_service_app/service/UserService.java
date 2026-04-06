package com.example.user_service_app.service;

import com.example.user_service_client.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

public interface UserService {
    Page<UserResponseDto> getAllUsers(Pageable pageable);
    UserResponseDto getUserById(Long id);
    UserResponseDto getUserByUsername(String username);
    UserResponseDto createUser(UserCreateDto dto);

    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal")
    UserResponseDto updateUser(Long id, UserUpdateDto dto);

    void deleteUserById(Long id);

    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal")
    void beOwner(Long id);

    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal")
    void verifyUserEmail(Long id);

    void confirmEmail(String token);
    AuthResponseDto login(UserLoginDto dto);
}
