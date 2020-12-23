package com.hrs.model;

import java.util.List;

/**
 * A mapping of patient ids to a list of rules that were triggered by their readings
 */
public class TriggeredRuleSet {
    private int id;
    private List<TriggeredRule> triggeredRules;

    public TriggeredRuleSet(){}
    
    public TriggeredRuleSet(int id, List<TriggeredRule> triggeredRules) {
        this.id = id;
        this.triggeredRules = triggeredRules;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<TriggeredRule> getTriggeredRules() {
        return triggeredRules;
    }

    public void setTriggeredRules(List<TriggeredRule> triggeredRules) {
        this.triggeredRules = triggeredRules;
    }
    
}
