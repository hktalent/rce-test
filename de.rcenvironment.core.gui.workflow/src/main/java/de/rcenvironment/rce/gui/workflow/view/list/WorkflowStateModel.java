/*
 * Copyright (C) 2006-2012 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.rce.gui.workflow.view.list;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import de.rcenvironment.rce.component.workflow.WorkflowState;

/**
 * A simple synchronized {@link Map} holding the last known {@link WorkflowState} for each workflow
 * by its id.
 * 
 * @author Robert Mischke
 */
public final class WorkflowStateModel {

    private static WorkflowStateModel instance = new WorkflowStateModel();

    private Map<String, WorkflowState> theMap = Collections.synchronizedMap(new HashMap<String, WorkflowState>());

    private WorkflowStateModel() {}

    public static WorkflowStateModel getInstance() {
        return instance;
    }

    /**
     * Fetches the last known state for a workflow.
     * 
     * @param key the id of the workflow
     * @return the last known state or null if none exists
     */
    public WorkflowState getState(String key) {
        return theMap.get(key);
    }

    /**
     * Sets the latest known state for a workflow.
     * 
     * @param string the id of the workflow
     * @param value the new last known state
     */
    public void setState(String string, WorkflowState value) {
        theMap.put(string, value);
    }

}
