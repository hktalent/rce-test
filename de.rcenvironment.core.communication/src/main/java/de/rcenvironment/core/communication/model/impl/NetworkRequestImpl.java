/*
 * Copyright (C) 2006-2012 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.communication.model.impl;

import java.io.Serializable;
import java.util.Map;

import de.rcenvironment.commons.IdGenerator;
import de.rcenvironment.core.communication.model.NetworkRequest;
import de.rcenvironment.core.communication.model.internal.AbstractNetworkMessage;
import de.rcenvironment.core.communication.utils.SerializationException;

/**
 * Implementation of a transport-independent network request.
 * 
 * @author Robert Mischke
 */
public class NetworkRequestImpl extends AbstractNetworkMessage implements NetworkRequest, Serializable {

    // TODO made this class Serializable for quick prototyping; rework so this is not used anymore
    private static final long serialVersionUID = 1608492229624555125L;

    public NetworkRequestImpl(byte[] contentBytes, Map<String, String> metaData) {
        this(contentBytes, metaData, IdGenerator.randomUUID());
    }

    // TODO comment: parameters are wrapped, not cloned
    public NetworkRequestImpl(Serializable body, Map<String, String> metaData) throws SerializationException {
        this(body, metaData, IdGenerator.randomUUID());
    }

    // TODO comment: parameters are wrapped, not cloned
    public NetworkRequestImpl(byte[] contentBytes, Map<String, String> metaData, String requestId) {
        super(metaData);
        setContentBytes(contentBytes);
        setRequestId(requestId);
    }

    // TODO comment: parameters are wrapped, not cloned
    public NetworkRequestImpl(Serializable body, Map<String, String> metaData, String requestId) throws SerializationException {
        super(metaData);
        setContent(body);
        setRequestId(requestId);
    }
}
