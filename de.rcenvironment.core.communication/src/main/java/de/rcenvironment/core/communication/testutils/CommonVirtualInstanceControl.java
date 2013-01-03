/*
 * Copyright (C) 2006-2012 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.communication.testutils;

import de.rcenvironment.core.communication.connection.NetworkConnectionListener;
import de.rcenvironment.core.communication.connection.NetworkTrafficListener;
import de.rcenvironment.core.communication.model.NetworkContactPoint;
import de.rcenvironment.core.communication.transport.spi.NetworkTransportProvider;

/**
 * Interface for operations on virtual instances/nodes that can be used as batch operations as well,
 * in the sense that a single method call can affect any number of nodes.
 * 
 * @see VirtualInstance
 * @see VirtualInstanceGroup
 * 
 * @author Robert Mischke
 */
public interface CommonVirtualInstanceControl {

    /**
     * Sets the given target state for all nodes. Setting a target state causes the receiving
     * instance to modify its state so that it will eventually (and usually, asynchronously) reach
     * the given state, unless this is prevented by an internal error.
     * 
     * In the current {@link VirtualInstance} and {@link VirtualInstanceGroup} implementations,
     * calling this method with "STARTED" may cause receivers to enter the "STARTING" state, and
     * calling it with "STOPPED" may cause them to enter the "STOPPING" state.
     * 
     * @param state the target that should be reached
     * @throws InterruptedException on interruption while waiting for a required lock/monitor
     * 
     */
    void setTargetState(VirtualInstanceState state) throws InterruptedException;

    /**
     * Waits until all nodes have reached a "stable" state, in the sense that they will not change
     * their state without further method calls.
     * 
     * In the current {@link VirtualInstance} and {@link VirtualInstanceGroup} implementations, the
     * INITIAL, STARTED and STOPPED states are considered stable; STARTING and STOPPING are
     * considered unstable.
     * 
     * @throws InterruptedException on interruption while waiting for the target state
     */
    void waitForStateChangesToFinish() throws InterruptedException;

    /**
     * Convenience method; equivalent to "setTargetState(STARTED); waitForStateChangesToFinish()".
     * @throws InterruptedException on interruption while waiting for the target state
     */
    void start() throws InterruptedException;

    /**
     * Convenience method; equivalent to
     * "setTargetState(SIMULATED_CRASHING); waitForStateChangesToFinish()".
     * @throws InterruptedException on interruption while waiting for the target state
     */
    void simulateCrash() throws InterruptedException;

    /**
     * Convenience method; equivalent to "setTargetState(STOPPED); waitForStateChangesToFinish()".
     * @throws InterruptedException on interruption while waiting for the target state
     */
    void shutDown() throws InterruptedException;

    /**
     * Registers the given network transport for all nodes.
     * 
     * @param provider the new network transport provider
     */
    void registerNetworkTransportProvider(NetworkTransportProvider provider);

    /**
     * Registers a {@link NetworkConnectionListener}.
     * 
     * @param listener the new listener
     */
    void addNetworkConnectionListener(NetworkConnectionListener listener);

    /**
     * Registers a {@link NetworkTrafficListener}.
     * 
     * @param listener the new listener
     */
    void addNetworkTrafficListener(NetworkTrafficListener listener);

    /**
     * Registers the given network contact point as an initial peer for all nodes. Initial peers are
     * contacted on startup, and possibly later during refresh operations.
     * 
     * @param contactPoint the contact point to register
     */
    void addInitialNetworkPeer(NetworkContactPoint contactPoint);

}
