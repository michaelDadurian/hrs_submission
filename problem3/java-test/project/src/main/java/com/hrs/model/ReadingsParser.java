package com.hrs.model;

import java.util.Map;

/**
 * Second level interface, classes that implement ReadingsParser are designed
 * to read in patient readings.
 */
public interface ReadingsParser extends FileParser<Map<Integer, PatientData>> {}
