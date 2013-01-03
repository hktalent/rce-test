/*
 * Copyright (C) 2006-2012 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.communication.utils;

/**
 * Exception class for errors during serialization or deserialization.
 * 
 * @author Robert Mischke
 */
public class SerializationException extends Exception {

    private static final long serialVersionUID = 6985871603095457656L;

    public SerializationException(Throwable ex) {
        super(ex);
    }

    public SerializationException(String msg) {
        super(msg);
    }

}
