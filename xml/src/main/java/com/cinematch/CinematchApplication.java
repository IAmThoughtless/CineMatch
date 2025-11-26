package com.cinematch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Η annotation αυτή δηλώνει ότι αυτή είναι η κύρια κλάση εκκίνησης του Spring Boot.
@SpringBootApplication
public class CinematchApplication {

    public static void main(String[] args) {
        // Η μέθοδος που δίνει εντολή στο Spring Boot να ξεκινήσει
        SpringApplication.run(CinematchApplication.class, args);
    }
}
