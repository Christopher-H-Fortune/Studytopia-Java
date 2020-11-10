package com.fullsail.christopherfortune.studytopia.DataModels.ForumMessage;

import java.io.Serializable;

public class ForumMessage implements Serializable {

    private String messageEntered;
    private String timeSent;
    private String dateSent;
    private String creatorUsername;
    private String creatorId;
    private String dateTimeSent;

    public ForumMessage(){}

    public ForumMessage(String messageEntered, String timeSent, String dateSent, String creatorUsername, String creatorId, String dateTimeSent){
        this.messageEntered = messageEntered;
        this.timeSent = timeSent;
        this.dateSent = dateSent;
        this.creatorUsername = creatorUsername;
        this.creatorId = creatorId;
        this.dateTimeSent = dateTimeSent;
    }

    public String getTimeSent() {
        return timeSent;
    }

    public String getMessageEntered() {
        return messageEntered;
    }

    public String getDateSent() {
        return dateSent;
    }

    public String getCreatorUsername() {
        return creatorUsername;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public String getDateTimeSent() {
        return dateTimeSent;
    }
}
