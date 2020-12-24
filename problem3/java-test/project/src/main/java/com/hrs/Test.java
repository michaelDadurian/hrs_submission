package com.hrs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hrs.model.BloodPressureReading;
import com.hrs.model.GlucoseReading;
import com.hrs.model.JsonReadingsParser;
import com.hrs.model.JsonRulesParser;
import com.hrs.model.PatientData;
import com.hrs.model.PatientReadings;
import com.hrs.model.Rule;
import com.hrs.model.Subtype;
import com.hrs.model.TriggeredRule;
import com.hrs.model.TriggeredRuleSet;
import com.hrs.model.WeightReading;
import com.hrs.model.YamlReadingsParser;

import org.apache.commons.io.FilenameUtils;

public class Test {

    // Functional interface to evaluate patient readings
    static interface PredicateEvaluator {
        boolean evaluate(int patientValue, int threshold);
    }

    static Map<String, PredicateEvaluator> operatorMap;

    public static void main(String[] args) throws InterruptedException {
        String readingsFile = args.length >= 1 ? args[0] : null;
        String rulesFiles = args.length >= 2 ? args[1] : "rules.json";

        operatorMap = new HashMap<>();

        operatorMap.put("<", new PredicateEvaluator() {
            public boolean evaluate(int patientValue, int threshold) {
                return patientValue < threshold;
            }
        });
        operatorMap.put(">", new PredicateEvaluator() {
            public boolean evaluate(int patientValue, int threshold) {
                return patientValue > threshold;
            }
        });
        operatorMap.put(">=", new PredicateEvaluator() {
            public boolean evaluate(int patientValue, int threshold) {
                return patientValue >= threshold;
            }
        });
        operatorMap.put("<=", new PredicateEvaluator() {
            public boolean evaluate(int patientValue, int threshold) {
                return patientValue <= threshold;
            }
        });

        new Test(rulesFiles, readingsFile);
        Thread.sleep(5);
    }

    public Test(String rulesFile, String readingsFile) {
        // Read resource source file for rules
        List<Rule> rules = new ArrayList<>();
        Map<Integer, PatientData> patientData = new HashMap<>();

        JsonRulesParser jsonRulesParser = new JsonRulesParser(rulesFile);
        try {
            rules = jsonRulesParser.parseFile();

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Read resource source file for values
        String readingsFileExt = FilenameUtils.getExtension(readingsFile);
        if (readingsFileExt.equals("json")) {
            JsonReadingsParser readingsParser = new JsonReadingsParser(readingsFile);
            try {
                patientData = readingsParser.parseFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        else if (readingsFileExt.equals("yml")) {
            YamlReadingsParser readingsParser = new YamlReadingsParser(readingsFile);
            try {
                patientData = readingsParser.parseFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // See which values trigger the rules
        List<TriggeredRuleSet> triggeredRuleSets = findTriggers(patientData, rules);

        // Report the findings to another server
        // (don't need to make the actual http request but code should lead up until
        // that final moment)
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);

            String jsonStr = mapper.writeValueAsString(triggeredRuleSets);

            System.out.println("Data Sync Success");
            System.out.println(jsonStr);

            // Write findings as json file to /code/results.json
            File resultsFile = new File("results.json");
            resultsFile.createNewFile();
            mapper.writeValue(Paths.get("results.json").toFile(), triggeredRuleSets);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the list of triggered rules for each patient
     * @param patientData
     * @param rules
     * @return
     */
    public List<TriggeredRuleSet> findTriggers(Map<Integer, PatientData> patientData, List<Rule> rules) {
        List<TriggeredRuleSet> triggeredRuleSets = new ArrayList<>();
    
        // For every patient, find each rule that triggered
        for (Entry<Integer, PatientData> patientEntry: patientData.entrySet()) {
            // Triggered rule set contains a patient id and a list of triggered rules for that patient
            TriggeredRuleSet triggeredRuleSet = new TriggeredRuleSet();
            List<TriggeredRule> triggeredRules = new ArrayList<>();

            // Collect each patient's id and their readings
            int patientId = patientEntry.getKey();
            PatientReadings patientReadings = patientEntry.getValue().getReadings();

            // Store each reading value to match with rule subtypes
            Map<String, Integer> readingsValues = getPatientReadingValues(patientReadings);

            for (Rule rule: rules) {
                // Collect each triggered rule and subtype
                String ruleType = rule.getRuleType();
                List<Subtype> ruleSubtypes = rule.getSubtypes();

                for (Subtype subtype: ruleSubtypes) {
                    int patientValue = readingsValues.get(subtype.getName());

                    // Evaluate each subtype rule, if it triggers add it to our list of triggered rules
                    if (operatorMap.get(subtype.getOperation()).evaluate(patientValue, subtype.getThreshold())) {
                        triggeredRules.add(new TriggeredRule(ruleType, subtype.getName()));
                    }
                }
            }

            //Store each triggered rule set for each patient
            triggeredRuleSet.setId(patientId);
            triggeredRuleSet.setTriggeredRules(triggeredRules);
            triggeredRuleSets.add(triggeredRuleSet);
        }

        return triggeredRuleSets;
    }

    
    /**
     * Get a map of patient reading values
     * @param patientReadings
     * @return
     */
    public Map<String, Integer> getPatientReadingValues(PatientReadings patientReadings) {
        Map<String, Integer> readingsValues = new HashMap<>();
        readingsValues.put("diastolic", patientReadings.getBpReading().getDiastolic());
        readingsValues.put("systolic", patientReadings.getBpReading().getSystolic());
        readingsValues.put("heartRate", patientReadings.getBpReading().getHeartRate());
        readingsValues.put("bloodSugarLevel", patientReadings.getGlucoseReading().getBloodSugarLevel());
        readingsValues.put("weight", patientReadings.getWeightReading().getWeight());

        return readingsValues;
    }
    
}
