/*
 * Copyright (C) 2006-2011 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.rce.eventlog.internal;

/**
 * The top-level context of {@link EventLogMessage}s and {@link EventLogger}s. See the JavaDoc of
 * enum fields for details.
 * 
 * @author Robert Mischke
 * 
 */
public enum EventLogContext {
    /**
     * The global RCE platform context. This marks events that are not related to a specific
     * workflow.
     */
    PLATFORM,

    /**
     * The workflow context. This marks events and loggers that relate to a specific workflow run;
     * events related to static workflow aspects (for example, workflow definition files) should use
     * the {@link #PLATFORM} context instead.
     */
    WORKFLOW,

    /**
     * The component context. This marks events and loggers that relate to a component of a specific
     * workflow run; events related to components outside a running workflow (for example, the
     * component installations in a platform) should use the {@link #PLATFORM} context instead.
     */
    COMPONENT;
}
