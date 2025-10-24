
package com.bookfast.backend.resource.service;

import com.bookfast.backend.common.model.User;
import com.bookfast.backend.common.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public List<User> getAllUsers() {
        return repository.findAll();
    }

    public User updateUser(Long id, User updated) {
        User user = repository.findById(id).orElse(null);
        if (user != null) {
            user.setFirstName(updated.getFirstName());
            user.setLastName(updated.getLastName());
            user.setEmail(updated.getEmail());
            user.setRole(updated.getRole());
            // No enabled field in User model
            return repository.save(user);
        }
        return null;
    }

    public void deleteUser(Long id) {
        repository.deleteById(id);
    }
}
