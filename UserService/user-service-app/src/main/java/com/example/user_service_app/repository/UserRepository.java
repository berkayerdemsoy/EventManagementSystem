package com.example.user_service_app.repository;

import com.example.user_service_app.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // ─── UserProfile ile birlikte çeken versiyonlar (N+1 önlemek için) ────────
    // EntityGraph ile aynı sorguda userProfile JOIN edilir; response DTO'daki
    // firstName / lastName / phoneNumber alanlarına erişildiğinde ek SELECT atılmaz.

    @EntityGraph(attributePaths = {"userProfile"})
    @Override
    Optional<User> findById(Long id);

    @EntityGraph(attributePaths = {"userProfile"})
    @Override
    Page<User> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"userProfile"})
    Optional<User> findByUsernameIgnoreCase(String username);

    boolean existsByUsernameIgnoreCase(String username);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByIdAndIsVerifiedTrue(Long id);

}
