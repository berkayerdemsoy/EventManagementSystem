package com.example.user_service_app.serviceImpl;

import com.example.ems_common.exceptions.AlreadyExistsException;
import com.example.ems_common.exceptions.ForbiddenException;
import com.example.ems_common.exceptions.InvalidCredentialsException;
import com.example.ems_common.exceptions.NotFoundException;
import com.example.ems_common.security.JwtUtil;
import com.example.user_service_app.configs.adminLoginConfigs.AdminProperties;
import com.example.user_service_app.configs.emailConfigs.VerificationToken;
import com.example.user_service_app.configs.emailConfigs.VerificationTokenRepository;
import com.example.user_service_app.entity.User;
import com.example.user_service_app.entity.UserProfile;
import com.example.user_service_app.mapper.UserMapper;
import com.example.user_service_app.repository.UserRepository;
import com.example.user_service_app.service.EmailService;
import com.example.user_service_client.dto.*;
import com.example.user_service_client.enums.Roles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;
    @Mock private AdminProperties adminProperties;
    @Mock private VerificationTokenRepository verificationTokenRepository;
    @Mock private EmailService emailService;
    @Mock private JwtUtil jwtUtil;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserProfile testProfile;
    private UserResponseDto testResponseDto;

    @BeforeEach
    void setUp() {
        testProfile = new UserProfile();
        testProfile.setId(1L);
        testProfile.setFirstName("Berkay");
        testProfile.setLastName("Erdemsoy");
        testProfile.setPhoneNumber("5551234567");

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("berkay");
        testUser.setPassword("$2a$10$encodedPassword");
        testUser.setEmail("berkay@gmail.com");
        testUser.setRole(Roles.USER);
        testUser.setVerified(false);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUserProfile(testProfile);
        testProfile.setUser(testUser);

        testResponseDto = new UserResponseDto();
        testResponseDto.setId(1L);
        testResponseDto.setUsername("berkay");
        testResponseDto.setEmail("berkay@gmail.com");
        testResponseDto.setRole(Roles.USER);
        testResponseDto.setVerified(false);
        testResponseDto.setFirstName("Berkay");
        testResponseDto.setLastName("Erdemsoy");
        testResponseDto.setPhoneNumber("5551234567");
    }

    // ═══════════════════════════════════════════════════════════════
    //  GET ALL USERS
    // ═══════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("getAllUsers")
    class GetAllUsersTests {

        @Test
        @DisplayName("should return paginated users")
        void shouldReturnPaginatedUsers() {
            Pageable pageable = PageRequest.of(0, 20);
            Page<User> userPage = new PageImpl<>(List.of(testUser), pageable, 1);

            when(userRepository.findAll(pageable)).thenReturn(userPage);
            when(userMapper.toResponseDto(testUser)).thenReturn(testResponseDto);

            Page<UserResponseDto> result = userService.getAllUsers(pageable);

            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getContent().getFirst().getUsername()).isEqualTo("berkay");
            verify(userRepository).findAll(pageable);
        }

        @Test
        @DisplayName("should return empty page when no users exist")
        void shouldReturnEmptyPage() {
            Pageable pageable = PageRequest.of(0, 20);
            Page<User> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

            when(userRepository.findAll(pageable)).thenReturn(emptyPage);

            Page<UserResponseDto> result = userService.getAllUsers(pageable);

            assertThat(result.getTotalElements()).isZero();
            assertThat(result.getContent()).isEmpty();
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  GET USER BY ID
    // ═══════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("getUserById")
    class GetUserByIdTests {

        @Test
        @DisplayName("should return user when found")
        void shouldReturnUser() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userMapper.toResponseDto(testUser)).thenReturn(testResponseDto);

            UserResponseDto result = userService.getUserById(1L);

            assertThat(result.getUsername()).isEqualTo("berkay");
            assertThat(result.getEmail()).isEqualTo("berkay@gmail.com");
        }

        @Test
        @DisplayName("should throw NotFoundException when user not found")
        void shouldThrowNotFound() {
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getUserById(999L))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("User not found");
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  GET USER BY USERNAME
    // ═══════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("getUserByUsername")
    class GetUserByUsernameTests {

        @Test
        @DisplayName("should return user when found by username")
        void shouldReturnUserByUsername() {
            when(userRepository.findByUsernameIgnoreCase("berkay")).thenReturn(Optional.of(testUser));
            when(userMapper.toResponseDto(testUser)).thenReturn(testResponseDto);

            UserResponseDto result = userService.getUserByUsername("berkay");

            assertThat(result.getUsername()).isEqualTo("berkay");
        }

        @Test
        @DisplayName("should throw NotFoundException when username not found")
        void shouldThrowNotFoundForUsername() {
            when(userRepository.findByUsernameIgnoreCase("ghost")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getUserByUsername("ghost"))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("User not found");
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  CREATE USER
    // ═══════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("createUser")
    class CreateUserTests {

        private UserCreateDto createDto;

        @BeforeEach
        void setUp() {
            createDto = new UserCreateDto();
            createDto.setUsername("newuser");
            createDto.setPassword("password123");
            createDto.setEmail("newuser@gmail.com");
            createDto.setFirstName("New");
            createDto.setLastName("User");
            createDto.setPhoneNumber("5559876543");
        }

        @Test
        @DisplayName("should create regular user successfully")
        void shouldCreateUser() {
            when(userRepository.existsByUsernameIgnoreCase("newuser")).thenReturn(false);
            when(userRepository.existsByEmailIgnoreCase("newuser@gmail.com")).thenReturn(false);
            when(userMapper.toEntity(createDto)).thenReturn(testUser);
            when(userMapper.toUserProfile(createDto)).thenReturn(testProfile);
            when(adminProperties.getUsernames()).thenReturn(List.of("admin"));
            when(passwordEncoder.encode("password123")).thenReturn("$2a$10$encoded");
            when(userRepository.save(testUser)).thenReturn(testUser);
            when(userMapper.toResponseDto(testUser)).thenReturn(testResponseDto);

            UserResponseDto result = userService.createUser(createDto);

            assertThat(result).isNotNull();
            assertThat(result.getUsername()).isEqualTo("berkay");
            verify(userRepository).save(testUser);
            verify(passwordEncoder).encode("password123");
        }

        @Test
        @DisplayName("should assign ADMIN role when username/password matches admin config")
        void shouldCreateAdminUser() {
            createDto.setUsername("admin");
            createDto.setPassword("adminpass");

            when(userRepository.existsByUsernameIgnoreCase("admin")).thenReturn(false);
            when(userRepository.existsByEmailIgnoreCase("newuser@gmail.com")).thenReturn(false);
            when(userMapper.toEntity(createDto)).thenReturn(testUser);
            when(userMapper.toUserProfile(createDto)).thenReturn(testProfile);
            when(adminProperties.getUsernames()).thenReturn(List.of("admin"));
            when(adminProperties.getPasswords()).thenReturn(List.of("adminpass"));
            when(passwordEncoder.encode("adminpass")).thenReturn("$2a$10$encodedAdmin");
            when(userRepository.save(testUser)).thenReturn(testUser);
            when(userMapper.toResponseDto(testUser)).thenReturn(testResponseDto);

            userService.createUser(createDto);

            verify(userRepository).save(argThat(user -> user.getRole() == Roles.ADMIN));
        }

        @Test
        @DisplayName("should throw AlreadyExistsException for duplicate username")
        void shouldThrowForDuplicateUsername() {
            when(userRepository.existsByUsernameIgnoreCase("newuser")).thenReturn(true);

            assertThatThrownBy(() -> userService.createUser(createDto))
                    .isInstanceOf(AlreadyExistsException.class)
                    .hasMessage("User with this username already exists");

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw AlreadyExistsException for duplicate email")
        void shouldThrowForDuplicateEmail() {
            when(userRepository.existsByUsernameIgnoreCase("newuser")).thenReturn(false);
            when(userRepository.existsByEmailIgnoreCase("newuser@gmail.com")).thenReturn(true);

            assertThatThrownBy(() -> userService.createUser(createDto))
                    .isInstanceOf(AlreadyExistsException.class)
                    .hasMessage("User with this email already exists");

            verify(userRepository, never()).save(any());
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  UPDATE USER
    // ═══════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("updateUser")
    class UpdateUserTests {

        @Test
        @DisplayName("should update user successfully")
        void shouldUpdateUser() {
            UserUpdateDto updateDto = new UserUpdateDto();
            updateDto.setFirstName("Updated");

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.save(testUser)).thenReturn(testUser);
            when(userMapper.toResponseDto(testUser)).thenReturn(testResponseDto);

            UserResponseDto result = userService.updateUser(1L, updateDto);

            assertThat(result).isNotNull();
            verify(userMapper).updateUserFromDto(updateDto, testUser);
            verify(userMapper).updateUserProfileFromDto(updateDto, testProfile);
        }

        @Test
        @DisplayName("should throw NotFoundException when updating non-existent user")
        void shouldThrowNotFoundOnUpdate() {
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.updateUser(999L, new UserUpdateDto()))
                    .isInstanceOf(NotFoundException.class);
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  DELETE USER
    // ═══════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("deleteUserById")
    class DeleteUserTests {

        @Test
        @DisplayName("should delete user successfully")
        void shouldDeleteUser() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

            userService.deleteUserById(1L);

            verify(userRepository).delete(testUser);
        }

        @Test
        @DisplayName("should throw NotFoundException when deleting non-existent user")
        void shouldThrowNotFoundOnDelete() {
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.deleteUserById(999L))
                    .isInstanceOf(NotFoundException.class);
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  BE OWNER
    // ═══════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("beOwner")
    class BeOwnerTests {

        @Test
        @DisplayName("should upgrade verified user to EVENT_OWNER")
        void shouldUpgradeToOwner() {
            testUser.setVerified(true);
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

            userService.beOwner(1L);

            assertThat(testUser.getRole()).isEqualTo(Roles.EVENT_OWNER);
            verify(userRepository).save(testUser);
        }

        @Test
        @DisplayName("should throw ForbiddenException when user is not verified")
        void shouldThrowForbiddenWhenNotVerified() {
            testUser.setVerified(false);
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

            assertThatThrownBy(() -> userService.beOwner(1L))
                    .isInstanceOf(ForbiddenException.class)
                    .hasMessage("User is not verified");

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw NotFoundException when user not found")
        void shouldThrowNotFoundOnBeOwner() {
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.beOwner(999L))
                    .isInstanceOf(NotFoundException.class);
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  LOGIN
    // ═══════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("login")
    class LoginTests {

        private UserLoginDto loginDto;

        @BeforeEach
        void setUp() {
            loginDto = new UserLoginDto("berkay", "password123");
        }

        @Test
        @DisplayName("should login successfully with correct credentials")
        void shouldLoginSuccessfully() {
            when(userRepository.findByUsernameIgnoreCase("berkay")).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches("password123", testUser.getPassword())).thenReturn(true);
            when(jwtUtil.generateToken(1L, "berkay", "USER")).thenReturn("jwt-token");
            when(userMapper.toResponseDto(testUser)).thenReturn(testResponseDto);

            AuthResponseDto result = userService.login(loginDto);

            assertThat(result.getToken()).isEqualTo("jwt-token");
            assertThat(result.getUser().getUsername()).isEqualTo("berkay");
        }

        @Test
        @DisplayName("should throw InvalidCredentialsException for wrong password")
        void shouldThrowForWrongPassword() {
            when(userRepository.findByUsernameIgnoreCase("berkay")).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches("wrongpass", testUser.getPassword())).thenReturn(false);

            loginDto.setPassword("wrongpass");

            assertThatThrownBy(() -> userService.login(loginDto))
                    .isInstanceOf(InvalidCredentialsException.class)
                    .hasMessage("Invalid username or password");
        }

        @Test
        @DisplayName("should throw InvalidCredentialsException for non-existent username")
        void shouldThrowForNonExistentUser() {
            when(userRepository.findByUsernameIgnoreCase("ghost")).thenReturn(Optional.empty());

            loginDto.setUsername("ghost");

            assertThatThrownBy(() -> userService.login(loginDto))
                    .isInstanceOf(InvalidCredentialsException.class)
                    .hasMessage("Invalid username or password");
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  VERIFY USER EMAIL
    // ═══════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("verifyUserEmail")
    class VerifyUserEmailTests {

        @Test
        @DisplayName("should send verification email successfully")
        void shouldSendVerificationEmail() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(verificationTokenRepository.findByUser(testUser)).thenReturn(Optional.empty());

            userService.verifyUserEmail(1L);

            verify(verificationTokenRepository).save(any(VerificationToken.class));
            verify(emailService).sendVerificationEmail(eq("berkay@gmail.com"), anyString());
        }

        @Test
        @DisplayName("should delete old token before creating new one")
        void shouldDeleteOldToken() {
            VerificationToken oldToken = new VerificationToken("oldHash", testUser);
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(verificationTokenRepository.findByUser(testUser)).thenReturn(Optional.of(oldToken));

            userService.verifyUserEmail(1L);

            verify(verificationTokenRepository).delete(oldToken);
            verify(verificationTokenRepository).save(any(VerificationToken.class));
        }

        @Test
        @DisplayName("should throw NotFoundException for non-existent user")
        void shouldThrowNotFoundOnVerifyEmail() {
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.verifyUserEmail(999L))
                    .isInstanceOf(NotFoundException.class);
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  CONFIRM EMAIL
    // ═══════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("confirmEmail")
    class ConfirmEmailTests {

        @Test
        @DisplayName("should confirm email with valid token")
        void shouldConfirmEmail() {
            VerificationToken vt = mock(VerificationToken.class);
            when(vt.isExpired()).thenReturn(false);
            when(vt.getUser()).thenReturn(testUser);

            when(verificationTokenRepository.findByTokenHash(anyString())).thenReturn(Optional.of(vt));

            userService.confirmEmail("some-uuid-token");

            assertThat(testUser.isVerified()).isTrue();
            verify(userRepository).save(testUser);
            verify(verificationTokenRepository).delete(vt);
        }

        @Test
        @DisplayName("should throw ForbiddenException for expired token")
        void shouldThrowForExpiredToken() {
            VerificationToken vt = mock(VerificationToken.class);
            when(vt.isExpired()).thenReturn(true);

            when(verificationTokenRepository.findByTokenHash(anyString())).thenReturn(Optional.of(vt));

            assertThatThrownBy(() -> userService.confirmEmail("expired-token"))
                    .isInstanceOf(ForbiddenException.class)
                    .hasMessage("Verification token has expired");

            verify(verificationTokenRepository).delete(vt);
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw NotFoundException for invalid token")
        void shouldThrowNotFoundForInvalidToken() {
            when(verificationTokenRepository.findByTokenHash(anyString())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.confirmEmail("invalid-token"))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("Invalid verification token");
        }
    }
}

