/*
 * Copyright (C) 2006-2010 DLR, Fraunhofer SCAI, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.rce.gui.workflow.editor;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.actions.ActionFactory;

import de.rcenvironment.rce.component.workflow.WorkflowNode;
import de.rcenvironment.rce.gui.workflow.parts.WorkflowNodePart;

/**
 * ContextMenu for the Workflow editor.
 * 
 * @author Heinrich Wendel
 */
class WorkflowEditorContextMenuProvider extends ContextMenuProvider {
    
    public static final String GROUP_NODE_ACTIONS = "de.rcenvironment.rce.gui.workflow.editor.nodeActions";

    /** The editor's action registry. */
    private final ActionRegistry actionRegistry;
    
    private EditPartViewer viewer;
    
    /**
     * Instantiate a new menu context provider for the specified EditPartViewer and ActionRegistry.
     * @param cs The Command Stack of the editor.
     * @param viewer the editor's graphical viewer
     * @param actionRegistry the editor's action registry
     */
    public WorkflowEditorContextMenuProvider(EditPartViewer viewer, ActionRegistry actionRegistry) {
        super(viewer);
        this.viewer = viewer;
        this.actionRegistry = actionRegistry;
    }
    
    @Override
    public void buildContextMenu(IMenuManager menu) {
        
        if (viewer.getSelectedEditParts().size() > 0) {
            addContributedContextMenuActions(menu);            
        }
        
        // Add standard action groups to the menu
        GEFActionConstants.addStandardActionGroups(menu);
        
        // Add actions to the menu
        menu.appendToGroup(
            GEFActionConstants.GROUP_UNDO,
            actionRegistry.getAction(ActionFactory.UNDO.getId()));
        menu.appendToGroup(
            GEFActionConstants.GROUP_UNDO,
            actionRegistry.getAction(ActionFactory.REDO.getId()));
    }

    private void addContributedContextMenuActions(IMenuManager menu) {

        menu.add(new Separator(GROUP_NODE_ACTIONS));

        // add node actions contributed via extensions
        IConfigurationElement[] confElements = Platform.getExtensionRegistry()
            .getConfigurationElementsFor("de.rcenvironment.rce.gui.workflow.editorActions"); //$NON-NLS-1$

        for (final IConfigurationElement confElement : confElements) {

            WorkflowNode node = (WorkflowNode) ((WorkflowNodePart) viewer.getSelectedEditParts().get(0)).getModel();

            if (node.getComponentDescription().getIdentifier().matches(confElement.getAttribute("component"))) { //$NON-NLS-1$

                final WorkflowEditorAction action;
                try {
                    Object actionObject = (WorkflowEditorAction) confElement.createExecutableExtension("class");
                    if (!(actionObject instanceof WorkflowEditorAction)) {
                        throw new RuntimeException(String.format(
                            "Class in attribute 'class' is not a subtype of '%s'.",
                            WorkflowEditorAction.class.getName()));
                    }
                    action = (WorkflowEditorAction) actionObject;
                } catch (CoreException e) {
                    throw new RuntimeException(e);
                }
                action.setWorkflowNode(node);    
                menu.appendToGroup(GROUP_NODE_ACTIONS, new Action() {

                    @Override
                    public String getText() {
                        return confElement.getAttribute("label");
                    }

                    @Override
                    public void run() {
                        action.performAction();
                    }

                    @Override
                    public boolean isEnabled() {
                        @SuppressWarnings("rawtypes") List selection = ((GraphicalViewer) viewer).getSelectedEditParts();
                        return selection.size() == 1 && selection.get(0).getClass() == WorkflowNodePart.class;
                    }
                });
                break;
            }
        }
    }
}
