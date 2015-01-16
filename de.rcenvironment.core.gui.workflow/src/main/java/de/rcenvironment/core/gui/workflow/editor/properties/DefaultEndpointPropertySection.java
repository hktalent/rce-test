/*
 * Copyright (C) 2006-2014 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.gui.workflow.editor.properties;

import de.rcenvironment.core.datamodel.api.EndpointType;

/**
 * An extended "Properties" view tab for configuring endpoints (ie inputs and outputs) and using
 * initial Variables.
 * 
 * @author Sascha Zur
 */
public class DefaultEndpointPropertySection extends EndpointPropertySection {

    public DefaultEndpointPropertySection() {
        super();
        EndpointSelectionPane inputPane = new EndpointSelectionPane(Messages.inputs,
            EndpointType.INPUT, this, false, "default", false);

        EndpointSelectionPane outputPane = new EndpointSelectionPane(Messages.outputs,
            EndpointType.OUTPUT, this, false, "default", false);
        setColumns(2);
        setPanes(inputPane, outputPane);
    }
}
