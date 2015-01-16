/*
 * Copyright (C) 2006-2014 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.datamodel.api;

/**
 * An exception class for {@link DataType}-related errors. The most typical cause are conversion
 * attempts between incompatible {@link DataType}s.
 * 
 * @author Robert Mischke
 */
public class DataTypeException extends Exception {

    private static final long serialVersionUID = 580720178437562348L;

    public DataTypeException(String message) {
        super(message);
    }
}
