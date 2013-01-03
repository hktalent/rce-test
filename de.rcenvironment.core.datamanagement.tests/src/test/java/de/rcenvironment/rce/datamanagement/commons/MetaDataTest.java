/*
 * Copyright (C) 2006-2010 DLR, Fraunhofer SCAI, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.rce.datamanagement.commons;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;


/**
 * Tests cases for {@link MetaData}.
 *
 * @author Juergen Klein
 * @author Doreen Seider
 */
public class MetaDataTest {

    private final String key = "testKey";

    private MetaData metaData;

    /** Set up. */
    @Before
    public void setUp() {
        metaData = new MetaData(key, true, false);
    }

    /** Test. */
    @Test
    public void test() {
        metaData = new MetaData(key, true, false);
        assertTrue(metaData.isRevisionIndependent());
        assertFalse(metaData.isReadOnly());
        assertFalse(new MetaData(key, false, false).isRevisionIndependent());
        assertTrue(new MetaData(key, false, true).isReadOnly());
    }

    /** Test. */
    @Test
    public void testIsRevisionIndependent() {
        assertTrue(metaData.isRevisionIndependent());
        assertFalse(new MetaData(key, false, false).isRevisionIndependent());
    }

    /** Test. */
    @Test
    public void testIsReadOnly() {
        assertFalse(metaData.isReadOnly());
        assertTrue(new MetaData(key, false, true).isReadOnly());
    }

    /** Test. */
    @Test
    public void testGetKey() {
        assertEquals(key, metaData.getKey());
    }

    /** Test. */
    @Test
    public void testEquals() {
        assertTrue(metaData.equals(metaData));
        assertTrue(metaData.equals(new MetaData(key, true, false)));
        assertFalse(metaData.equals(new Object()));
        assertFalse(metaData.equals(new MetaData(key, false, true)));
        assertFalse(metaData.equals(new MetaData(key, true, true)));
        assertFalse(metaData.equals(new MetaData(key, false, false)));
        assertFalse(metaData.equals(new MetaData("wuattt", true, false)));
        assertFalse(metaData.equals(null));
    }

    /** Test. */
    @Test
    public void testToString() {
        assertEquals(key, metaData.toString());
    }

    /** Test. */
    @Test
    public void testHashCode() {
        metaData.hashCode();
        new MetaData(key, false, true).hashCode();
    }
}
