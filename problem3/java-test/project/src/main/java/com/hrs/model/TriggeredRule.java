package com.hrs.model;

/**
 * Representation of a triggered rule
 */
public class TriggeredRule {
    private String type;
    private String subtype;

    public TriggeredRule(){}

    public TriggeredRule(String type, String subtype) {
        this.type = type;
        this.subtype = subtype;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    @Override
    public String toString() {
        return this.type + "." + this.subtype;
    }
}
