package com.fullsail.christopherfortune.studytopia.DataModels.Subjects;

public class Subjects {

    private String subjectString;
    private int subjectIcon;

    public Subjects(){}

    public Subjects(String subjectString, int subjectIcon){
        this.subjectString = subjectString;
        this.subjectIcon = subjectIcon;
    }

    public String getSubjectString() {
        return subjectString;
    }

    public int getSubjectIcon() {
        return subjectIcon;
    }
}
