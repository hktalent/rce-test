/*
 * Copyright (C) 2006-2011 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.rce.component.wrapper.impl;

import de.rcenvironment.rce.component.wrapper.sandboxed.ExecutionEnvironment;
import de.rcenvironment.rce.component.wrapper.sandboxed.SandboxBehaviour;
import de.rcenvironment.rce.component.wrapper.sandboxed.WrapperConfigurationFactory;

/**
 * A simple {@link WrapperConfigurationFactory} that defines local execution and a single sandbox
 * for all repeated tool invocations (for the lifetime of the configured wrapper).
 * 
 * @author Robert Mischke
 * 
 */
public class DefaultWrapperConfigurationFactory implements WrapperConfigurationFactory {

    @Override
    public ExecutionEnvironment createExecutionEnvironment() {
        return new LocalExecutionEnvironment();
    }

    @Override
    public SandboxBehaviour createSandboxBehaviour(ExecutionEnvironment executionEnvironment) {
        return new ContinuousReuseSandboxBehaviour(executionEnvironment);
    }

}
