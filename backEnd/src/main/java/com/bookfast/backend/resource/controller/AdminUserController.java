package com.bookfast.backend.resource.controller;

import com.bookfast.backend.common.model.User;
import com.bookfast.backend.resource.service.UserService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminUserController {
    private final UserService service;

    public AdminUserController(UserService service) {
        this.service = service;
    }

    @GetMapping

    public List<User> getAllUsers() {
        return service.getAllUsers();
    }

    @PutMapping("/{id}")

    public User updateUser(@PathVariable Long id, @RequestBody User updated) {
        return service.updateUser(id, updated);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        service.deleteUser(id);
    }
}
