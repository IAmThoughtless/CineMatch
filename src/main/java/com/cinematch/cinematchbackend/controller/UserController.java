package com.cinematch.cinematchbackend.controller;

import com.cinematch.cinematchbackend.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private User savedUser = null;

    @CrossOrigin(origins = "*") // Επιτρέπει αιτήματα από οποιοδήποτε frontend (για τοπική δοκιμή)
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        if (savedUser != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A user is already registered locally.");
        }

        // Αποθήκευση του χρήστη
        this.savedUser = user;
        System.out.println("Backend: Registration successful for user: " + user.getUsername());

        return ResponseEntity.ok("Registration successful! Proceed to login.");
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        if (savedUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No user registered.");
        }

        // Έλεγχος των στοιχείων
        if (savedUser.getUsername().equals(user.getUsername()) && savedUser.getPassword().equals(user.getPassword())) {
            System.out.println("Backend: Login successful for user: " + user.getUsername());
            return ResponseEntity.ok("Login successful! Welcome.");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password.");
        }
    }
}
