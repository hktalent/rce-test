/*
 * Copyright (C) 2006-2014 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.gui.communication.views.model;

import java.util.Collection;
import java.util.Map;

import de.rcenvironment.core.communication.common.NetworkGraph;
import de.rcenvironment.core.communication.common.NetworkGraphWithProperties;
import de.rcenvironment.core.communication.common.NodeIdentifier;
import de.rcenvironment.core.communication.connection.api.ConnectionSetup;
import de.rcenvironment.core.component.api.DistributedComponentKnowledge;
import de.rcenvironment.core.gui.communication.views.NetworkView;

/**
 * The complete model that the {@link NetworkView} is filled from.
 * 
 * @author Robert Mischke
 */
public class NetworkViewModel {

    /**
     * The reachable network graph, with no attached properties.
     */
    public NetworkGraph networkGraph;

    /**
     * The latest {@link DistributedComponentKnowledge} object.
     */
    public DistributedComponentKnowledge componentKnowledge;

    /**
     * The collection of connection setups.
     */
    public Collection<ConnectionSetup> connectionSetups;

    /**
     * The collected node's property maps; the inner maps must be immutable.
     */
    public Map<NodeIdentifier, Map<String, String>> nodeProperties;

    /**
     * The merged {@link NetworkGraphWithProperties}, constructed from {@link #networkGraph} and {@link #nodeProperties}.
     */
    public NetworkGraphWithProperties networkGraphWithProperties;

    public NetworkViewModel(NetworkGraph networkGraph, DistributedComponentKnowledge componentKnowledge,
        Collection<ConnectionSetup> connectionSetups, Map<NodeIdentifier, Map<String, String>> nodeProperties) {
        this.networkGraph = networkGraph;
        this.nodeProperties = nodeProperties;
        this.componentKnowledge = componentKnowledge;
        this.connectionSetups = connectionSetups;
        updateGraphWithProperties();
    }

    public NetworkGraph getNetworkGraphWithProperties() {
        return networkGraphWithProperties;
    }

    public DistributedComponentKnowledge getComponentKnowledge() {
        return componentKnowledge;
    }

    public Collection<ConnectionSetup> getConnectionSetups() {
        return connectionSetups;
    }

    public Map<NodeIdentifier, Map<String, String>> getNodeProperties() {
        return nodeProperties;
    }

    /**
     * Updates the merged {@link NetworkGraphWithProperties} from {@link #networkGraph} and {@link #nodeProperties}.
     */
    public void updateGraphWithProperties() {
        if (networkGraph != null) {
            networkGraphWithProperties = networkGraph.attachNodeProperties(nodeProperties);
        } else {
            networkGraphWithProperties = null;
        }
    }

}
