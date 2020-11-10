package com.fullsail.christopherfortune.studytopia.DataModels.TopicOfTheDay;

import java.io.Serializable;

public class TopicOfTheDay implements Serializable {

    private String topic;
    private boolean isDisplayedYet;
    private int topicNumber;

    public TopicOfTheDay(){}

    public TopicOfTheDay(String topic, boolean isDisplayedYet, int topicNumber){
        this.topic = topic;
        this.isDisplayedYet = isDisplayedYet;
        this.topicNumber = topicNumber;
    }

    public String getTopic() {
        return topic;
    }

    public boolean isDisplayedYet() {
        return isDisplayedYet;
    }

    public int getTopicNumber() {
        return topicNumber;
    }

    public void setTopicNumber(int topicNumber) {
        this.topicNumber = topicNumber;
    }
}
