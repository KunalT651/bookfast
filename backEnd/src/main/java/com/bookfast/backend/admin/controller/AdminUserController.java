package com.bookfast.backend.admin.controller;

import com.bookfast.backend.admin.service.AdminUserService;
import com.bookfast.backend.common.dto.ErrorResponse;
import com.bookfast.backend.common.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {
    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = adminUserService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to fetch users: " + ex.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            User user = adminUserService.getUserById(id);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(user);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to fetch user: " + ex.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            User createdUser = adminUserService.createUser(user);
            return ResponseEntity.ok(createdUser);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to create user: " + ex.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            User updatedUser = adminUserService.updateUser(id, user);
            if (updatedUser == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(updatedUser);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to update user: " + ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            boolean deleted = adminUserService.deleteUser(id);
            if (!deleted) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to delete user: " + ex.getMessage()));
        }
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<?> getUsersByRole(@PathVariable String role) {
        try {
            List<User> users = adminUserService.getUsersByRole(role);
            return ResponseEntity.ok(users);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to fetch users by role: " + ex.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(@RequestParam String q) {
        try {
            List<User> users = adminUserService.searchUsers(q);
            return ResponseEntity.ok(users);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to search users: " + ex.getMessage()));
        }
    }
}
