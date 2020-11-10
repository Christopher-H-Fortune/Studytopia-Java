package com.fullsail.christopherfortune.studytopia.DataModels.Usernames;

import java.io.Serializable;

public class Usernames implements Serializable {

    private String userName;

    public Usernames(){}

    public Usernames(String userName){

        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }
}
