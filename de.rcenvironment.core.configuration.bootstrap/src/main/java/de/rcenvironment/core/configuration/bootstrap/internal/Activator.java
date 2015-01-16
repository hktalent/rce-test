/*
 * Copyright (C) 2006-2014 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */
package de.rcenvironment.core.configuration.bootstrap.internal;

import java.io.IOException;
import java.io.PrintStream;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import de.rcenvironment.core.configuration.bootstrap.BootstrapConfiguration;
import de.rcenvironment.core.configuration.bootstrap.LogArchiver;

/**
 * Bundle activator that triggers the {@link BootstrapConfiguration} initialization.
 * 
 * @author Robert Mischke
 */
public class Activator implements BundleActivator {

    @Override
    public void start(BundleContext arg0) {
        try {
            BootstrapConfiguration.initialize();
            LogArchiver.run(BootstrapConfiguration.getInstance().getProfileDirectory());
        } catch (IOException e) {
            // circumvent CheckStyle rule to print fatal errors before the log system is initialized
            PrintStream sysErr = System.err;
            sysErr.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    @Override
    public void stop(BundleContext arg0) {}
}
