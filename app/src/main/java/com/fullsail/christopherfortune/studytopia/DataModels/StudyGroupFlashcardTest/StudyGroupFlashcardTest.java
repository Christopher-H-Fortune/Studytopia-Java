package com.fullsail.christopherfortune.studytopia.DataModels.StudyGroupFlashcardTest;

import java.io.Serializable;

public class StudyGroupFlashcardTest implements Serializable {

    private String flashcardTestName;
    private int flashcardCount;
    private String flashcardCreationDate;
    private String flashcardTestId;
    private String creatorUsername;

    public StudyGroupFlashcardTest(){

    }

    public StudyGroupFlashcardTest(String flashcardTestName, int flashcardCount, String flashcardCreationDate, String flashcardTestId, String creatorUsername){
        this.flashcardTestName = flashcardTestName;
        this.flashcardCount = flashcardCount;
        this.flashcardCreationDate = flashcardCreationDate;
        this.flashcardTestId = flashcardTestId;
        this.creatorUsername = creatorUsername;
    }

    public String getFlashcardTestName() {
        return flashcardTestName;
    }

    public int getFlashcardCount() {
        return flashcardCount;
    }

    public String getFlashcardCreationDate() {
        return flashcardCreationDate;
    }

    public String getFlashcardTestId() {
        return flashcardTestId;
    }

    public String getCreatorUsername() {
        return creatorUsername;
    }
}
