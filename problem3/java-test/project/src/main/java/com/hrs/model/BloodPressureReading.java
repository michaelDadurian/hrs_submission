package com.hrs.model;

/**
 * Representation of a Blood Pressure reading
 */
public class BloodPressureReading {
    private String type;
    private int systolic;
    private int diastolic;
    private int heartRate;

    public BloodPressureReading(int systolic, int diastolic, int heartRate) {
        this.type = "bloodPressure"; // match the exact name that appears in the readings files
        this.systolic = systolic;
        this.diastolic = diastolic;
        this.heartRate = heartRate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSystolic() {
        return systolic;
    }

    public void setSystolic(int systolic) {
        this.systolic = systolic;
    }

    public int getDiastolic() {
        return diastolic;
    }

    public void setDiastolic(int diastolic) {
        this.diastolic = diastolic;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }
    
    @Override
    public String toString() {
        return "Blood Pressure Reading: \n\t" 
        + "Systolic: " + this.systolic + "\n\t" 
        + "Diastolic: " + this.diastolic + "\n\t" 
        + "Heart Rate: " + this.heartRate;
    }
    
}
