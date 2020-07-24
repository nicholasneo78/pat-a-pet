package com.example.chatapp.Model;

/**
 * A singular Chat Model
 * @author Aleem
 * @version 1.0
 * @since 1.0
 */
public class Chat {

    private String sender;
    private String receiver;
    private String message;
    private boolean isseen;

    /**
     * Creates Chat with the specified sender, receiver, message, isseen
     * @param sender The sender name
     * @param receiver The receiver name
     * @param message The message that is sent
     * @param isseen True/False if receiver received the message
     */
    public Chat(String sender, String receiver, String message, boolean isseen) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.isseen = isseen;
    }

    /**
     * Creates Chat without parameters
     */
    public Chat() {

    }

    /**
     * Gets sender name
     * @return sender's name
     */
    public String getSender() {
        return sender;
    }

    /**
     * Set senders name
     * @param sender The sender name
     */
    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     * Gets the receiver name
     * @return receiver's name
     */
    public String getReceiver() {
        return receiver;
    }

    /**
     * Set receiver's name
     * @param receiver The receiver name
     */
    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    /**
     * Get's the current message
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the current message
     * @param message The current message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Checks if user has read the message
     * @return True or False
     */
    public boolean isIsseen() {
        return isseen;
    }

    /**
     * Sets if user has read the message
     * @param isseen True if seen, false if not
     */
    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }
}
