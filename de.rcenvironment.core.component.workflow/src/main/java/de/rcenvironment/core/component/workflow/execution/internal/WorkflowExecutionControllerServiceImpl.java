/*
s * Copyright (C) 2006-2014 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.component.workflow.execution.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import de.rcenvironment.core.communication.management.WorkflowHostService;
import de.rcenvironment.core.component.execution.api.ExecutionConstants;
import de.rcenvironment.core.component.execution.api.WorkflowExecutionControllerCallback;
import de.rcenvironment.core.component.workflow.execution.api.WorkflowExecutionContext;
import de.rcenvironment.core.component.workflow.execution.api.WorkflowExecutionController;
import de.rcenvironment.core.component.workflow.execution.api.WorkflowExecutionControllerService;
import de.rcenvironment.core.component.workflow.execution.api.WorkflowExecutionException;
import de.rcenvironment.core.component.workflow.execution.api.WorkflowExecutionInformation;
import de.rcenvironment.core.component.workflow.execution.api.WorkflowState;
import de.rcenvironment.core.component.workflow.execution.impl.WorkflowExecutionInformationImpl;
import de.rcenvironment.core.utils.common.security.AllowRemoteAccess;

/**
 * Implementation of {@link WorkflowExecutionControllerService}.
 * 
 * @author Doreen Seider
 */
public class WorkflowExecutionControllerServiceImpl implements WorkflowExecutionControllerService {

    private BundleContext bundleContext;
    
    private WorkflowHostService workflowHostService;
    
    private Map<String, ServiceRegistration<?>> workflowServiceRegistrations = Collections.synchronizedMap(
        new HashMap<String, ServiceRegistration<?>>());
    
    private Map<String, WorkflowExecutionInformation> workflowExecutionInformations = Collections.synchronizedMap(
        new HashMap<String, WorkflowExecutionInformation>());

    protected void activate(BundleContext context) {
        bundleContext = context;
    }

    @Override
    @AllowRemoteAccess
    public WorkflowExecutionInformation createExecutionController(WorkflowExecutionContext wfExeCtx,
        Map<String, String> executionAuthTokens, Boolean calledFromRemote) throws WorkflowExecutionException {

        if (calledFromRemote && !workflowHostService.getWorkflowHostNodes().contains(wfExeCtx.getNodeId())) {
            throw new WorkflowExecutionException(String.format("Workflow execution request refused, as the requested instance is "
                + "not declared as workflow host: %s (%s)", wfExeCtx.getNodeId().getAssociatedDisplayName(),
                wfExeCtx.getNodeId().getIdString()));
        }
        WorkflowExecutionController workflowController = new WorkflowExecutionControllerImpl((WorkflowExecutionContext) wfExeCtx);
        workflowController.setComponentExecutionAuthTokens(executionAuthTokens);

        Dictionary<String, String> properties = new Hashtable<String, String>();
        properties.put(ExecutionConstants.EXECUTION_ID_OSGI_PROP_KEY, wfExeCtx.getExecutionIdentifier());

        ServiceRegistration<?> serviceRegistration = bundleContext.registerService(
            new String[] { WorkflowExecutionController.class.getName(), WorkflowExecutionControllerCallback.class.getName() },
            workflowController, properties);

        WorkflowExecutionInformationImpl workflowExecutionInformation = new WorkflowExecutionInformationImpl(wfExeCtx);
        workflowExecutionInformation.setIdentifier(wfExeCtx.getExecutionIdentifier());
        
        synchronized (workflowExecutionInformations) {
            workflowExecutionInformations.put(wfExeCtx.getExecutionIdentifier(), workflowExecutionInformation);
            workflowServiceRegistrations.put(wfExeCtx.getExecutionIdentifier(), serviceRegistration);
        }
        return workflowExecutionInformation;
    }

    @Override
    @AllowRemoteAccess
    public void performDispose(String executionId) {
        getWorkflowExecutionController(executionId).dispose();
        dispose(executionId);
    }
    
    private void dispose(String executionId) {
        synchronized (workflowExecutionInformations) {
            workflowExecutionInformations.remove(executionId);
            if (workflowServiceRegistrations.containsKey(executionId)) {
                workflowServiceRegistrations.get(executionId).unregister();
                workflowServiceRegistrations.remove(executionId);
            }
        }
    }
    
    @Override
    @AllowRemoteAccess
    public void performStart(String executionId) {
        getWorkflowExecutionController(executionId).start();
    }
    
    @Override
    @AllowRemoteAccess
    public void performCancel(String executionId) {
        getWorkflowExecutionController(executionId).cancel();
    }

    @Override
    @AllowRemoteAccess
    public void performPause(String executionId) {
        getWorkflowExecutionController(executionId).pause();
    }

    @Override
    @AllowRemoteAccess
    public void performResume(String executionId) {
        getWorkflowExecutionController(executionId).resume();
    }
    
    @Override
    @AllowRemoteAccess
    public WorkflowState getWorkflowState(String executionId) {
        return getWorkflowExecutionController(executionId).getState();
    }
    
    @Override
    @AllowRemoteAccess
    public Collection<WorkflowExecutionInformation> getWorkflowExecutionInformations() {
        return new HashSet<WorkflowExecutionInformation>(workflowExecutionInformations.values());
    }

    private WorkflowExecutionController getWorkflowExecutionController(String executionId) {

        String filter = createPropertyFilter(executionId);
        try {
            ServiceReference<?>[] serviceReferences = bundleContext.getServiceReferences(WorkflowExecutionController.class.getName(),
                filter);
            if (serviceReferences != null) {
                for (ServiceReference<?> ref : serviceReferences) {
                    return (WorkflowExecutionController) bundleContext.getService(ref);
                }
            }
        } catch (InvalidSyntaxException e) {
            // should not happen
            LogFactory.getLog(getClass()).error(String.format("Filter '%s' is not valid", filter));
        } catch (IllegalStateException e) {
            LogFactory.getLog(getClass()).warn("Bundle seems to be shutted down. "
                + "If this occurs at any time thant shut down, it is an error", e);
        }
        throw new RuntimeException(String.format("%s with id '%s' not registered as OSGi service",
            WorkflowExecutionController.class.getSimpleName(), executionId));
    }

    private String createPropertyFilter(String executionId) {
        return String.format("(%s=%s)", ExecutionConstants.EXECUTION_ID_OSGI_PROP_KEY, executionId);
    }

    protected void bindWorkflowHostService(WorkflowHostService newService) { 
        this.workflowHostService = newService;
    }
}
