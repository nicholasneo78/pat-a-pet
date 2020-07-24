package com.example.chatapp.Notification;

/**
 * This module aids in the development of the app notification
 * @author Aleem
 * @author Koddev
 * @version 1.0
 * @since 1.0
 */
public class Token {
    private String token;

    public Token(String token) {
        this.token = token;
    }

    public Token() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
