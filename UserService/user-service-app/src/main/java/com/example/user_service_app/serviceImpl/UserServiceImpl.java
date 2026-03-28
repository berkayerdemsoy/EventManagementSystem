package com.example.user_service_app.serviceImpl;

import com.example.ems_common.exceptions.AlreadyExistsException;
import com.example.ems_common.exceptions.NotFoundException;
import com.example.user_service_app.entity.User;
import com.example.user_service_app.mapper.UserMapper;
import com.example.user_service_app.repository.UserRepository;
import com.example.user_service_app.service.UserService;
import com.example.user_service_client.dto.UserCreateDto;
import com.example.user_service_client.dto.UserResponseDto;
import com.example.user_service_client.dto.UserUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public Page<UserResponseDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toResponseDto);
    }
    @Override
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        return userMapper.toResponseDto(user);
    }

    @Override
    public UserResponseDto getUserByUsername(String username) {
        User user = userRepository.findByUsernameIgnoreCase(username).orElseThrow(() -> new NotFoundException("User not found"));
        return userMapper.toResponseDto(user);
    }
    @Override
    public UserResponseDto createUser(UserCreateDto dto) {
        if(userRepository.existsByUsernameIgnoreCase(dto.getUsername())) {
            throw new AlreadyExistsException("User with this username already exists");
        }
        if(userRepository.existsByEmailIgnoreCase(dto.getEmail())) {
            throw new AlreadyExistsException("User with this email already exists");
        }
        User user = userMapper.toEntity(dto);
        return userMapper.toResponseDto(userRepository.save(user));
    }
    @Override
    public UserResponseDto updateUser(Long id, UserUpdateDto dto) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        userMapper.updateUserFromDto(dto, user);
        userMapper.updateUserProfileFromDto(dto, user.getUserProfile());
        return userMapper.toResponseDto(user);
    }

    @Override
    public void deleteUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        userRepository.delete(user);
    }
}
