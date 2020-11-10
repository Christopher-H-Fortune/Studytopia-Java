package com.fullsail.christopherfortune.studytopia.DataModels.StudyGroup;

import java.io.Serializable;

public class StudyGroup implements Serializable {
    private String studyGroupCreatorImgUrl;
    private String studyGroupName;
    private String studyGroupSubject;
    private String studyGroupCompleteAddress;
    private String studyGroupAddress;
    private String studyGroupCity;
    private String studyGroupState;
    private double studyGroupLat;
    private double studyGroupLong;
    private String studyGroupDate;
    private String studyGroupTime;
    private int studyGroupAttendeeCount;
    private boolean isOnMap;
    private String creatorUsername;
    private String studyGroupId;
    private int studyGroupHour;
    private int studyGroupMinute;
    private int studyGroupDay;
    private int studyGroupMonth;
    private int studyGroupYear;

    public StudyGroup(){

    }

    public StudyGroup(String studyGroupCreatorImgUrl ,String studyGroupName, String studyGroupSubject, String studyGroupCompleteAddress, String studyGroupAddress, String studyGroupCity, String studyGroupState,double studyGroupLat, double studyGroupLong, String studyGroupDate, String studyGroupTime, int studyGroupAttendeeCount, boolean isOnMap, String creatorUsername, String studyGroupId, int studyGroupHour, int studyGroupMinute, int studyGroupDay, int studyGroupMonth, int studyGroupYear){

        this.studyGroupCreatorImgUrl = studyGroupCreatorImgUrl;
        this.studyGroupName = studyGroupName;
        this.studyGroupSubject = studyGroupSubject;
        this.studyGroupCompleteAddress = studyGroupCompleteAddress;
        this.studyGroupAddress = studyGroupAddress;
        this.studyGroupCity = studyGroupCity;
        this.studyGroupState = studyGroupState;
        this.studyGroupLat = studyGroupLat;
        this.studyGroupLong = studyGroupLong;
        this.studyGroupDate = studyGroupDate;
        this.studyGroupTime = studyGroupTime;
        this.studyGroupAttendeeCount = studyGroupAttendeeCount;
        this.isOnMap = isOnMap;
        this.creatorUsername = creatorUsername;
        this.studyGroupId = studyGroupId;
        this.studyGroupHour = studyGroupHour;
        this.studyGroupMinute = studyGroupMinute;
        this.studyGroupDay = studyGroupDay;
        this.studyGroupMonth = studyGroupMonth;
        this.studyGroupYear = studyGroupYear;
    }

    public String getStudyGroupCreatorImgUrl() {
        return studyGroupCreatorImgUrl;
    }

    public String getStudyGroupName() {
        return studyGroupName;
    }

    public String getStudyGroupSubject() {
        return studyGroupSubject;
    }

    public String getStudyGroupCompleteAddress() {
        return studyGroupCompleteAddress;
    }

    public double getStudyGroupLat() {
        return studyGroupLat;
    }

    public double getStudyGroupLong() {
        return studyGroupLong;
    }

    public String getStudyGroupDate() {
        return studyGroupDate;
    }

    public String getStudyGroupTime() {
        return studyGroupTime;
    }

    public int getStudyGroupAttendeeCount() {
        return studyGroupAttendeeCount;
    }

    public boolean isOnMap() {
        return isOnMap;
    }

    public String getCreatorUsername() {
        return creatorUsername;
    }

    public String getStudyGroupAddress() {
        return studyGroupAddress;
    }

    public String getStudyGroupCity() {
        return studyGroupCity;
    }

    public String getStudyGroupState() {
        return studyGroupState;
    }

    public String getStudyGroupId() {
        return studyGroupId;
    }

    public int getStudyGroupHour() {
        return studyGroupHour;
    }

    public int getStudyGroupMinute() {
        return studyGroupMinute;
    }

    public int getStudyGroupDay() {
        return studyGroupDay;
    }

    public int getStudyGroupMonth() {
        return studyGroupMonth;
    }

    public int getStudyGroupYear() {
        return studyGroupYear;
    }
}
