/*
 * Copyright (C) 2006-2014 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.component.model.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.rcenvironment.core.component.api.ComponentConstants;
import de.rcenvironment.core.component.model.api.ComponentColor;
import de.rcenvironment.core.component.model.api.ComponentInterface;
import de.rcenvironment.core.component.model.api.ComponentShape;
import de.rcenvironment.core.component.model.api.ComponentSize;
import de.rcenvironment.core.component.model.configuration.api.ConfigurationDefinition;
import de.rcenvironment.core.component.model.configuration.api.ConfigurationExtensionDefinition;
import de.rcenvironment.core.component.model.configuration.impl.ConfigurationDefinitionImpl;
import de.rcenvironment.core.component.model.configuration.impl.ConfigurationExtensionDefinitionImpl;
import de.rcenvironment.core.component.model.endpoint.api.EndpointDefinitionsProvider;
import de.rcenvironment.core.component.model.endpoint.impl.EndpointDefinitionsProviderImpl;

/**
 * A writable {@link ComponentInterface} implementation.
 * 
 * @author Robert Mischke
 * @author Doreen Seider
 */
public class ComponentInterfaceImpl implements ComponentInterface, Serializable {

    private String displayName;

    private String groupName;

    private byte[] icon16;

    private byte[] icon24;

    private byte[] icon32;

    private ComponentSize size;

    private ComponentColor color;

    private ComponentShape shape;

    // Note: temporary field for migration; will probably change for 5.0
    private String identifier;

    // Note: temporary field for migration; will probably change for 5.0
    private String version;

    private List<String> identifiers;

    private EndpointDefinitionsProviderImpl inputDefinitionsProvider;

    private EndpointDefinitionsProviderImpl outputDefinitionsProvider;

    private ConfigurationDefinitionImpl configurationDefinition;

    private Set<ConfigurationExtensionDefinitionImpl> configurationExtensionDefinitions =
        new HashSet<ConfigurationExtensionDefinitionImpl>();

    private boolean localExecutionOnly = false;

    private boolean performLazyDisposal = false;

    private boolean isDeprecated = false;
    
    private boolean canHandleIndefiniteInputDataTypes = false;
    
    private boolean isResetSink = false;

    @Override
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public byte[] getIcon16() {
        return icon16;
    }

    public void setIcon16(byte[] icon16) {
        this.icon16 = icon16;
    }

    @Override
    public byte[] getIcon24() {
        return icon24;
    }

    public void setIcon24(byte[] icon24) {
        this.icon24 = icon24;
    }

    @Override
    public byte[] getIcon32() {
        return icon32;
    }

    public void setIcon32(byte[] icon32) {
        this.icon32 = icon32;
    }

    @Override
    public ComponentSize getSize() {
        return size;
    }

    public void setSize(ComponentSize size) {
        this.size = size;
    }

    @Override
    public ComponentColor getColor() {
        return color;
    }

    public void setColor(ComponentColor color) {
        this.color = color;
    }

    @Override
    public ComponentShape getShape() {
        return shape;
    }

    public void setShape(ComponentShape shape) {
        this.shape = shape;
    }

    @Override
    public String getIdentifier() {
        if (version != null && !identifier.endsWith(ComponentConstants.ID_SEPARATOR + version)) {
            return identifier + ComponentConstants.ID_SEPARATOR + version;
        }
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public List<String> getIdentifiers() {
        List<String> resultIdentifiers = new ArrayList<>();
        for (String id : identifiers) {
            if (version != null && !id.endsWith(ComponentConstants.ID_SEPARATOR + version)) {
                resultIdentifiers.add(id + ComponentConstants.ID_SEPARATOR + version);
            } else {
                resultIdentifiers.add(id);
            }
        }
        return resultIdentifiers;
    }

    public void setIdentifiers(List<String> identifiers) {
        this.identifiers = identifiers;
    }

    @Override
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public EndpointDefinitionsProvider getInputDefinitionsProvider() {
        return inputDefinitionsProvider;
    }

    public void setInputDefinitionsProvider(EndpointDefinitionsProviderImpl inputDefinitionsProvider) {
        this.inputDefinitionsProvider = inputDefinitionsProvider;
    }

    @Override
    public EndpointDefinitionsProvider getOutputDefinitionsProvider() {
        return outputDefinitionsProvider;
    }

    public void setOutputDefinitionsProvider(EndpointDefinitionsProviderImpl outputDefinitionsProvider) {
        this.outputDefinitionsProvider = outputDefinitionsProvider;
    }

    @Override
    public ConfigurationDefinition getConfigurationDefinition() {
        return configurationDefinition;
    }

    public void setConfigurationDefinition(ConfigurationDefinitionImpl configurationDefinition) {
        this.configurationDefinition = configurationDefinition;
    }

    @Override
    public Set<ConfigurationExtensionDefinition> getConfigurationExtensionDefinitions() {
        return new HashSet<ConfigurationExtensionDefinition>(configurationExtensionDefinitions);
    }

    public void setConfigurationExtensionDefinitions(Set<ConfigurationExtensionDefinitionImpl> configurationExtensionDefinitions) {
        this.configurationExtensionDefinitions = configurationExtensionDefinitions;
    }

    @Override
    public boolean getLocalExecutionOnly() {
        return localExecutionOnly;
    }

    public void setLocalExecutionOnly(boolean localExecutionOnly) {
        this.localExecutionOnly = localExecutionOnly;
    }

    @Override
    public boolean getPerformLazyDisposal() {
        return performLazyDisposal;
    }

    public void setPerformLazyDisposal(boolean performLazyDisposal) {
        this.performLazyDisposal = performLazyDisposal;
    }

    @Override
    public boolean getIsDeprecated() {
        return isDeprecated;
    }

    public void setIsDeprecated(boolean isDeprecated) {
        this.isDeprecated = isDeprecated;
    }
    
    @Override
    public boolean getCanHandleIndefiniteInputDataTypes() {
        return canHandleIndefiniteInputDataTypes;
    }
    
    public void setCanHandleIndefiniteInputDataTypes(boolean canHandleIndefiniteInputDataTypes) {
        this.canHandleIndefiniteInputDataTypes = canHandleIndefiniteInputDataTypes;
    }

    @Override
    public boolean getIsResetSink() {
        return isResetSink;
    }
    
    public void setIsResetSink(boolean isResetSink) {
        this.isResetSink = isResetSink;
    }

    @Override
    public String toString() {
        return String.format("ComponentInterface(id=%s)", getIdentifier());
    }

}
