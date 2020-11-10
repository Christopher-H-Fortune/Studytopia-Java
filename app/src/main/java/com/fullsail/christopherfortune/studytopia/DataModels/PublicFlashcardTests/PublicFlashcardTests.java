package com.fullsail.christopherfortune.studytopia.DataModels.PublicFlashcardTests;

import java.io.Serializable;

public class PublicFlashcardTests implements Serializable {

    private String flashcardTestId;
    private String flashcardTestName;
    private int flashcardCount;
    private boolean flashcardPublic;
    private String flashcardCreationDate;
    private String flashcardTestCreator;

    public PublicFlashcardTests(){

    }

    public PublicFlashcardTests(String flashcardTestId, String flashcardTestName, int flashcardCount, boolean flashcardPublic, String flashcardCreationDate, String flashcardTestCreator){
        this.flashcardTestId = flashcardTestId;
        this.flashcardTestName = flashcardTestName;
        this.flashcardCount = flashcardCount;
        this.flashcardPublic = flashcardPublic;
        this.flashcardCreationDate = flashcardCreationDate;
        this.flashcardTestCreator = flashcardTestCreator;
    }

    public String getFlashcardTestId() {
        return flashcardTestId;
    }

    public String getFlashcardTestName() {
        return flashcardTestName;
    }

    public void setFlashcardTestName(String flashcardTestName) {
        this.flashcardTestName = flashcardTestName;
    }

    public int getFlashcardCount() {
        return flashcardCount;
    }

    public void setFlashcardCount(int flashcardCount) {
        this.flashcardCount = flashcardCount;
    }

    public boolean isFlashcardPublic() {
        return flashcardPublic;
    }

    public void setFlashcardPublic(boolean flashcardPublic) {
        this.flashcardPublic = flashcardPublic;
    }

    public String getFlashcardCreationDate() {
        return flashcardCreationDate;
    }

    public void setFlashcardCreationDate(String flashcardCreationDate) {
        this.flashcardCreationDate = flashcardCreationDate;
    }

    public String getFlashcardTestCreator() {
        return flashcardTestCreator;
    }

    public void setFlashcardTestCreator(String flashcardTestCreator) {
        this.flashcardTestCreator = flashcardTestCreator;
    }
}
