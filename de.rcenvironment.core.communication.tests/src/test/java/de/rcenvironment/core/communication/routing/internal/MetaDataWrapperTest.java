/*
 * Copyright (C) 2006-2012 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */
package de.rcenvironment.core.communication.routing.internal;

import java.util.Map;

import junit.framework.TestCase;
import de.rcenvironment.core.communication.utils.MetaDataWrapper;

/**
 * Unit tests for {@link MetaDataWrapper}.
 * 
 * @author Phillip Kroll
 * 
 */
public class MetaDataWrapperTest extends TestCase {

    /**
     * Simple testcase.
     */
    public final void testCreate() {
        MetaDataWrapper data1 = new MetaDataWrapper();
        MetaDataWrapper data2 = new MetaDataWrapper();

        assertTrue(data1.matches(data2.getInnerMap()));
        assertTrue(data1.matches(data2));

        data1.setCategoryRouting();

        assertFalse(data1.matches(data2));
        assertTrue(data2.matches(data1));

        data2.setCategoryRouting();

        assertTrue(data1.matches(data2));
        assertTrue(data2.matches(data1));

        data2.setTopicLsa();

        assertTrue(data1.matches(data2));
        assertFalse(data2.matches(data1));

        data1.setTopicRouted();

        assertFalse(data1.matches(data2));
        assertFalse(data2.matches(data1));

        data1.setTopicLsa();

        assertTrue(data1.matches(data2));
        assertTrue(data2.matches(data1));

    }

    /**
     * Tests {@link MetaDataWrapper#getHopCount() and MetaDataWrapper#incHopCount().
     */
    public final void testHopCount() {

        Map<String, String> metaData = MetaDataWrapper.createEmpty().getInnerMap();
        assertEquals(0, MetaDataWrapper.wrap(metaData).getHopCount());
        MetaDataWrapper.wrap(metaData).incHopCount();
        assertEquals(1, MetaDataWrapper.wrap(metaData).getHopCount());

    }

}
