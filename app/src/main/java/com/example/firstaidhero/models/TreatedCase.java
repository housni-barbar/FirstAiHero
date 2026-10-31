package com.example.firstaidhero.models;

import java.io.Serializable;

public class TreatedCase implements Serializable {
    private int treatedId;
    private String description;
    private String date;
    private String photo;
    private String procedures;

    public TreatedCase(int treatedId, String description, String date, String photo, String procedures) {
        this.treatedId = treatedId;
        this.description = description;
        this.date = date;
        this.photo = photo;
        this.procedures = procedures;
    }

    public int getTreatedId() {
        return treatedId;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getPhoto() {
        return photo;
    }

    public String getProcedures() {
        return procedures;
    }
}
