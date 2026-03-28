package com.example.user_service_client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDto {
    private String email;
    private String password;

    // UserProfile alanları (flat)
    private String firstName;
    private String lastName;
    private String phoneNumber;
}
