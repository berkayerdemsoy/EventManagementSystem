package com.example.user_service_client.client;

import com.example.user_service_client.dto.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange("/users")
public interface UserServiceClient {

    @GetExchange("/all")
    PageResponse<UserResponseDto> getAllUsers(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    );

    @GetExchange("/id/{id}")
    UserResponseDto getUserById(@PathVariable("id") Long id);

    @GetExchange("/username/{username}")
    UserResponseDto getUserByUsername(@PathVariable("username") String username);

    @DeleteExchange("/{id}")
    void deleteUserById(@PathVariable("id") Long id);

    @PostExchange("/create")
    UserResponseDto createUser(@RequestBody UserCreateDto dto);

    @PostExchange("/update/{id}")
    UserResponseDto updateUser(@PathVariable("id") Long id, @RequestBody UserUpdateDto dto);
}

