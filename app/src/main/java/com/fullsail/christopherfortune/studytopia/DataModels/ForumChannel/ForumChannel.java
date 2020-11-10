package com.fullsail.christopherfortune.studytopia.DataModels.ForumChannel;

import java.io.Serializable;

public class ForumChannel implements Serializable {

    private String channelName;
    private String creatorId;
    private String creatorUsername;
    private String channelId;
    private int totalMessages;

    public ForumChannel(){}

    public ForumChannel(String channelName, String creatorId, String creatorUsername, String channelId, int totalMessages){
        this.channelName = channelName;
        this.creatorId = creatorId;
        this.creatorUsername = creatorUsername;
        this.channelId = channelId;
        this.totalMessages = totalMessages;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public String getCreatorUsername() {
        return creatorUsername;
    }

    public String getChannelId() {
        return channelId;
    }

    public int getTotalMessages() {
        return totalMessages;
    }
}
