package com.fullsail.christopherfortune.studytopia.DataModels.StudyGroupJoined;

public class StudyGroupJoined {

    private String studyGroupJoinedName;
    private String studyGroupId;
    private String studyGroupDate;

    public StudyGroupJoined(){}

    public StudyGroupJoined(String studyGroupJoinedName, String studyGroupId, String studyGroupDate){
        this.studyGroupJoinedName = studyGroupJoinedName;
        this.studyGroupId = studyGroupId;
        this.studyGroupDate = studyGroupDate;
    }

    public String getStudyGroupJoinedName() {
        return studyGroupJoinedName;
    }

    public void setStudyGroupJoinedName(String studyGroupJoinedName) {
        this.studyGroupJoinedName = studyGroupJoinedName;
    }

    public String getStudyGroupId() {
        return studyGroupId;
    }

    public void setStudyGroupId(String studyGroupId) {
        this.studyGroupId = studyGroupId;
    }

    public String getStudyGroupDate() {
        return studyGroupDate;
    }

    public void setStudyGroupDate(String studyGroupDate) {
        this.studyGroupDate = studyGroupDate;
    }
}
