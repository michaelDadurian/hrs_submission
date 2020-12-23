package com.hrs.model;

/**
 * Representation of a weight reading
 */
public class WeightReading {
    private String type;
    private int weight;

    public WeightReading(int weight) {
        this.type = "weight";
        this.weight = weight;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "Weight Reading: \n\t" + "Weight: " + this.weight;
    }
}
