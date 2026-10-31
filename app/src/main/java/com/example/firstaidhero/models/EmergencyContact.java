package com.example.firstaidhero.models;

import java.io.Serializable;

public class EmergencyContact implements Serializable {
    private int contactId;
    private String contactName;
    private String phoneNumber;

    public EmergencyContact(int contactId, String contactName, String phoneNumber) {
        this.contactId = contactId;
        this.contactName = contactName;
        this.phoneNumber = phoneNumber;
    }

    public int getContactId() {
        return contactId;
    }

    public String getContactName() {
        return contactName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
