package com.bookfast.backend.common.repository;

import com.bookfast.backend.common.model.Role;
import com.bookfast.backend.common.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    long countByRole(Role role);
    List<User> findByRole(Role role);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role.name = :roleName")
    long countByRoleName(@Param("roleName") String roleName);

    // Find users by role name (case insensitive)
    @Query("SELECT u FROM User u WHERE LOWER(u.role.name) = LOWER(:roleName)")
    List<User> findByRoleNameIgnoreCase(@Param("roleName") String roleName);

    // Count users by role name (case insensitive)
    @Query("SELECT COUNT(u) FROM User u WHERE LOWER(u.role.name) = LOWER(:roleName)")
    long countByRoleNameIgnoreCase(@Param("roleName") String roleName);

    // Find users by role and service category
    @Query("SELECT u FROM User u WHERE LOWER(u.role.name) = LOWER(:roleName) AND LOWER(u.serviceCategory) = LOWER(:serviceCategory)")
    List<User> findByRoleNameIgnoreCaseAndServiceCategoryIgnoreCase(@Param("roleName") String roleName, @Param("serviceCategory") String serviceCategory);

    // Search users by role and name/email
    @Query("SELECT u FROM User u WHERE LOWER(u.role.name) = LOWER(:roleName) AND (LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<User> findByRoleNameIgnoreCaseAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(@Param("roleName") String roleName, @Param("search") String firstName, @Param("search") String lastName, @Param("search") String email);

    // Search users by name/email
    @Query("SELECT u FROM User u WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(@Param("search") String firstName, @Param("search") String lastName, @Param("search") String email);

    // Count users created after a certain date
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdDate > :date")
    long countByCreatedDateAfter(@Param("date") LocalDate date);

    // Count users by role created after a certain date
    @Query("SELECT COUNT(u) FROM User u WHERE LOWER(u.role.name) = LOWER(:roleName) AND u.createdDate > :date")
    long countByRoleNameIgnoreCaseAndCreatedDateAfter(@Param("roleName") String roleName, @Param("date") LocalDate date);
}
