package com.example.cinematch;

public class UserSession {

    // 1. The single instance of this class
    private static UserSession instance;

    // 2. The data we want to remember
    private String username;

    // Private constructor so nobody can make a new instance manually
    private UserSession() {}

    // 3. Method to get the storage box
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    // 4. Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // 5. Clear session (for Logout)
    public void cleanUserSession() {
        username = null;
    }

    public boolean isLoggedIn() {
        return username != null;
    }
}