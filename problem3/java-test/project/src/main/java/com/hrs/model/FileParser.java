package com.hrs.model;

import java.io.IOException;

/**
 * Top level interface of a file parser. Each implementation of FileParser.parseFile()
 * must return an object defined by the parser type.
 * 
 * e.g
 * ReadingsParsers will return a map of PatientData objects, while RulesParsers will return a list of Rule objects
 */
public interface FileParser<T> {
    public T parseFile() throws IOException;
}