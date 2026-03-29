package com.example.user_service_app.controller;

import com.example.user_service_app.service.UserService;
import com.example.user_service_client.dto.UserCreateDto;
import com.example.user_service_client.dto.UserLoginDto;
import com.example.user_service_client.dto.UserResponseDto;
import com.example.user_service_client.dto.UserUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/all")
    public ResponseEntity<Page<UserResponseDto>> getAllUsers(Pageable pageable) {
        Page<UserResponseDto> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id){
        UserResponseDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponseDto> getUserByUsername(@PathVariable String username){
        UserResponseDto user = userService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id){
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/create")
    public ResponseEntity<UserResponseDto> createUser(@RequestBody UserCreateDto dto){
        UserResponseDto user = userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable("id") Long id,@RequestBody UserUpdateDto dto){
        UserResponseDto user = userService.updateUser(id, dto);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/owner/{id}")
    public ResponseEntity<Void> beOwner(@PathVariable Long id){
        userService.beOwner(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDto> login(@RequestBody UserLoginDto dto){
        UserResponseDto user = userService.login(dto);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/verify-email/{id}")
    public ResponseEntity<String> verifyEmail(@PathVariable Long id){
        userService.verifyUserEmail(id);
        return ResponseEntity.ok("Verification email sent successfully");
    }

    @GetMapping("/confirm-email")
    public ResponseEntity<String> confirmEmail(@RequestParam String token){
        userService.confirmEmail(token);
        return ResponseEntity.ok("Email verified successfully");
    }

}
