package com.example.cinematch;

public class UserSession {

    // 1. The single instance of this class
    private static UserSession instance;

    // 2. The data we want to remember
    private String username;
    private Long userId;

    private String returnToUrl;


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

    public String getReturnToUrl() {
        return returnToUrl;
    }

    public void setReturnToUrl(String returnToUrl) {
        this.returnToUrl = returnToUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    // 5. Clear session (for Logout)
    public void cleanUserSession() {
        username = null;
        userId = null;
        returnToUrl = null; // Καθαρίζουμε και το URL κατά το Logout
    }

    public boolean isLoggedIn() {
        return username != null;
    }
}
