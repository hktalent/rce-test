/*
 * Copyright (C) 2006-2010 DLR, Fraunhofer SCAI, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.rce.communication;

import java.io.Serializable;
import java.text.MessageFormat;

import de.rcenvironment.commons.Assertions;

/**
 * Represents an immediate network contact, identified by host name, port, and the internal
 * "protocol" to use.
 * 
 * @author Heinrich Wendel
 * @author Doreen Seider
 * @author Robert Mischke (refactoring)
 */
public class NetworkContact implements Serializable {

    /**
     * Exception thrown if a parameter is null.
     */
    private static final String ERROR_PARAMETERS_NULL = "The parameter \"{0}\" must not be null.";

    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 6245227247948870320L;

    /**
     * The host to contact.
     */
    private final String myHost;

    /**
     * The protocol to use. The protocol is defined as java package name, e.g.
     * "de.rcenvironment.rce.communication.rmi".
     */
    private final String myProtocol;

    /**
     * The port to use.
     */
    private final Integer myPort;

    /**
     * Constructor.
     * 
     * @param host The host to contact.
     * @param protocol The protocol to use. The protocol is defined as java package name, e.g.
     *        "de.rcenvironment.rce.communication.rmi".
     * @param port The port to use.
     */
    public NetworkContact(String host, String protocol, Integer port) {

        Assertions.isDefined(host, MessageFormat.format(ERROR_PARAMETERS_NULL, "host"));
        Assertions.isDefined(protocol, MessageFormat.format(ERROR_PARAMETERS_NULL, "protocol"));
        Assertions.isDefined(port, MessageFormat.format(ERROR_PARAMETERS_NULL, "port"));

        myHost = host;
        myProtocol = protocol;
        myPort = port;
    }

    /**
     * Returns the name of the host.
     * 
     * @return The host name.
     */
    public String getHost() {
        return myHost;
    }

    /**
     * Returns the protocol.
     * 
     * TODO review: rename to "transport" for clarity? - misc_ro
     * 
     * @return The protocol.
     */
    public String getProtocol() {
        return myProtocol;
    }

    /**
     * Returns the port.
     * 
     * @return The port.
     */
    public Integer getPort() {
        return myPort;
    }

    @Override
    public boolean equals(Object object) {
        Assertions.isDefined(object, MessageFormat.format(ERROR_PARAMETERS_NULL, "object"));
        return toString().equals(object.toString());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        return myHost + ":" + myProtocol + ":" + myPort;
    }
}
