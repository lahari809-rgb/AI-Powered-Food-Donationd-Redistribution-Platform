package com.freshconnect.service;

import com.freshconnect.model.User;
import com.freshconnect.model.User.UserRole;
import com.freshconnect.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public User register(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        return userRepository.save(user);
    }
    
    public User login(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password)
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));
    }
    
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public List<User> findByRole(UserRole role) {
        return userRepository.findByRole(role);
    }
    
    public List<User> findAll() {
        return userRepository.findAll();
    }
    
    public long countByRole(UserRole role) {
        return userRepository.countByRole(role);
    }
    
    public User updateUser(Long id, User updated) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (updated.getName() != null) user.setName(updated.getName());
        if (updated.getPhone() != null) user.setPhone(updated.getPhone());
        if (updated.getAddress() != null) user.setAddress(updated.getAddress());
        if (updated.getCity() != null) user.setCity(updated.getCity());
        if (updated.getOrganizationName() != null) user.setOrganizationName(updated.getOrganizationName());
        if (updated.getOrganizationType() != null) user.setOrganizationType(updated.getOrganizationType());
        if (updated.getDescription() != null) user.setDescription(updated.getDescription());
        return userRepository.save(user);
    }
    
    public void toggleActive(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setActive(!user.isActive());
        userRepository.save(user);
    }
    
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
