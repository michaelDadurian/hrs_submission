package com.hrs.model;

/**
 * Representation of patient data
 */
public class PatientData {
    private int id;
    private PatientReadings readings; // Contains readings of each type (bp, glucose, weight)

    public PatientData() {}
    
    public PatientData (int id, PatientReadings readings) {
        this.id = id;
        this.readings = readings;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PatientReadings getReadings() {
        return readings;
    }

    public void setReadings(PatientReadings readings) {
        this.readings = readings;
    }
}
