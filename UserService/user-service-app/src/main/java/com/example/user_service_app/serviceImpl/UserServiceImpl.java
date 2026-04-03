package com.example.user_service_app.serviceImpl;

import com.example.ems_common.exceptions.AlreadyExistsException;
import com.example.ems_common.exceptions.ForbiddenException;
import com.example.ems_common.exceptions.InvalidCredentialsException;
import com.example.ems_common.exceptions.NotFoundException;
import com.example.user_service_app.configs.adminLoginConfigs.AdminProperties;
import com.example.user_service_app.configs.emailConfigs.HashUtil;
import com.example.user_service_app.configs.emailConfigs.VerificationToken;
import com.example.user_service_app.configs.emailConfigs.VerificationTokenRepository;
import com.example.user_service_app.configs.security.JwtUtil;
import com.example.user_service_app.entity.User;
import com.example.user_service_app.entity.UserProfile;
import com.example.user_service_app.mapper.UserMapper;
import com.example.user_service_app.repository.UserRepository;
import com.example.user_service_app.service.EmailService;
import com.example.user_service_app.service.UserService;
import com.example.user_service_client.dto.*;
import com.example.user_service_client.enums.Roles;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AdminProperties  adminProperties;
    private final VerificationTokenRepository  verificationTokenRepository;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

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
    //TODO while creating user's role , should be set to USER by default and should not be allowed to set it to ADMIN
    //TODO email validation should be added
    @Override
    public UserResponseDto createUser(UserCreateDto dto) {
        if(userRepository.existsByUsernameIgnoreCase(dto.getUsername())) {
            throw new AlreadyExistsException("User with this username already exists");
        }
        if(userRepository.existsByEmailIgnoreCase(dto.getEmail())) {
            throw new AlreadyExistsException("User with this email already exists");
        }
        User user = userMapper.toEntity(dto);
        int adminIndex = adminProperties.getUsernames().indexOf(dto.getUsername());
        boolean isAdmin = adminIndex != -1
                && adminProperties.getPasswords().size() > adminIndex
                && adminProperties.getPasswords().get(adminIndex).equals(dto.getPassword());
        user.setRole(isAdmin ? Roles.ADMIN : Roles.USER);
        UserProfile userProfile = userMapper.toUserProfile(dto);
        user.setUserProfile(userProfile);
        userProfile.setUser(user);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        return userMapper.toResponseDto(userRepository.save(user));
    }
    @Override
    public UserResponseDto updateUser(Long id, UserUpdateDto dto) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        userMapper.updateUserFromDto(dto, user);
        userMapper.updateUserProfileFromDto(dto, user.getUserProfile());
        return userMapper.toResponseDto(userRepository.save(user));
    }

    @Override
    public void deleteUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        userRepository.delete(user);
    }

    @Override
    public void beOwner(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        if (!user.isVerified()) {
            throw new ForbiddenException("User is not verified");
        }
        user.setRole(Roles.EVENT_OWNER);
        userRepository.save(user);
    }

    @Override
    public AuthResponseDto login(UserLoginDto dto) {
        User user = userRepository.findByUsernameIgnoreCase(dto.getUsername()).orElseThrow(() -> new NotFoundException("User not found"));
        boolean isPasswordValid =  passwordEncoder.matches(dto.getPassword(), user.getPassword());
        if (!isPasswordValid && !dto.getPassword().equals(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
            userRepository.save(user);
            isPasswordValid = true;
        }

        if (!isPasswordValid) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole().name());
        return new AuthResponseDto(token, userMapper.toResponseDto(user));



    }

    @Transactional
    @Override
    public void verifyUserEmail(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        String email =  user.getEmail();

        // Eski token varsa sil
        verificationTokenRepository.findByUser(user).ifPresent(verificationTokenRepository::delete);

        String plainToken = UUID.randomUUID().toString();
        String hashedToken = HashUtil.hashToken(plainToken);

        VerificationToken verificationToken = new VerificationToken(hashedToken, user);
        verificationTokenRepository.save(verificationToken);

        String verificationLink = "http://localhost:8080/users/confirm-email?token=" + plainToken;

        emailService.sendVerificationEmail(email,verificationLink);
    }

    @Transactional
    @Override
    public void confirmEmail(String token) {
        String hashedToken = HashUtil.hashToken(token);
        VerificationToken verificationToken = verificationTokenRepository.findByTokenHash(hashedToken)
                .orElseThrow(() -> new NotFoundException("Invalid verification token"));

        if (verificationToken.isExpired()) {
            verificationTokenRepository.delete(verificationToken);
            throw new ForbiddenException("Verification token has expired");
        }

        User user = verificationToken.getUser();
        user.setVerified(true);
        userRepository.save(user);

        verificationTokenRepository.delete(verificationToken);
    }


}
