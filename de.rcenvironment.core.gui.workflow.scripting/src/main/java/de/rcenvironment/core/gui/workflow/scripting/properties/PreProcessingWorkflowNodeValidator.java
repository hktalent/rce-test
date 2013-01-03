/*
 * Copyright (C) 2006-2011 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */
 
package de.rcenvironment.core.gui.workflow.scripting.properties;

import de.rcenvironment.commons.scripting.ScriptableComponentConstants.ScriptTime;

/**
 * {@link de.rcenvironment.rce.gui.workflow.editor.validator.WorkflowNodeValidator} for workflow nodes with preprocessing.
 *
 * @author Christian Weiss
 */
public class PreProcessingWorkflowNodeValidator extends AbstractProcessingWorkflowNodeValidator {

    public PreProcessingWorkflowNodeValidator() {
        super(ScriptTime.PRE);
    }

}
