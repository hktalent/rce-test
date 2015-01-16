/*
 * Copyright (C) 2006-2014 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */
package de.rcenvironment.core.gui.resources.internal;

import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import de.rcenvironment.core.gui.resources.api.ImageManager;

/**
 * Standard bundle activator.
 * 
 * @author Robert Mischke
 */
public class Activator implements BundleActivator {

    /**
     * Creates an {@link ImageManager} on startup.
     * 
     * @param bundleContext (not used)
     */
    public void start(BundleContext bundleContext) {
        synchronized (ImageManager.class) {
            ImageManager instance = ImageManager.getInstance();
            if (instance != null) {
                throw new IllegalStateException("Image manager already present");
            }
            ImageManager.setInstance(new ImageManagerImpl());
        }
    }

    /**
     * Disposes the previously created {@link ImageManager} on shutdown.
     * 
     * @param bundleContext (not used)
     */
    public void stop(BundleContext bundleContext) {
        synchronized (ImageManager.class) {
            ImageManager oldInstance = ImageManager.getInstance();
            if (oldInstance == null) {
                // this can happen if initialization failed, so don't throw another exception
                LogFactory.getLog(getClass()).warn("No image manager present on shutdown");
                return;
            }
            ImageManager.setInstance(null);
            ((ImageManagerImpl) oldInstance).dispose();
        }
    }

}
