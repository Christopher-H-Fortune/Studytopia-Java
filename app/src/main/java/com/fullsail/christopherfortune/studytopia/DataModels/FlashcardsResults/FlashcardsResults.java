package com.fullsail.christopherfortune.studytopia.DataModels.FlashcardsResults;

import java.io.Serializable;

public class FlashcardsResults implements Serializable {

    private int flashcardNumber;

    public FlashcardsResults(){

    }

    public FlashcardsResults(int flashcardNumber){
        this.flashcardNumber = flashcardNumber;
    }

    public int getFlashcardNumber() {
        return flashcardNumber;
    }
}
