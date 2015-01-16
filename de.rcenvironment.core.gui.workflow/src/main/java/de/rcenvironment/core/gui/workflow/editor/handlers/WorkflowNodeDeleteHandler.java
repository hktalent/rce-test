/*
 * Copyright (C) 2006-2014 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.gui.workflow.editor.handlers;

import java.util.ArrayList;
import java.util.List;

import de.rcenvironment.core.component.workflow.model.api.WorkflowDescription;
import de.rcenvironment.core.component.workflow.model.api.WorkflowLabel;
import de.rcenvironment.core.component.workflow.model.api.WorkflowNode;
import de.rcenvironment.core.gui.workflow.editor.commands.WorkflowLabelDeleteCommand;
import de.rcenvironment.core.gui.workflow.editor.commands.WorkflowNodeDeleteCommand;
import de.rcenvironment.core.gui.workflow.parts.WorkflowLabelPart;
import de.rcenvironment.core.gui.workflow.parts.WorkflowNodePart;

/**
 * Deletes a workflow node.
 * 
 * @author Doreen Seider
 */
public class WorkflowNodeDeleteHandler extends AbstractWorkflowNodeEditHandler {

    @Override
    void edit() {
        @SuppressWarnings("rawtypes") List selections = viewer.getSelectedEditParts();
        List<WorkflowNode> wfNodes = new ArrayList<WorkflowNode>();
        List<WorkflowLabel> wfLabels = new ArrayList<WorkflowLabel>();
        for (Object element : selections) {
            if (element instanceof WorkflowNodePart) {
                WorkflowNodePart part = (WorkflowNodePart) element;
                wfNodes.add((WorkflowNode) part.getModel());
            }
            if (element instanceof WorkflowLabelPart) {
                WorkflowLabelPart part = (WorkflowLabelPart) element;
                wfLabels.add((WorkflowLabel) part.getModel());
            }
        }

        if (!wfNodes.isEmpty()) {
            commandStack.execute(new WorkflowNodeDeleteCommand((WorkflowDescription) viewer.getContents().getModel(), wfNodes));
        }
        if (!wfLabels.isEmpty()) {
            commandStack.execute(new WorkflowLabelDeleteCommand((WorkflowDescription) viewer.getContents().getModel(), wfLabels));
        }
    }
}
