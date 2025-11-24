package com.cinematch.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class UserDashboard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
}
