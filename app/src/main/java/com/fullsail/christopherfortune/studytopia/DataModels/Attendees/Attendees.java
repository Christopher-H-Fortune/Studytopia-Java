package com.fullsail.christopherfortune.studytopia.DataModels.Attendees;

public class Attendees {

    private String username;
    private String userProfileImage;

    public Attendees(){

    }

    public Attendees(String username, String userProfileImage){
        this.username = username;
        this.userProfileImage = userProfileImage;
    }

    public String getUsername() {
        return username;
    }

    public String getUserProfileImage() {
        return userProfileImage;
    }
}
