/*
 * Copyright (C) 2006-2012 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.rce.communication.internal;

/**
 * Enumeration to decide between communication contacts and file transfer contacts.
 * 
 * @author Robert Mischke (extracted class out of CommunicationContactMap)
 */
public enum CommunicationType {

    /** CommunicationType for calling remote services. */
    SERVICE_CALL,

    /** CommunicationType for transferring remote files. */
    FILE_TRANSFER
}
