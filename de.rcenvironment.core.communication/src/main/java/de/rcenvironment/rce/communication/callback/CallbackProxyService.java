/*
 * Copyright (C) 2006-2010 DLR, Fraunhofer SCAI, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.rce.communication.callback;

/**
 * Service handling callbacks from one platform to another.
 * 
 * @author Doreen Seider
 */
public interface CallbackProxyService {

    /**
     * Adds a proxy so that callbacks on this object can be invoked.
     * 
     * @param callBackProxy {@link Object} to invoke callbacks on.
     */
    void addCallbackProxy(CallbackProxy callBackProxy);

    /**
     * Gets an already added proxy.
     * 
     * @param objectIdentifier The identifier of the proxied object.
     * @return the proxy or <code>null</code>, if there is none.
     */
    Object getCallbackProxy(String objectIdentifier);

    /**
     * Sets the time to live for a bunch of objects represented by its identifier.
     * 
     * @param objectIdentifier The object's identifier to set the TTL for.
     * @param ttl The TTL to set.
     */
    void setTTL(String objectIdentifier, Long ttl);

}
