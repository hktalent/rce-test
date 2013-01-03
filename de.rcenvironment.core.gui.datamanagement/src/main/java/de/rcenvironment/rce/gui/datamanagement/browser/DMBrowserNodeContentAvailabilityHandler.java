/*
 * Copyright (C) 2006-2010 DLR, Fraunhofer SCAI, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.rce.gui.datamanagement.browser;

import java.util.EventListener;

import de.rcenvironment.rce.gui.datamanagement.browser.spi.DMBrowserNode;

/**
 * A handler interface to get notified about the availability of the content of
 * {@link DMBrowserNode}s.
 *
 * @author Christian Weiss
 *
 */
public interface DMBrowserNodeContentAvailabilityHandler extends EventListener {

    /**
     * Called as soon as the content of the given {@link DMBrowserNode} is
     * available.
     *
     * @param node
     *            the {@link DMBrowserNode}
     */
    void handleContentAvailable(DMBrowserNode node);

    /**
     * Called if the content retrieval resulted in an error.
     * 
     * @param node the {@link DMBrowserNode}
     * @param cause the causing {@link Exception}
     */
    void handleContentRetrievalError(DMBrowserNode node, Exception cause);

}
