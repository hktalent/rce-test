/*
 * Copyright (C) 2006-2014 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.gui.workflow.view.properties;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import de.rcenvironment.core.communication.api.SimpleCommunicationService;
import de.rcenvironment.core.component.execution.api.ComponentExecutionInformation;
import de.rcenvironment.core.component.workflow.execution.api.WorkflowExecutionInformation;
import de.rcenvironment.core.gui.workflow.view.Messages;

/**
 * Class that maps information about a running component onto the IPropertySource interface.
 * 
 * @author Doreen Seider
 */
public class ComponentInstancePropertySource extends WorkflowInstancePropertySource {

    private static final String PROP_KEY_PLATFORM = "de.rcenvironment.rce.gui.workflow.view.properties.platform";

    private String wfNodeId;
    
    public ComponentInstancePropertySource(WorkflowExecutionInformation wfExeInfo, String wfNodeId) {
        super(wfExeInfo);
        this.wfNodeId = wfNodeId;
    }
    
    @Override
    public IPropertyDescriptor[] getPropertyDescriptors() {
        IPropertyDescriptor[] descriptors = new IPropertyDescriptor[4];

        descriptors[0] = new TextPropertyDescriptor(PROP_KEY_NAME, Messages.name);
        descriptors[1] = new TextPropertyDescriptor(PROP_KEY_STARTTIME, Messages.starttime);
        descriptors[2] = new TextPropertyDescriptor(PROP_KEY_PLATFORM, Messages.platform);
        descriptors[3] = new TextPropertyDescriptor(PROP_KEY_WORKKLOWPLATFORM, Messages.controllerPlatform);

        return descriptors;
    }

    @Override
    public Object getPropertyValue(Object key) {
        
        ComponentExecutionInformation compInstDescr = wfExeInfo.getComponentExecutionInformation(wfNodeId);
        if (compInstDescr == null) {
            return de.rcenvironment.core.gui.workflow.view.properties.Messages.componentInstanceUnknown;
        }
        Object value = null;
        if (key.equals(PROP_KEY_PLATFORM)) {
            if (compInstDescr.getNodeId() == null || new SimpleCommunicationService().isLocalNode(compInstDescr.getNodeId())) {
                value = Messages.local;
            } else {
                value = compInstDescr.getNodeId().getAssociatedDisplayName();
            }
        } else if (key.equals(PROP_KEY_NAME)) {
            value = compInstDescr.getInstanceName();
        } else {
            value = super.getPropertyValue(key);
        }
        return value;
    }

}
