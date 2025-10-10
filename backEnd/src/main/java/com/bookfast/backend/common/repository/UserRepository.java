package com.bookfast.backend.common.repository;

import com.bookfast.backend.common.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email); // <-- Add this line
    Optional<User> findByEmail(String email); // <-- Also useful for login
}
