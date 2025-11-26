package com.cinematch.payload.response;

/**
 * DTO (Data Transfer Object) για την αποστολή απλών μηνυμάτων
 * από τον server στον client (π.χ. σε επιτυχή εγγραφή ή σε σφάλμα).
 */
public class MessageResponse {
    private String message;

    // Constructor
    public MessageResponse(String message) {
        this.message = message;
    }

    // Getters και Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}