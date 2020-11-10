package com.fullsail.christopherfortune.studytopia.DataModels.Leaderboard;

import java.io.Serializable;

public class Leaderboard implements Serializable {

    private String usersUsername;
    private String usersProfileImageUrl;
    private long timeInMinutes;
    private long timeInSeconds;
    private String dateTaken;
    private String timeTaken;

    public Leaderboard(){

    }

    public Leaderboard(String usersUsername, String usersProfileImageUrl, long timeInMinutes, long timeInSeconds, String dateTaken, String timeTaken){
        this.usersUsername = usersUsername;
        this.usersProfileImageUrl = usersProfileImageUrl;
        this.timeInMinutes = timeInMinutes;
        this.timeInSeconds = timeInSeconds;
        this.dateTaken = dateTaken;
        this.timeTaken = timeTaken;
    }

    public String getUsersUsername() {
        return usersUsername;
    }

    public String getUsersProfileImageUrl() {
        return usersProfileImageUrl;
    }

    public long getTimeInMinutes() {
        return timeInMinutes;
    }

    public long getTimeInSeconds() {
        return timeInSeconds;
    }

    public String getDateTaken() {
        return dateTaken;
    }

    public String getTimeTaken() {
        return timeTaken;
    }
}
