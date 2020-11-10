package com.fullsail.christopherfortune.studytopia.DataModels.User;

public class UserData {

    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String imageUrl;
    private String imageName;
    private int flashcardTestCount;
    private int flashCardCount;
    private int studyGroupCount;
    private boolean isPremium;

    public UserData(){

    }

    public UserData(String firstName, String lastName, String username, String email, String imageUrl, String imageName, int flashcardTestCount, int flashCardCount, int studyGroupCount, boolean isPremium){
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.imageUrl = imageUrl;
        this.imageName = imageName;
        this.flashcardTestCount = flashcardTestCount;
        this.flashCardCount = flashCardCount;
        this.studyGroupCount = studyGroupCount;
        this.isPremium = isPremium;
    }

    public String getImageName() {
        return imageName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getFlashcardTestCount() {
        return flashcardTestCount;
    }

    public void setFlashcardTestCount(int flashcardTestCount) {
        this.flashcardTestCount = flashcardTestCount;
    }

    public int getFlashCardCount() {
        return flashCardCount;
    }

    public void setFlashCardCount(int flashCardCount) {
        this.flashCardCount = flashCardCount;
    }

    public int getStudyGroupCount() {
        return studyGroupCount;
    }

    public void setStudyGroupCount(int studyGroupCount) {
        this.studyGroupCount = studyGroupCount;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }
}
