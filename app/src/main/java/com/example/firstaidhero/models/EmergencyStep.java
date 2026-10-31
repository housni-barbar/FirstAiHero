package com.example.firstaidhero.models;

import java.io.Serializable;

public class EmergencyStep implements Serializable {
    private int stepId;
    private int caseId;
    private int stepNumber;
    private String instructionText;

    public EmergencyStep(int stepId, int caseId, int stepNumber, String instructionText) {
        this.stepId = stepId;
        this.caseId = caseId;
        this.stepNumber = stepNumber;
        this.instructionText = instructionText;
    }

    public int getStepId() {
        return stepId;
    }

    public int getCaseId() {
        return caseId;
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public String getInstructionText() {
        return instructionText;
    }
}
