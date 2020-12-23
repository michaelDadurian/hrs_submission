package com.hrs.model;

import java.util.List;

/**
 * Second level interface, classes that implement RulesParser are designed
 * to read in rules.
 */
public interface RulesParser extends FileParser<List<Rule>> {}
