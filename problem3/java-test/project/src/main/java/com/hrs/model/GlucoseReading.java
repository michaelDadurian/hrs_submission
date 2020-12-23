package com.hrs.model;

/**
 * Representation of glucose readings
 */
public class GlucoseReading {
    private String type;
    private int bloodSugarLevel;

    public GlucoseReading(int bloodSugarLevel) {
        this.type = "glucose"; // match the exact name that appears in the readings files
        this.bloodSugarLevel = bloodSugarLevel;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getBloodSugarLevel() {
        return bloodSugarLevel;
    }

    public void setBloodSugarLevel(int bloodSugarLevel) {
        this.bloodSugarLevel = bloodSugarLevel;
    }

    @Override
    public String toString() {
        return "Glucose Reading: \n\t" + "Blood Sugar: " + this.bloodSugarLevel;
    }
}
