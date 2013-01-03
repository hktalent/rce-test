/*
 * Copyright (C) 2006-2012 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.communication.routing.internal;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import de.rcenvironment.core.communication.model.NodeIdentifier;

/**
 * Grouping of multiple LSAs in one batch.
 * 
 * @author Phillip Kroll
 */
public class LinkStateAdvertisementBatch extends HashMap<NodeIdentifier, LinkStateAdvertisement> implements Serializable {

    private static final long serialVersionUID = -4462813588655196069L;

    public LinkStateAdvertisementBatch() {}

    public LinkStateAdvertisementBatch(Map<? extends NodeIdentifier, ? extends LinkStateAdvertisement> m) {
        super(m);
    }

}
