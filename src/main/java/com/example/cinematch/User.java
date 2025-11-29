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
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTakis() {
        this.email = "123";
    }
}
