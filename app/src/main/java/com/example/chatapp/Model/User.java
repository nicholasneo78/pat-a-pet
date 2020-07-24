package com.example.chatapp.Model;

/**
 * A singular User Model
 * @author Aleem
 * @version 1.0
 * @since 1.0
 */
public class User {

    private String id;
    private String username;
    private String imageURL;
    private String status;
    private String search;
    private String isCaretaker;
    private String aboutme;
    private String experience;
    private String whyCaretaker;
    private String location;


    //Need to update this
    /**
     * Creates a user with the specified id, username, imageURL, status and search
     * @param id The id of the user
     * @param username The username of the user
     * @param imageURL The imageURL used by the user
     * @param status Determines if the user is online/offline
     * @param search Lowercased username for easy search
     */
    public User(String id, String username, String imageURL, String status, String search,
                String isCaretaker, String aboutme, String experience, String whyCaretaker,
                String location) {
        this.id = id;
        this.username = username;
        this.imageURL = imageURL;
        this.status = status;
        this.search = search;
        this.isCaretaker = isCaretaker;
        this.aboutme = aboutme;
        this.experience = experience;
        this.whyCaretaker = whyCaretaker;
        this.location = location;
    }

    /**
     * Creates a user without parameters
     */
    public User() {

    }

    /**
     * Gets the ID of the user
     * @return ID of user
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the ID of the user
     * @param id ID of user
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the username of the user
     * @return Username of current user
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username for the user
     * @param username The specified username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the imageURL of the user
     * @return the URL of the profile image
     */
    public String getImageURL() {
        return imageURL;
    }

    /**
     * Sets the URL of the user's profile image
     * @param imageURL The specified Image URL
     */
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    /**
     * Get the status of user
     * @return Online/Offline
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the user
     * @param status Online/Offline
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the search username of the user
     * @return The lowercased username
     */
    public String getSearch() {
        return search;
    }

    /**
     * Sets the search username of the user
     * @param search The lowercased username of the user
     */
    public void setSearch(String search) {
        this.search = search;
    }

    /**
     * Gets the String of isCaretaker
     * @return String
     */
    public String getIsCaretaker() {
        return isCaretaker;
    }

    /**
     * Sets the String of isCaretaker
     * @param isCaretaker the String specified
     */
    public void setIsCaretaker(String isCaretaker) {
        this.isCaretaker = isCaretaker;
    }

    /**
     * Gets the String for aboutme
     * @return String
     */
    public String getAboutMe() {
        return aboutme;
    }

    /**
     * Sets the String for aboutme
     * @param aboutMe The String specified
     */
    public void setAboutMe(String aboutMe) {
        this.aboutme = aboutMe;
    }

    /**
     * Gets the String for experience
     * @return String
     */
    public String getExperience() {
        return experience;
    }

    /**
     * Sets the String for experience
     * @param experience The String specified
     */
    public void setExperience(String experience) {
        this.experience = experience;
    }

    /**
     * Gets the String for whyCaretaker
     * @return String
     */
    public String getWhyCaretaker() {
        return whyCaretaker;
    }

    /**
     * Sets the String for whyCaretaker
     * @param whyCaretaker The String specified
     */
    public void setWhyCaretaker(String whyCaretaker) {
        this.whyCaretaker = whyCaretaker;
    }

    /**
     * Gets the String for getLocation
     * @return String
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the String for getLocation
     * @param location The String specified
     */
    public void setLocation(String location) {
        this.location = location;
    }
}
