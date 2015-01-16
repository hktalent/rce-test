/*
 * Copyright (C) 2006-2014 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.communication.model;

/**
 * An interface to access information that has been gathered about a node.
 * 
 * @author Robert Mischke
 */
public interface NodeInformation {

    /**
     * @return the display name associated with the node, or null if none is available/known
     */
    String getDisplayName();

    /**
     * @return whether this node is known to act as a "workflow host"; if this is unknown, "false"
     *         is returned
     */
    boolean isWorkflowHost();
}
