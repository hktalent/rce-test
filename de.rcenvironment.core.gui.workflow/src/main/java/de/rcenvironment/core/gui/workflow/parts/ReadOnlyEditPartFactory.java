/*
 * Copyright (C) 2006-2014 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.gui.workflow.parts;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import de.rcenvironment.core.component.workflow.execution.api.WorkflowExecutionInformation;
import de.rcenvironment.core.component.workflow.model.api.WorkflowDescription;
import de.rcenvironment.core.component.workflow.model.api.WorkflowLabel;
import de.rcenvironment.core.component.workflow.model.api.WorkflowNode;

/**
 * Factory responsible for creating the EditParts.
 * 
 * @author Heinrich Wendel
 */
public class ReadOnlyEditPartFactory implements EditPartFactory {

    @Override
    public EditPart createEditPart(EditPart context, Object model) {
        EditPart part = null;
        if (model instanceof WorkflowDescription) {
            part = new ReadOnlyWorkflowPart();
        } else if (model instanceof WorkflowNode) {
            part = new ReadOnlyWorkflowNodePart();
        } else if (model instanceof ConnectionWrapper) {
            part = new ConnectionPart();
        } else if (model instanceof WorkflowExecutionInformation) {
            part = new WorkflowExecutionInformationPart();
        } else if (model instanceof WorkflowLabel) {
            part = new WorkflowLabelPart();
        }
        part.setModel(model);
        return part;
    }

}
