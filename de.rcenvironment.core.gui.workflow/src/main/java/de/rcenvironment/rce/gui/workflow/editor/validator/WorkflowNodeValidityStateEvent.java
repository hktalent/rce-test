/*
 * Copyright (C) 2006-2011 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */
 
package de.rcenvironment.rce.gui.workflow.editor.validator;

import java.io.Serializable;

import de.rcenvironment.rce.component.workflow.WorkflowNode;

/**
 * An event informing about an event in the validity state determined by a
 * {@link WorkflowNodeValidator}.
 * 
 * @author Christian Weiss
 */
public class WorkflowNodeValidityStateEvent implements Serializable {

    private static final long serialVersionUID = 6012538869724067962L;

    private final WorkflowNode workflowNode;

    private final boolean valid;

    public WorkflowNodeValidityStateEvent(final WorkflowNode workflowNode, final boolean valid) {
        this.workflowNode = workflowNode;
        this.valid = valid;
    }

    public WorkflowNode getWorkflowNode() {
        return workflowNode;
    }

    public boolean isValid() {
        return valid;
    }

}
