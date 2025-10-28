package com.bookfast.backend.common.repository;

import com.bookfast.backend.common.model.Role;
import com.bookfast.backend.common.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    long countByRole(Role role);
    List<User> findByRole(Role role);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role.name = :roleName")
    long countByRoleName(@Param("roleName") String roleName);

    // Admin statistics methods
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdDate >= :date")
    long countByCreatedDateAfter(@Param("date") java.time.LocalDateTime date);
    
    // Role-based queries
    List<User> findByRoleNameIgnoreCase(String roleName);
    long countByRoleNameIgnoreCase(String roleName);
    
    // Search queries
    List<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
        String firstName, String lastName, String email);
    
    // Role and search combination
    List<User> findByRoleNameIgnoreCaseAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
        String roleName, String firstName, String lastName, String email);
    
    // Category-based queries
    List<User> findByRoleNameIgnoreCaseAndServiceCategoryIgnoreCase(String roleName, String category);
    
    // Date-based role queries
    long countByRoleNameIgnoreCaseAndCreatedDateAfter(String roleName, java.time.LocalDateTime date);
}
