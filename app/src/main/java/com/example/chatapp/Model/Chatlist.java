package com.example.chatapp.Model;

/**
 * A singular Chatlist Model
 * @author Aleem
 * @version 1.0
 * @since 1.0
 */
public class Chatlist {
    public String id;

    /**
     * Creates a chatlist with the specified id
     * @param id The id of the chatlist stored in Database
     */
    public Chatlist(String id) {
        this.id = id;
    }

    /**
     * Creates a Chatlist without parameters
     */
    public Chatlist() {
    }

    /**
     * Get the id of the chatlist
     * @return the id of Chatlist
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id of the chatlist
     * @param id The specified id of a chatlist
     */
    public void setId(String id) {
        this.id = id;
    }
}
