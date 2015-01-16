/*
 * Copyright (C) 2006-2014 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.communication.connection.api;

import de.rcenvironment.core.communication.common.CommunicationException;
import de.rcenvironment.core.communication.model.MessageChannel;

/**
 * Represents a connection setup, ie a configured network destination that a logical message channel can be established to.
 * 
 * @author Robert Mischke
 */
public interface ConnectionSetup {

    /**
     * @return the current {@link ConnectionSetupState} of the connection
     */
    ConnectionSetupState getState();

    /**
     * @return the reason for the last disconnect; only non-null in the DISCONNECTING and DISCONNECTED {@link ConnectionSetupState}s.
     */
    DisconnectReason getDisconnectReason();

    /**
     * Initiates an synchronous connection attempt.
     * 
     * @throws CommunicationException on connection errors
     */
    void connectSync() throws CommunicationException;

    /**
     * Signals that an active connection is desired; may trigger an asynchronous connection attempt.
     */
    void signalStartIntent();

    /**
     * Signals that an active connection is not desired (anymore); may trigger an asynchronous disconnect.
     */
    void signalStopIntent();

    /**
     * @return the display name specified on creation
     */
    String getDisplayName();

    /**
     * @return the numeric, JVM-unique id of this setup; for use by console commands, for example
     */
    long getId();

    /**
     * @return true if this connection should automatically try to connect on instance startup
     */
    boolean getConnnectOnStartup();

    /**
     * @return the string definition of the {@link NetworkContactPoint} to connect to
     */
    String getNetworkContactPointString();

    /**
     * @return the currently associated {@link MessageChannel}; only non-null in CONNECTED and DISCONNECTED {@link ConnectionSetupState}s
     */
    MessageChannel getCurrentChannel();

    /**
     * @return the id of the currently associated {@link MessageChannel}; only non-null when CONNECTED
     */
    String getCurrentChannelId();

    /**
     * @return the id of the last associated {@link MessageChannel}; may be null
     */
    String getLastChannelId();

}
