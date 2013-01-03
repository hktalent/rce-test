/*
 * Copyright (C) 2006-2010 DLR, Fraunhofer SCAI, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */
 
package de.rcenvironment.core.start.common.validation;

/**
 * Message informing about validation occurrences.
 *
 * @author Christian Weiss
 */
public class PlatformMessage {

    /**
     * Type of a message.
     *
     * @author Christian Weiss
     */
    public enum Type {
        /** A non-critical warning. */
        WARNING,
        /** A critical error. */
        ERROR;
    }

    private final Type type;

    private final String bundleSymbName;

    private final String message;

    public PlatformMessage(final Type type, final String bundleSymbolicName, final String message) {
        this.type = type;
        this.bundleSymbName = bundleSymbolicName;
        this.message = message;
    }

    public Type getType() {
        return type;
    }

    public String getBundleSymbolicName() {
        return bundleSymbName;
    }

    public String getMessage() {
        return message;
    }

}
