/*
 * Copyright (C) 2006-2010 DLR, Fraunhofer SCAI, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.rce.authentication;

/**
 * This exception will be thrown if an authentication related operations fails.
 * 
 * @author Doreen Seider
 */
public class AuthenticationException extends Exception {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = 2911996501788218615L;

    /**
     * 
     * Creates an instance of this exception.
     * 
     * @param string A text message describing the error.
     */
    public AuthenticationException(String string) {
        super(string);
    }

    /**
     * Creates an instance of this exception.
     * 
     * @param cause The cause for this exception.
     */
    public AuthenticationException(Throwable cause) {
        super(cause);
    }

    /**
     * 
     * Creates an instance of this exception.
     * 
     * @param string A text message describing the error.
     * @param cause The cause of this exception
     */
    public AuthenticationException(String string, Throwable cause) {
        super(string, cause);
    }

}
