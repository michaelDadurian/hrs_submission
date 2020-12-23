package com.hrs.model;

/**
 * Representation of a Rule subtype
 */
public class Subtype {
    private String name;
    private String operation;
    private int threshold;

    public Subtype(){}
    
    public Subtype(String name, String operation, int threshold) {
        this.name = name;
        this.operation = operation;
        this.threshold = threshold;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOperation() {
        return this.operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public int getThreshold() {
        return this.threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }
}
