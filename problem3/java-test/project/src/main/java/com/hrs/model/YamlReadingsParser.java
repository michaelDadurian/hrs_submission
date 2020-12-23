package com.hrs.model;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

/**
 * Parser for patient readings in YAML format
 */
public class YamlReadingsParser implements ReadingsParser {
    private String fileName;


    public YamlReadingsParser(String fileName) {
        this.fileName = fileName;
    }

    public HashMap<Integer, PatientData> parseFile() throws IOException{
        HashMap<Integer, PatientData> patientDataMap = new HashMap<>();
        byte[] yamlBytes;

        // Read in yaml file and create the Json tree
        yamlBytes = Files.readAllBytes(Paths.get(this.fileName));
        String yamlStr = new String(yamlBytes, StandardCharsets.UTF_8);

        ObjectMapper mapper = new YAMLMapper();
        JsonNode root = mapper.readTree(yamlStr);

        Iterator<Entry<String, JsonNode>> fieldIterator = root.fields();
        
        // Iterate through each patient reading
        while (fieldIterator.hasNext()) {
            PatientData patientData = new PatientData();
            PatientReadings patientReadings = new PatientReadings();

            Entry<String, JsonNode> readingEntry = fieldIterator.next();

            // Retrieve patient id and their associated readings
            Integer patientDataId = Integer.parseInt(readingEntry.getKey());
            JsonNode readingsDataNode = readingEntry.getValue().get("readings");
            Iterator<Entry<String, JsonNode>> readingsIterator = readingsDataNode.fields();

            // Iterate through the patient's readings
            while (readingsIterator.hasNext()) {
                Entry<String, JsonNode> reading = readingsIterator.next();
                String readingType = reading.getKey();
                JsonNode readingDataNode = reading.getValue();

                // Get blood pressure reading
                if (readingType.equals("bloodPressure")) {
                    int systolic = readingDataNode.get("systolic").asInt();
                    int diastolic = readingDataNode.get("diastolic").asInt();
                    int heartRate = readingDataNode.get("heartRate").asInt();

                    BloodPressureReading bpReading = new BloodPressureReading(systolic, diastolic, heartRate);
                    patientReadings.setBpReading(bpReading);
                }

                // Get glucose reading
                else if (readingType.equals("glucose")) {
                    int bloodSugarLevel = readingDataNode.get("bloodSugarLevel").asInt();

                    GlucoseReading glucoseReading = new GlucoseReading(bloodSugarLevel);
                    patientReadings.setGlucoseReading(glucoseReading);
                }

                // Get weight reading
                else if (readingType.equals("weight")) {
                    int weight = readingDataNode.get("weight").asInt();

                    WeightReading weightReading = new WeightReading(weight);
                    patientReadings.setWeightReading(weightReading);
                }
            }

            // Store patient data
            patientData.setId(patientDataId);
            patientData.setReadings(patientReadings);

            patientDataMap.put(patientDataId, patientData);
        }

        return patientDataMap;
    }
}
