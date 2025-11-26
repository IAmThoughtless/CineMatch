package com.example.cinematch;

public class User {
    private String username;
    private String password;
    private String email;

    public User(String email, String username, String password){
        this.email=email;
        this.username=username;
        this.password=password;
    }
    public User(String username, String password){
        this.username=username;
        this.password=password;
        this.email=email;
    }
}
