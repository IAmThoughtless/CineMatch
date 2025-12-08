package com.cinematch.cinematchbackend.controller;

import com.cinematch.cinematchbackend.model.User;
import com.cinematch.cinematchbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    private UserRepository userRepository;

    @CrossOrigin(origins = "*")
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {

        // 1. Check if user already exists in the Database
        if (userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username is already taken.");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email is already registered.");
        }

        // 2. Save the user to the Database
        userRepository.save(user);
        System.out.println("Backend: Saved new user to DB: " + user.getUsername());

        return ResponseEntity.ok("Registration successful! Proceed to login.");
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {

        // 1. Search for the user in the Database
        Optional<User> dbUser = userRepository.findByUsername(loginRequest.getUsername());

        if (dbUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found.");
        }

        // 2. Check if the password matches
        if (dbUser.get().getPassword().equals(loginRequest.getPassword())) {
            System.out.println("Backend: Login successful for: " + loginRequest.getUsername());
            return ResponseEntity.ok(dbUser.get());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password.");
        }
    }
}