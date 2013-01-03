/*
 * Copyright (C) 2006-2010 DLR, Fraunhofer SCAI, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.rce.authorization.internal;

/**
 * 
 * Provides configuration of this bundle and initializes default configuration.
 * 
 * @author Doreen Seider
 * @author Tobias Menden
 */
public final class AuthorizationConfiguration {

    private String defaultStore = "de.rcenvironment.rce.authorization.xml";

    public void setStore(String defaultsStore) {
        this.defaultStore = defaultsStore;
    } 

    public String getStore() {
        return defaultStore;
    }
}
