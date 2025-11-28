package com.cinematch.cinematchbackend.repository;


import com.cinematch.cinematchbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// This connects the User class to the Database
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring automatically writes the SQL for these:
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}