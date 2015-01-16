/*
 * Copyright (C) 2006-2014 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.component.execution.api;

import de.rcenvironment.core.component.execution.internal.EndpointDatumProcessor;
import de.rcenvironment.core.component.model.endpoint.api.EndpointDatum;

/**
 * Component-specific {@link ExecutionController}.
 * 
 * @author Doreen Seider
 */
public interface ComponentExecutionController extends ExecutionController {

    /**
     * Prepares the component.
     */
    void prepare();

    /**
     * @return the {@link ComponentState}
     */
    ComponentState getState();
    
    /**
     * Calls if an {@link EndpointDatum} was received from a local {@link EndpointDatumProcessor}.
     * 
     * @param endpointDatum {@link EndpointDatum} received
     */
    void onEndpointDatumReceived(EndpointDatum endpointDatum);
    
    /**
     * Cancels the component.
     * @param timeoutMsec max time to wait
     * @throws InterruptedException if waiting for the component to get cancelled failed
     */
    void cancelSync(long timeoutMsec) throws InterruptedException;
    
    /**
     * @return <code>true</code> if sending heartbeats to workflow controller succeeded, <code>false</code> otherwise
     */
    boolean isWorkflowControllerReachable();
}
