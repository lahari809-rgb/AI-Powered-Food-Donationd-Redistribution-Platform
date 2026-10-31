package com.freshconnect.repository;

import com.freshconnect.model.User;
import com.freshconnect.model.User.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndPassword(String email, String password);
    List<User> findByRole(UserRole role);
    long countByRole(UserRole role);
    boolean existsByEmail(String email);
    List<User> findByRoleAndActiveTrue(UserRole role);
}
