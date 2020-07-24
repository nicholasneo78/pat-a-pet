package com.example.chatapp.Notification;

/**
 * This module aids in the development of the app notification
 * @author Aleem
 * @author Koddev
 * @version 1.0
 * @since 1.0
 */
public class Sender {
    public Data data;
    public String to;

    public Sender(Data data, String to) {
        this.data = data;
        this.to = to;
    }
}
