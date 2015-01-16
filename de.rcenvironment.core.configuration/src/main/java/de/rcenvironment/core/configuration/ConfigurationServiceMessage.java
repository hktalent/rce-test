/*
 * Copyright (C) 2006-2014 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */
 
package de.rcenvironment.core.configuration;


/**
 * An error which occurred in a {@link ConfigurationService}.
 *
 * @author Christian Weiss
 */
public class ConfigurationServiceMessage {
    
    private final String message;
    
    public ConfigurationServiceMessage(final String message) {
        this.message = message;
    }
    
    public String getMessage() {
        return message;
    }
    
    

}
