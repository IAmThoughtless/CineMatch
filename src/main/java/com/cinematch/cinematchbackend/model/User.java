package com.cinematch.cinematchbackend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users") // <--- Make sure this matches your MySQL table name
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;
}