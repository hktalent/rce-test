/*
 * Copyright (C) 2006-2012 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.communication.model.internal;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import de.rcenvironment.core.communication.model.NetworkMessage;
import de.rcenvironment.core.communication.utils.MessageUtils;
import de.rcenvironment.core.communication.utils.MetaDataWrapper;
import de.rcenvironment.core.communication.utils.SerializationException;

/**
 * Abstract base class for transport-independent network messages.
 * 
 * @author Robert Mischke
 */
public class AbstractNetworkMessage implements NetworkMessage {

    // TODO review
    private static final String METADATA_KEY_REQUEST_ID = "common.requestId";

    protected MetaDataWrapper metaDataWrapper;

    private Map<String, String> metaData;

    private byte[] contentBytes;

    // cached deserialization of contentBytes
    private Serializable deserializedContent;

    public AbstractNetworkMessage() {
        setMetaData(new HashMap<String, String>());
    }

    public AbstractNetworkMessage(Map<String, String> metaData) {
        // TODO review: the provided map is embedded/used, not cloned (for now)
        setMetaData(metaData);
    }

    @Override
    public byte[] getContentBytes() {
        return contentBytes;
    }

    public void setContentBytes(byte[] contentBytes) {
        this.contentBytes = contentBytes;
    }

    @Override
    public synchronized Serializable getDeserializedContent() throws SerializationException {
        if (contentBytes == null) {
            return null;
        }
        // lazy init / caching of deserialized form
        if (deserializedContent == null) {
            deserializedContent = MessageUtils.deserializeObject(contentBytes);
        }
        return deserializedContent;
    }

    /**
     * @param messageBody the message body to serialize and store
     * @throws SerializationException on serialization failure
     */
    public void setContent(Serializable messageBody) throws SerializationException {
        this.contentBytes = MessageUtils.serializeObject(messageBody);
    }

    @Override
    public Map<String, String> accessRawMetaData() {
        // note: direct access, not cloned
        return metaData;
    }

    /**
     * Replaces the internal metadata map with the provided one.
     * 
     * @param metaData the new map instance to use internally
     */
    public void setMetaData(Map<String, String> metaData) {
        this.metaData = metaData;
        this.metaDataWrapper = MetaDataWrapper.wrap(metaData);
    }

    /**
     * @return the associated request id; currently unused
     */
    public String getRequestId() {
        return metaDataWrapper.getValue(METADATA_KEY_REQUEST_ID);
    }

    /**
     * @param requestId an arbitrary id to associate with this request; currently unused
     */
    public void setRequestId(String requestId) {
        metaDataWrapper.setValue(METADATA_KEY_REQUEST_ID, requestId);
    }

}
