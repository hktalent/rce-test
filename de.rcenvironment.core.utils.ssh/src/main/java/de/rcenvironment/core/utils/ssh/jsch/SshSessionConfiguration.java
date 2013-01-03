/*
 * Copyright (C) 2006-2012 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */
package de.rcenvironment.core.utils.ssh.jsch;

/**
 * Interface providing SSH configuration parameters.
 * 
 * @author Robert Mischke
 * 
 */
public interface SshSessionConfiguration {

    /**
     * @return the host name or IP to connect to
     */
    String getDestinationHost();

    /**
     * @return the port to connect to
     */
    int getPort();

    /**
     * @return the login name to use
     */
    String getSshAuthUser();

    /**
     * @return if a SSH key file location is given, the passphrase for this keyfile; otherwise, the
     *         login password to use
     */
    String getSshAuthPhrase();

    /**
     * @return the local filesystem path to a keyfile; set to null or an empty string to use
     *         password authentication
     */
    String getSshKeyFileLocation();

}
