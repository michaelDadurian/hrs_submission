package com.hrs.model;

import java.util.List;

/**
 * Representation of a Rule read from rules.json
 */
public class Rule {
    private String ruleType;
    private List<Subtype> subtypes;

    public Rule(){}
    
    public Rule(String ruleType){
        this.ruleType = ruleType;
    }

    public Rule(String ruleType, List<Subtype> subtypes) {
        this.ruleType = ruleType;
        this.subtypes = subtypes;
    }

    public String getRuleType() {
        return this.ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    public List<Subtype> getSubtypes() {
        return this.subtypes;
    }

    public void setSubtypes(List<Subtype> subtypes) {
        this.subtypes = subtypes;
    }

}
