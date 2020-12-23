package com.hrs.model;

/**
 * Representation of a patient reading. Each patient reading is composed of readings for bp, glucose, and weight.
 */
public class PatientReadings {
    private BloodPressureReading bpReading;
    private GlucoseReading glucoseReading;
    private WeightReading weightReading;

    public PatientReadings(){}
    
    public PatientReadings(BloodPressureReading bpReading, GlucoseReading glucoseReading, WeightReading weightReading) {
        this.bpReading = bpReading;
        this.glucoseReading = glucoseReading;
        this.weightReading = weightReading;
    }

    public BloodPressureReading getBpReading() {
        return bpReading;
    }

    public void setBpReading(BloodPressureReading bpReading) {
        this.bpReading = bpReading;
    }

    public GlucoseReading getGlucoseReading() {
        return glucoseReading;
    }

    public void setGlucoseReading(GlucoseReading glucoseReading) {
        this.glucoseReading = glucoseReading;
    }

    public WeightReading getWeightReading() {
        return weightReading;
    }

    public void setWeightReading(WeightReading weightReading) {
        this.weightReading = weightReading;
    }
}
