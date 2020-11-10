package com.fullsail.christopherfortune.studytopia.DataModels.FlashcardTest;

import java.io.Serializable;

public class FlashcardTest implements Serializable {

    private String flashcardTestName;
    private int flashcardCount;
    private boolean flashcardPublic;
    private String flashcardCreationDate;
    private String flashcardTestId;
    private String flashcardTestCreatorId;

    public FlashcardTest(){

    }

    public FlashcardTest(String flashcardTestName, int flashcardCount, boolean flashcardPublic, String flashcardCreationDate, String flashcardTestId, String flashcardTestCreatorId){
        this.flashcardTestName = flashcardTestName;
        this.flashcardCount = flashcardCount;
        this.flashcardPublic = flashcardPublic;
        this.flashcardCreationDate = flashcardCreationDate;
        this.flashcardTestId = flashcardTestId;
        this.flashcardTestCreatorId = flashcardTestCreatorId;
    }

    public String getFlashcardTestCreatorId() {
        return flashcardTestCreatorId;
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

    public String getFlashcardTestId() {
        return flashcardTestId;
    }
}
