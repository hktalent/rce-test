/*
 * Copyright (C) 2006-2014 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */
 
package de.rcenvironment.core.component.model.endpoint.impl;

import java.io.Serializable;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;

import de.rcenvironment.core.component.model.endpoint.api.EndpointDefinitionConstants;
import de.rcenvironment.core.component.model.endpoint.api.EndpointGroupDefinition;

/**
 * Implementation of {@link EndpointGroupDefinition}.
 * 
 * @author Doreen Seider
 */
public class EndpointGroupDefinitionImpl implements Serializable, EndpointGroupDefinition {

    private static final long serialVersionUID = -6777818685549261071L;

    private static final String KEY_TYPE = "type";
    
    private Map<String, Object> rawEndpointGroupDefinition;
    
    @JsonIgnore
    @Override
    public String getIdentifier() {
        return (String) rawEndpointGroupDefinition.get(EndpointDefinitionConstants.KEY_IDENTIFIER);
    }
    
    @JsonIgnore
    @Override
    public Type getType() {
        return Type.valueOf((String) rawEndpointGroupDefinition.get(KEY_TYPE));
    }

    @JsonIgnore
    @Override
    public String getGroupName() {
        return (String) rawEndpointGroupDefinition.get(EndpointDefinitionConstants.KEY_GROUP);
    }
    
    public void setRawEndpointGroupDefinition(Map<String, Object> rawEndpointDefinition) {
        this.rawEndpointGroupDefinition = rawEndpointDefinition;
    }
    
    public Map<String, Object> getRawEndpointGroupDefinition() {
        return rawEndpointGroupDefinition;
    }
    
}
