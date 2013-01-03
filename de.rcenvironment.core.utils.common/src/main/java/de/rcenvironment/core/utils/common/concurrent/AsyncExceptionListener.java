/*
 * Copyright (C) 2006-2012 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.utils.common.concurrent;

/**
 * A callback interface for exceptions in asynchronous tasks.
 * 
 * @author Robert Mischke
 */
public interface AsyncExceptionListener {

    /**
     * Reports an exception that occured in an asynchronous task.
     * 
     * @param e the exception
     */
    void onAsyncException(Exception e);

}
