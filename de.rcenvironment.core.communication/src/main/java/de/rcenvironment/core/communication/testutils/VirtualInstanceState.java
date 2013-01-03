/*
 * Copyright (C) 2006-2012 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.communication.testutils;

/**
 * Lifecycle enum for virtual node instances.
 * 
 * @author Robert Mischke
 */
public enum VirtualInstanceState {
    /**
     * Initial/created state.
     */
    INITIAL,
    /**
     * Transitional state towards RUNNING.
     */
    STARTING,
    /**
     * Stable state after startup and before receiving a shutdown command.
     */
    STARTED,
    /**
     * Transitional state towards STOPPED; shutting down.
     */
    STOPPING,
    /**
     * Transitional state to simulate a crashing instance; will proceed to STOPPED when finished.
     */
    SIMULATED_CRASHING,
    /**
     * Stable state after shutdown has finished.
     */
    STOPPED;
}
