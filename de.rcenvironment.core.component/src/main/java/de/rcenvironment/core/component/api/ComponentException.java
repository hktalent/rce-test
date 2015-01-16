/*
 * Copyright (C) 2006-2014 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.component.api;


/**
 * This exception will be thrown if an error within a {@link Component} occurred.
 * 
 * @author Doreen Seider
 */
public class ComponentException extends Exception {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = 2911996501788218615L;

    /**
     * Creates an instance of this exception.
     * 
     * @param string A text message describing the error.
     */
    public ComponentException(String string) {
        super(string);
    }

    /**
     * Creates an instance of this exception.
     * 
     * @param cause The cause for this exception.
     */
    public ComponentException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates an instance of this exception.
     * 
     * @param string A text message describing the error.
     * @param cause The cause of this exception
     */
    public ComponentException(String string, Throwable cause) {
        super(string, cause);
    }

}
