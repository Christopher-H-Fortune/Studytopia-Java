package com.fullsail.christopherfortune.studytopia.DataModels.TestResults;

import java.io.Serializable;

public class TestResults implements Serializable {

    private String testName;
    private String dateAndTimeTaken;
    private String dateTaken;
    private String timeTaken;
    private int questionsRight;
    private int questionsWrong;
    private int answerCount;
    private int skippedCount;

    public TestResults(){

    }

    public TestResults(String testName, String dateAndTimeTaken, String dateTaken, String timeTaken, int questionsRight, int questionsWrong, int answerCount, int skippedCount){
        this.testName = testName;
        this.dateAndTimeTaken = dateAndTimeTaken;
        this.dateTaken = dateTaken;
        this.timeTaken = timeTaken;
        this.questionsRight = questionsRight;
        this.questionsWrong = questionsWrong;
        this.answerCount = answerCount;
        this.skippedCount = skippedCount;
    }

    public String getTestName() {
        return testName;
    }

    public String getDateAndTimeTaken() {
        return dateAndTimeTaken;
    }

    public int getQuestionsRight() {
        return questionsRight;
    }

    public int getQuestionsWrong() {
        return questionsWrong;
    }

    public int getAnswerCount() {
        return answerCount;
    }

    public int getSkippedCount() {
        return skippedCount;
    }

    public String getDateTaken() {
        return dateTaken;
    }

    public String getTimeTaken() {
        return timeTaken;
    }
}
