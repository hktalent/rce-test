/*
 * Copyright (C) 2006-2010 DLR, Fraunhofer SCAI, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.rce.component;

import java.util.Dictionary;
import java.util.Hashtable;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;

/**
 * Test cases for {@link ComponentBundleListener}.
 *
 * @author Doreen Seider
 */
public class ComponentBundleListenerTest extends TestCase {

    /** Constant. */
    private static final String BUNDLE_NAME = "bundle";
    
    /** Class under test. */
    private ComponentBundleListener listener;
        
    @Override
    public void setUp() throws Exception {
        listener = new ComponentBundleListener();
    }
    
    /**
     * Test.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testBundleChanged() {
        
        // RCE-Component bundle is installed
        Bundle bundle = EasyMock.createNiceMock(Bundle.class);
        EasyMock.expect(bundle.getSymbolicName()).andReturn(BUNDLE_NAME);
        Dictionary dict = new Hashtable();
        dict.put(ComponentConstants.MANIFEST_ENTRY, Boolean.valueOf(true).toString());
        EasyMock.expect(bundle.getHeaders()).andReturn(dict);
        EasyMock.replay(bundle);
        
        listener.bundleChanged(new BundleEvent(BundleEvent.INSTALLED, bundle));
        
        // non-RCE-Component bundle is installed
        // strict mock in order to throw an exception if bundle.start() will be called, 
        // which would be incorrect
        bundle = EasyMock.createStrictMock(Bundle.class);
        dict.remove(ComponentConstants.MANIFEST_ENTRY);
        dict.put(ComponentConstants.MANIFEST_ENTRY, Boolean.valueOf(false).toString());
        EasyMock.expect(bundle.getHeaders()).andReturn(dict);
        EasyMock.replay(bundle);
        
        listener.bundleChanged(new BundleEvent(BundleEvent.INSTALLED, bundle));
        
        // non-RCE-Component bundle is installed
        // strict mock in order to throw an exception if bundle.start() will be called, 
        // which would be incorrect
        bundle = EasyMock.createStrictMock(Bundle.class);
        dict.remove(ComponentConstants.MANIFEST_ENTRY);
        EasyMock.expect(bundle.getHeaders()).andReturn(dict);
        EasyMock.replay(bundle);
        
        listener.bundleChanged(new BundleEvent(BundleEvent.INSTALLED, bundle));
        
        // RCE-Component bundle is started
        // strict mock in order to throw an exception if bundle.start() will be called, 
        // which would be incorrect
        bundle = EasyMock.createStrictMock(Bundle.class);
        dict.remove(ComponentConstants.MANIFEST_ENTRY);
        dict.put(ComponentConstants.MANIFEST_ENTRY, Boolean.valueOf(true).toString());
        EasyMock.expect(bundle.getHeaders()).andReturn(dict);
        EasyMock.replay(bundle);
        
        listener.bundleChanged(new BundleEvent(BundleEvent.STARTED, bundle));
        
    }
}
