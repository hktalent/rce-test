/*
 * Copyright (C) 2006-2012 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */
package de.rcenvironment.core.communication.transport.virtual;

import de.rcenvironment.core.communication.testutils.TestConfiguration;
import de.rcenvironment.core.communication.testutils.templates.AbstractSwitchableVirtualInstanceTest;

/**
 * Concrete class for running the {@link AbstractSwitchableVirtualInstanceTest} test cases with a
 * non-duplex {@link VirtualNetworkTransportProvider}.
 * 
 * @author Robert Mischke
 */
public class VirtualTransportNonDuplexVirtualInstanceTest extends AbstractSwitchableVirtualInstanceTest {

    @Override
    protected TestConfiguration defineTestConfiguration() {
        return new VirtualTransportTestConfiguration(false);
    }
}
