package com.hrs.model;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Parser for rules in JSON format
 */
public class JsonRulesParser implements RulesParser {
    private String fileName;
    private List<Rule> rules;

    public JsonRulesParser(String fileName) {
        this.fileName = fileName;
        this.rules = new ArrayList<>();
    }

    public List<Rule> parseFile() throws IOException {
        byte[] jsonBytes;
      
        // Read in json file and create json tree
        jsonBytes = Files.readAllBytes(Paths.get(this.fileName));
        String jsonStr = new String(jsonBytes, StandardCharsets.UTF_8);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonStr);

        Iterator<Entry<String, JsonNode>> fieldIterator = root.fields();
        // Iterate through each rule type
        while (fieldIterator.hasNext()) {
            Rule rule = new Rule();
            List<Subtype> subtypes = new ArrayList<>();

            Entry<String, JsonNode> ruleEntry = fieldIterator.next();

            String ruleType = ruleEntry.getKey();
            JsonNode ruleSubtypes = ruleEntry.getValue();

            Iterator<Entry<String, JsonNode>> subtypeIterator = ruleSubtypes.fields();
            // Iterate through each rule subtype
            while (subtypeIterator.hasNext()) {
                Subtype subtype = new Subtype();
                Entry<String, JsonNode> subtypeEntry = subtypeIterator.next();

                String subtypeName = subtypeEntry.getKey();
                JsonNode subtypeValues = subtypeEntry.getValue();

                ArrayList<String> subtypeData = mapper.readValue(subtypeValues.traverse(),
                        new TypeReference<ArrayList<String>>() {
                        });

                subtype.setName(subtypeName);
                subtype.setOperation(subtypeData.get(0));
                subtype.setThreshold(Integer.parseInt(subtypeData.get(1)));

                subtypes.add(subtype);
            }

            // Store rule data
            rule.setRuleType(ruleType);
            rule.setSubtypes(subtypes);
            this.rules.add(rule);
        }
    
        return this.rules;
    }

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public List<Rule> getRules() {
		return rules;
	}

	public void setRules(List<Rule> rules) {
		this.rules = rules;
	}
}
