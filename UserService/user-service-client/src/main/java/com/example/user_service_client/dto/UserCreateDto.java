package com.example.user_service_client.dto;

import com.example.user_service_client.enums.Roles;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateDto {
    private String username;
    private String password;
    private String email;
    private Roles role;

    // UserProfile alanları (flat)
    private String firstName;
    private String lastName;
    private String phoneNumber;
}

