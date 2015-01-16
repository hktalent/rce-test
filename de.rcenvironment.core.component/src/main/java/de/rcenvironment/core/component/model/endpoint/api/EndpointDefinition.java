/*
 * Copyright (C) 2006-2014 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.component.model.endpoint.api;

import java.util.List;

import de.rcenvironment.core.datamodel.api.DataType;
import de.rcenvironment.core.datamodel.api.EndpointType;

/**
 * Represents one endpoint of a component.
 * 
 * @author Doreen Seider
 */
public interface EndpointDefinition {

    /**
     * Attributes an input can have.
     * 
     * @author Doreen Seider
     */
    enum InputDatumHandling {
        
        /** The input value will be consumed by the component. Input values can be queued. */
        Queue("Queue (consumed)"),
        
        /** The input value will be consumed by the component. Input values must not be queued. */
        Single("Single (consumed)"),
        
        /**
         * The input value won't be consumed by the component. Values must not be sent multiple times within one inner loop/workflow run.
         * This value is available for the component during the entire inner loop/workflow run. If a another value is sent, it is an
         * error and the workflow will fail.
         */
        Constant("Constant (not consumed)");
        
        private String displayName;
        
        private InputDatumHandling(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    /**
     * Attributes an input can have.
     * 
     * @author Doreen Seider
     */
    enum InputExecutionContraint {
        
        /**
         * The input is required, no matter if it is connected to an output or not.
         */
        Required("Required"),
        
        /**
         * The input is considered as required if it is connected to an output.
         */
        RequiredIfConnected("Required if connected"),
        
        /**
         * It is not required If input value is available, the value will be provided to the component othwise not. The component will run
         * in either way.
         */
        NotRequired("Not required"),
        
        /**
         * There is not execution constraint as it is delegated to the parent group. Thus, it is only valid if input/input group belongs to
         * an(other) input group.
         */
        None("-");
        
        private String displayName;
        
        private InputExecutionContraint(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    /**
     * @return endpoint name or <code>null</code> if it is a dynamic endpoint
     */
    String getName();

    /**
     * @return endpoint identifier or <code>null</code> if it is a static endpoint
     */
    String getIdentifier();

    /**
     * @return <code>true</code> if it is a static endpoint, <code>false</code> otherwise
     */
    boolean isStatic();

    /**
     * @return <code>true</code> if endpoint is not modifiable, <code>false</code> otherwise
     */
    boolean isReadOnly();

    /**
     * @return <code>true</code> if endpoint name is not modifiable, <code>false</code> otherwise
     */
    boolean isNameReadOnly();

    /**
     * @return {@link DataTypes}s which are valid for this endpoint
     */
    List<DataType> getPossibleDataTypes();

    /**
     * @return default {@link TypedDatum} of this endpoint
     */
    DataType getDefaultDataType();
    
    /**
     * @return {@link InputDatumHandling}s which are valid for this endpoint. Is empty for outputs.
     */
    List<InputDatumHandling> getInputDatumOptions();

    /**
     * @return default {@link InputDatumHandling} of this endpoint. Is <code>null</code> for outputs.
     */
    InputDatumHandling getDefaultInputDatumHandling();
    
    /**
     * @return {@link InputExecutionContraint}s which are valid for this endpoint. Is empty for outputs.
     */
    List<InputExecutionContraint> getInputExecutionConstraintOptions();

    /**
     * @return default {@link InputExecutionContraint} of this endpoint. Is <code>null</code> for outputs.
     */
    InputExecutionContraint getDefaultInputExecutionConstraint();

    /**
     * @return {@link EndpointType#INPUT} if is an input, {@link EndpointType#OUTPUT} if is an output
     */
    EndpointType getEndpointType();

    /**
     * @return meta data description of this endpoint
     */
    EndpointMetaDataDefinition getMetaDataDefinition();
    
    /**
     * @return definitions of initial dynamic endpoints (will be used if no one was specified during configuration)
     */
    List<InitialDynamicEndpointDefinition> getInitialDynamicEndpointDefinitions();
    
    /**
     * @return name of the {@link EndpointGroupDefinitionImpl} the endpoint belongs to or <code>null</code> if it belongs to none
     */
    String getGroupName();

}
