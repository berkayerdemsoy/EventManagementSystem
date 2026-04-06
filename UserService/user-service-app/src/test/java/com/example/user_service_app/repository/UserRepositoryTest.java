package com.example.user_service_app.repository;

import com.example.user_service_app.entity.User;
import com.example.user_service_app.entity.UserProfile;
import com.example.user_service_client.enums.Roles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired private TestEntityManager entityManager;
    @Autowired private UserRepository userRepository;

    private User savedUser;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setUsername("berkay");
        user.setPassword("$2a$10$hashedPassword");
        user.setEmail("berkay@gmail.com");
        user.setRole(Roles.USER);
        user.setVerified(false);
        user.setCreatedAt(LocalDateTime.now());

        UserProfile profile = new UserProfile();
        profile.setFirstName("Berkay");
        profile.setLastName("Erdemsoy");
        profile.setPhoneNumber("5551234567");
        profile.setUser(user);
        user.setUserProfile(profile);

        savedUser = entityManager.persistAndFlush(user);
    }

    // ═══════════════════════════════════════════════════════════════
    //  findByUsernameIgnoreCase
    // ═══════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("findByUsernameIgnoreCase")
    class FindByUsernameIgnoreCaseTests {

        @Test
        @DisplayName("should find user with exact username")
        void shouldFindByExactUsername() {
            Optional<User> found = userRepository.findByUsernameIgnoreCase("berkay");

            assertThat(found).isPresent();
            assertThat(found.get().getEmail()).isEqualTo("berkay@gmail.com");
        }

        @Test
        @DisplayName("should find user with different case")
        void shouldFindByDifferentCase() {
            Optional<User> found = userRepository.findByUsernameIgnoreCase("BERKAY");

            assertThat(found).isPresent();
            assertThat(found.get().getUsername()).isEqualTo("berkay");
        }

        @Test
        @DisplayName("should return empty for non-existent username")
        void shouldReturnEmptyForNonExistent() {
            Optional<User> found = userRepository.findByUsernameIgnoreCase("ghost");

            assertThat(found).isEmpty();
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  existsByUsernameIgnoreCase
    // ═══════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("existsByUsernameIgnoreCase")
    class ExistsByUsernameIgnoreCaseTests {

        @Test
        @DisplayName("should return true for existing username (case-insensitive)")
        void shouldReturnTrueForExisting() {
            assertThat(userRepository.existsByUsernameIgnoreCase("Berkay")).isTrue();
        }

        @Test
        @DisplayName("should return false for non-existent username")
        void shouldReturnFalseForNonExistent() {
            assertThat(userRepository.existsByUsernameIgnoreCase("nobody")).isFalse();
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  existsByEmailIgnoreCase
    // ═══════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("existsByEmailIgnoreCase")
    class ExistsByEmailIgnoreCaseTests {

        @Test
        @DisplayName("should return true for existing email (case-insensitive)")
        void shouldReturnTrueForExisting() {
            assertThat(userRepository.existsByEmailIgnoreCase("BERKAY@GMAIL.COM")).isTrue();
        }

        @Test
        @DisplayName("should return false for non-existent email")
        void shouldReturnFalseForNonExistent() {
            assertThat(userRepository.existsByEmailIgnoreCase("nobody@gmail.com")).isFalse();
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  existsByIdAndIsVerifiedTrue  (custom query in repo, renamed field)
    // ═══════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("existsByIdAndIsVerifiedTrue")
    class ExistsByIdAndVerifiedTrueTests {

        @Test
        @DisplayName("should return false when user is not verified")
        void shouldReturnFalseWhenNotVerified() {
            assertThat(userRepository.existsByIdAndIsVerifiedTrue(savedUser.getId())).isFalse();
        }

        @Test
        @DisplayName("should return true when user is verified")
        void shouldReturnTrueWhenVerified() {
            savedUser.setVerified(true);
            entityManager.persistAndFlush(savedUser);

            assertThat(userRepository.existsByIdAndIsVerifiedTrue(savedUser.getId())).isTrue();
        }

        @Test
        @DisplayName("should return false for non-existent id")
        void shouldReturnFalseForNonExistentId() {
            assertThat(userRepository.existsByIdAndIsVerifiedTrue(9999L)).isFalse();
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  Basic JPA operations
    // ═══════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("Basic CRUD operations")
    class BasicCrudTests {

        @Test
        @DisplayName("should save and retrieve user with profile")
        void shouldSaveAndRetrieve() {
            Optional<User> found = userRepository.findById(savedUser.getId());

            assertThat(found).isPresent();
            User user = found.get();
            assertThat(user.getUsername()).isEqualTo("berkay");
            assertThat(user.getUserProfile()).isNotNull();
            assertThat(user.getUserProfile().getFirstName()).isEqualTo("Berkay");
        }

        @Test
        @DisplayName("should delete user and cascade to profile")
        void shouldDeleteWithCascade() {
            Long userId = savedUser.getId();
            userRepository.deleteById(userId);
            entityManager.flush();

            assertThat(userRepository.findById(userId)).isEmpty();
        }
    }
}
