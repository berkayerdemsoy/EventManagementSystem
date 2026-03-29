package com.example.user_service_app.service;

import com.example.user_service_client.dto.UserCreateDto;
import com.example.user_service_client.dto.UserLoginDto;
import com.example.user_service_client.dto.UserResponseDto;
import com.example.user_service_client.dto.UserUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    Page<UserResponseDto> getAllUsers(Pageable pageable);
    UserResponseDto getUserById(Long id);
    UserResponseDto getUserByUsername(String username);
    UserResponseDto createUser(UserCreateDto dto);
    UserResponseDto updateUser(Long id, UserUpdateDto dto);
    void deleteUserById(Long id);
    void beOwner(Long id);
    UserResponseDto login(UserLoginDto dto);
    void verifyUserEmail(Long id);
    void confirmEmail(String token);
}
