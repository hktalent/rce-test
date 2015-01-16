/*
 * Copyright (C) 2006-2014 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.communication.connection.internal;

import de.rcenvironment.core.communication.common.CommunicationException;
import de.rcenvironment.core.communication.model.MessageChannel;

/**
 * An exception type for situations when a message should be sent, but the target
 * {@link MessageChannel} is already closed.
 * 
 * @author Robert Mischke
 */
public class ConnectionClosedException extends CommunicationException {

    private static final long serialVersionUID = 712167269948498160L;

    public ConnectionClosedException(String string) {
        super(string);
    }

}
