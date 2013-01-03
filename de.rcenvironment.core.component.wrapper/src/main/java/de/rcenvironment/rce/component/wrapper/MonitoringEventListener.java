/*
 * Copyright (C) 2006-2011 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.rce.component.wrapper;

/**
 * Listener interface for raw stdout/stderr lines, and for lines of user information.
 * 
 * @author Robert Mischke
 * 
 */
public interface MonitoringEventListener {

    /**
     * Callback for received STDOUT lines.
     * 
     * @param line stdout line
     */
    void appendStdout(String line);

    /**
     * Callback for received STDERR lines.
     * 
     * @param line stderr line
     */
    void appendStderr(String line);

    /**
     * Callback for additional user information lines (validation, progress, ...).
     * 
     * @param line information line
     */
    void appendUserInformation(String line);
}
