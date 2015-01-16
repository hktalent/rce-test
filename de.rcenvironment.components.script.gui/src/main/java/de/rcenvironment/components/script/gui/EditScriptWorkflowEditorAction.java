/*
 * Copyright (C) 2006-2014 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */
package de.rcenvironment.components.script.gui;

import org.eclipse.gef.commands.Command;

import de.rcenvironment.core.component.executor.SshExecutorConstants;
import de.rcenvironment.core.gui.workflow.editor.WorkflowEditorAction;
import de.rcenvironment.core.gui.workflow.executor.properties.AbstractEditScriptRunnable;

/**
 * {@link WorkflowEditorAction} used to open or edit the underlying script.
 * 
 * @author Doreen Seider
 */
public class EditScriptWorkflowEditorAction extends WorkflowEditorAction {

    @Override
    public void run() {
        new EditScriptRunnable().run();
    }

    /**
     * Implementation of {@link AbstractEditScriptRunnable}.
     * 
     * @author Doreen Seider
     */
    private class EditScriptRunnable extends AbstractEditScriptRunnable {

        protected void setScript(String script) {
            commandStack.execute(new EditScriptCommand(script));
        }

        protected String getScript() {
            return (String) workflowNode.getConfigurationDescription()
                .getConfigurationValue(SshExecutorConstants.CONFIG_KEY_SCRIPT);
        }

        @Override
        protected String getScriptName() {
            return Messages.scriptname;
        }
    }

    /**
     * Command to edit the underlying script.
     * 
     * @author Doreen Seider
     */
    private class EditScriptCommand extends Command {

        private String newScript;

        private String oldScript;

        protected EditScriptCommand(String newScript) {
            oldScript =
                (String) workflowNode.getConfigurationDescription()
                    .getConfigurationValue(SshExecutorConstants.CONFIG_KEY_SCRIPT);
            this.newScript = newScript;
        }

        @Override
        public void execute() {
            workflowNode.getConfigurationDescription()
                .setConfigurationValue(SshExecutorConstants.CONFIG_KEY_SCRIPT, newScript);
        }

        public void undo() {
            workflowNode.getConfigurationDescription()
                .setConfigurationValue(SshExecutorConstants.CONFIG_KEY_SCRIPT, oldScript);
        }

        public void redo() {
            execute();
        }
    }

}
