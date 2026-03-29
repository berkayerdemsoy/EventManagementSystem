package com.example.user_service_client.dto;

import com.example.user_service_client.enums.Roles;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.NumberFormat;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateDto {
    @Size(min = 1, max = 50 , message = "Username must be between 1 and 50 characters")
    private String username;
    @Size(min = 5, max = 16 , message = "Password must be between 5 and 16 characters")
    private String password;
    @Email
    private String email;

    // UserProfile alanları (flat)
    @Size(min = 1, max = 50 , message = "First name must be between 1 and 50 characters")
    private String firstName;
    @Size(min = 1, max = 50 , message = "Last name must be between 1 and 50 characters")
    private String lastName;
    @NumberFormat
    private String phoneNumber;
}

