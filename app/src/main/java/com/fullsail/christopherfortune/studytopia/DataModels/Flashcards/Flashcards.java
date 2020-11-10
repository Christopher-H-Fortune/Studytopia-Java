package com.fullsail.christopherfortune.studytopia.DataModels.Flashcards;

import java.io.Serializable;

public class Flashcards implements Serializable {

    private String question;
    private String answer;
    private int questionNumber;

    public Flashcards(){

    }

    public Flashcards(String question, String answer, int questionNumber){
        this.question = question;
        this.answer = answer;
        this.questionNumber = questionNumber;
    }

    @Override
    public String toString() {
        return "Flashcards{" +
                "question='" + question + '\'' +
                ", answer='" + answer + '\'' +
                ", questionNumber=" + questionNumber +
                '}';
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(int questionNumber) {
        this.questionNumber = questionNumber;
    }
}
