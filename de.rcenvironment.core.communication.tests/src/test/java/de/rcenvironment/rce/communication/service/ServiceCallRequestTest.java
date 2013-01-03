/*
 * Copyright (C) 2006-2010 DLR, Fraunhofer SCAI, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.rce.communication.service;

import static de.rcenvironment.rce.communication.CommunicationTestHelper.LOCAL_PLATFORM;
import static de.rcenvironment.rce.communication.CommunicationTestHelper.METHOD;
import static de.rcenvironment.rce.communication.CommunicationTestHelper.PARAMETER_LIST;
import static de.rcenvironment.rce.communication.CommunicationTestHelper.REMOTE_PLATFORM;
import static de.rcenvironment.rce.communication.CommunicationTestHelper.SERVICE;

import java.io.Serializable;
import java.util.List;

import junit.framework.TestCase;
import de.rcenvironment.rce.communication.PlatformIdentifier;

/**
 * Unit test for the <code>ServiceCallRequest</code> class.
 * 
 * @author Thijs Metsch
 * @author Heinrich Wendel
 */
public class ServiceCallRequestTest extends TestCase {

    /**
     * Constant.
     */
    private static final String SERVICE_PROPERTIES = null;

    /**
     * Class under test.
     */
    private ServiceCallRequest myCommunicationRequest;

    @Override
    protected void setUp() throws Exception {
        myCommunicationRequest = new ServiceCallRequest(LOCAL_PLATFORM, REMOTE_PLATFORM,
            SERVICE, SERVICE_PROPERTIES, METHOD, PARAMETER_LIST);
    }

    @Override
    protected void tearDown() throws Exception {
        myCommunicationRequest = null;
    }

    /*
     * test for success
     */

    /**
     * Test Constructor for success.
     */
    public void testForSuccess() {
        new ServiceCallRequest(LOCAL_PLATFORM, REMOTE_PLATFORM, SERVICE, SERVICE_PROPERTIES, METHOD, PARAMETER_LIST);
        new ServiceCallRequest(LOCAL_PLATFORM, REMOTE_PLATFORM, SERVICE, "", METHOD, PARAMETER_LIST);
        new ServiceCallRequest(LOCAL_PLATFORM, REMOTE_PLATFORM, SERVICE, SERVICE_PROPERTIES, METHOD, null);
    }

    /*
     * test for failure
     */

    /**
     * Test Constructor for failure.
     */
    public void testForFailure() {
        try {
            new ServiceCallRequest(null, REMOTE_PLATFORM, SERVICE, SERVICE_PROPERTIES, METHOD, PARAMETER_LIST);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        try {
            new ServiceCallRequest(LOCAL_PLATFORM, null, SERVICE, SERVICE_PROPERTIES, METHOD, PARAMETER_LIST);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        try {
            new ServiceCallRequest(LOCAL_PLATFORM, REMOTE_PLATFORM, null, SERVICE_PROPERTIES, METHOD, PARAMETER_LIST);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        try {
            new ServiceCallRequest(LOCAL_PLATFORM, REMOTE_PLATFORM, "", SERVICE_PROPERTIES, METHOD, PARAMETER_LIST);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        try {
            new ServiceCallRequest(LOCAL_PLATFORM, REMOTE_PLATFORM, SERVICE, SERVICE_PROPERTIES, null, PARAMETER_LIST);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        try {
            new ServiceCallRequest(LOCAL_PLATFORM, REMOTE_PLATFORM, SERVICE, SERVICE_PROPERTIES, null, PARAMETER_LIST);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    /*
     * test for success
     */

    /** Test. */
    public void testRequestedPlatform() {
        assertEquals(LOCAL_PLATFORM, myCommunicationRequest.getRequestedPlatform());
    }

    /** Test. */
    public void testGetCallingPlatform() {
        assertEquals(REMOTE_PLATFORM, myCommunicationRequest.getCallingPlatform());
    }

    /**
     * Test method for success.
     * 
     */
    public void testGetServiceForSuccess() {
        myCommunicationRequest.getService();
    }

    /**
     * Test method for success.
     * 
     */
    public void testGetServicePropertiesForSuccess() {
        myCommunicationRequest.getServiceProperties();
    }

    /**
     * Test method for success.
     * 
     */
    public void testGetServiceMethodForSuccess() {
        myCommunicationRequest.getServiceMethod();
    }

    /**
     * Test method for success.
     * 
     */
    public void testGetParameterListForSuccess() {
        myCommunicationRequest.getParameterList();
    }

    /*
     * test for sanity.
     */

    /**
     * Test method for sanity.
     * 
     */
    public void testGetHostForSanity() {
        PlatformIdentifier host = myCommunicationRequest.getRequestedPlatform();
        assertEquals(host, LOCAL_PLATFORM);
    }

    /**
     * Test method for sanity.
     * 
     */
    public void testGetServiceForSanity() {
        String service = myCommunicationRequest.getService();
        assertEquals(service, SERVICE);

    }

    /**
     * Test method for sanity.
     * 
     */
    public void testGetServicePropertiesForSanity() {
        String serviceProperties = myCommunicationRequest.getServiceProperties();
        assertEquals(serviceProperties, SERVICE_PROPERTIES);

    }

    /**
     * Test method for sanity.
     * 
     */
    public void testGetServiceMethodForSanity() {
        String serviceMethod = myCommunicationRequest.getServiceMethod();
        assertEquals(serviceMethod, METHOD);
    }

    /**
     * Test method for sanity.
     * 
     */
    public void testGetParameterListForSanity() {
        List<? extends Serializable> list = myCommunicationRequest.getParameterList();
        assertEquals(list, PARAMETER_LIST);
    }

    /**
     * Test increaseHopCount method.
     */
    public void testIncreaseHopCount() {
        assertEquals(myCommunicationRequest.increaseHopCount(), new Integer(1));
        assertEquals(myCommunicationRequest.increaseHopCount(), new Integer(2));
    }

}
