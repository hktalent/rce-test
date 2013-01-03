/*
 * Copyright (C) 2006-2012 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */
package de.rcenvironment.core.communication.transport.activemq;

import de.rcenvironment.core.communication.testutils.TestConfiguration;
import de.rcenvironment.core.communication.testutils.templates.AbstractSwitchableVirtualInstanceTest;

/**
 * ActiveMQ implementation of the duplex connection {@link VirtualInstance} tests.
 * 
 * @author Robert Mischke
 */
public class ActiveMQDuplexVirtualInstanceTest extends AbstractSwitchableVirtualInstanceTest {

    @Override
    protected TestConfiguration defineTestConfiguration() {
        // NOTE: as the ActiveMQ transport does not support duplex yet, these
        // tests are actually running in non-duplex mode for now
        return new ActiveMQTestConfiguration();
    }
}
