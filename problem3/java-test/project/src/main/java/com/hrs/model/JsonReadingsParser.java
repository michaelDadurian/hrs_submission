package com.hrs.model;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * Parser for patient readings in JSON format
 */
public class JsonReadingsParser implements ReadingsParser {
    private String fileName;
    private HashMap<Integer, PatientData> patientDataMap;

    public JsonReadingsParser(String fileName) {
        this.fileName = fileName;
        this.patientDataMap = new HashMap<>();
    }

    public HashMap<Integer, PatientData> parseFile() throws IOException {
        byte[] jsonBytes;

        // Read in json file
        jsonBytes = Files.readAllBytes(Paths.get(this.fileName));
        String jsonStr = new String(jsonBytes, StandardCharsets.UTF_8);

        // Remove nefarious newline characters found in strings
        String sanitizedJsonStr = sanitizeJson(jsonStr);

        // Create json tree
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode root = (ArrayNode) mapper.readTree(sanitizedJsonStr);

        // root contains a list of json nodes, each containing a patient's readings
        for (int i = 0; i < root.size(); i++) {
            PatientData patientData = new PatientData();
            PatientReadings patientReadings = new PatientReadings();
            JsonNode patientDataNode = root.get(i);

            Integer patientDataId = patientDataNode.get("id").asInt();
            ArrayNode readingsNode = (ArrayNode) patientDataNode.get("readings");

            // For this specific patient, iterate over their readings
            for (int j = 0; j < readingsNode.size(); j++) {
                JsonNode readingsDataNode = readingsNode.get(j);

                String readingType = readingsDataNode.get("type").asText();

                // Get blood pressure reading
                if (readingType.equals("bloodPressure")) {
                    int systolic = readingsDataNode.get("systolic").asInt();
                    int diastolic = readingsDataNode.get("diastolic").asInt();
                    int heartRate = readingsDataNode.get("heartRate").asInt();

                    BloodPressureReading bpReading = new BloodPressureReading(systolic, diastolic, heartRate);
                    patientReadings.setBpReading(bpReading);
                } 
                
                // Get glucose reading
                else if (readingType.equals("glucose")) {
                    int bloodSugarLevel = readingsDataNode.get("bloodSugarLevel").asInt();

                    GlucoseReading glucoseReading = new GlucoseReading(bloodSugarLevel);
                    patientReadings.setGlucoseReading(glucoseReading);
                }

                // Get weight reading
                else if (readingType.equals("weight")) {
                    int weight = readingsDataNode.get("weight").asInt();

                    WeightReading weightReading = new WeightReading(weight);
                    patientReadings.setWeightReading(weightReading);
                }
            }

            // Store patient data
            patientData.setId(patientDataId);
            patientData.setReadings(patientReadings);
            this.patientDataMap.put(patientDataId, patientData);
        }

        return this.patientDataMap;
    }
    
    public String sanitizeJson(String jsonStr) {
        return jsonStr.replace("\\n", "");
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Map<Integer, PatientData> getPatientDataMap() {
        return patientDataMap;
    }

    public void setPatientDataMap(HashMap<Integer, PatientData> patientDataMap) {
        this.patientDataMap = patientDataMap;
    }
}
