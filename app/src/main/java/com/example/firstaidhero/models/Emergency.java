package com.example.firstaidhero.models;

import java.io.Serializable;

public class Emergency implements Serializable {
    private int caseId;
    private String title;
    private String dangerLevel;
    private String symptoms;

    public Emergency(int caseId, String title, String dangerLevel, String symptoms) {
        this.caseId = caseId;
        this.title = title;
        this.dangerLevel = dangerLevel;
        this.symptoms = symptoms;
    }

    public int getCaseId() {
        return caseId;
    }

    public String getTitle() {
        return title;
    }

    public String getDangerLevel() {
        return dangerLevel;
    }

    public String getSymptoms() {
        return symptoms;
    }
}
