/*
 * Copyright (C) 2006-2012 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */
package de.rcenvironment.core.communication.routing.internal;

import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.rcenvironment.core.communication.testutils.VirtualInstance;

/**
 * Obsolete test container; move/rework the contained tests.
 * 
 * @author Phillip Kroll
 * @author Robert Mischke
 */
public class LargeScaleBasicTests extends AbstractLargeScaleTest {

    /*
     * Parameters for test scenarios.
     */
    private static final int TEST_SIZE = 20;

    private static final int EPOCHS = 5;

    /**
     * @throws Exception on uncaught exceptions
     */
    @BeforeClass
    public static void setTestParameters() throws Exception {
        testSize = TEST_SIZE;
        epochs = EPOCHS;
    }

    /**
     * @throws Exception on uncaught exceptions
     */
    @Test
    public void testSendLinkStateAdvertisementBarrierRing() throws Exception {

        prepareWaitForNextMessage();
        instanceUtils.connectToDoubleChainTopology(allInstances);
        waitForNextMessage();
        waitForNetworkSilence();

        assertTrue(instanceUtils.allInstancesConverged(allInstances));
    }

    /**
     * @throws Exception on uncaught exceptions
     */
    @Test
    public void testSendLinkStateAdvertisementBarrierChain() throws Exception {

        prepareWaitForNextMessage();
        instanceUtils.connectToDoubleChainTopology(allInstances);
        waitForNextMessage();
        waitForNetworkSilence();

        assertTrue(instanceUtils.allInstancesConverged(allInstances));
    }

    /**
     * @throws Exception on uncaught exceptions
     */
    @Test
    public void testSendLinkStateAdvertisementBarrierStar() throws Exception {

        prepareWaitForNextMessage();
        instanceUtils.connectToDoubleStarTopology(allInstances);
        waitForNextMessage();
        waitForNetworkSilence();

        assertTrue(instanceUtils.allInstancesConverged(allInstances));
    }

    /**
     * @throws Exception on uncaught exceptions
     */
    @Test
    public void testBarrierInDoubleRingTopology() throws Exception {

        prepareWaitForNextMessage();
        instanceUtils.connectToDoubleChainTopology(allInstances);
        waitForNextMessage();
        waitForNetworkSilence();

        assertTrue(instanceUtils.allInstancesConverged(allInstances));
    }

    /**
     * @throws Exception on uncaught exceptions
     */
    @Test
    public void testBarrierInDoubleChainTopoglogy() throws Exception {

        prepareWaitForNextMessage();
        instanceUtils.connectToDoubleRingTopology(allInstances);
        waitForNextMessage();
        waitForNetworkSilence();

        assertTrue(instanceUtils.allInstancesConverged(allInstances));
    }

    /**
     * @throws Exception on uncaught exceptions
     */
    @Test
    public void testBarrierInDoubleStarTopology() throws Exception {

        prepareWaitForNextMessage();
        instanceUtils.connectToDoubleChainTopology(allInstances);
        waitForNextMessage();
        waitForNetworkSilence();

        assertTrue(instanceUtils.allInstancesConverged(allInstances));
    }

    /**
     * @throws Exception on uncaught exceptions
     */
    @Test
    @Ignore("TODO uses active LSA sending and message checking; rework or discard")
    public void testLsaReceptionInDoubleRingTopology() throws Exception {

        prepareWaitForNextMessage();
        instanceUtils.connectToDoubleRingTopology(allInstances);
        waitForNextMessage();
        waitForNetworkSilence();

        for (int epoch = 0; epoch < epochs; epoch++) {

            prepareWaitForNextMessage();
            VirtualInstance instance = instanceUtils.getRandomInstance(allInstances);
            String id = instance.getRoutingService().getProtocolManager().broadcastLsa();
            waitForNextMessage();
            waitForNetworkSilence();

            for (int i = 0; i < allInstances.length; i++) {
                assertTrue(allInstances[i].checkMessageReceivedById(id));
            }
        }
    }

    /**
     * @throws Exception on uncaught exceptions
     */
    @Test
    @Ignore("TODO uses active LSA sending and message checking; rework or discard")
    public void testLsaReceptionInDoubleChainTopology() throws Exception {

        prepareWaitForNextMessage();
        instanceUtils.connectToDoubleChainTopology(allInstances);
        waitForNextMessage();
        waitForNetworkSilence();

        for (int epoch = 0; epoch < epochs; epoch++) {

            prepareWaitForNextMessage();
            VirtualInstance instance = instanceUtils.getRandomInstance(allInstances);
            String id = instance.getRoutingService().getProtocolManager().broadcastLsa();
            waitForNextMessage();
            waitForNetworkSilence();

            for (int i = 0; i < allInstances.length; i++) {
                assertTrue(allInstances[i].checkMessageReceivedById(id));
            }
        }
    }

    /**
     * @throws Exception on uncaught exceptions
     */
    @Test
    @Ignore("TODO uses active LSA sending and message checking; rework or discard")
    public void testLsaReceptionInDoubleStarTopology() throws Exception {

        prepareWaitForNextMessage();
        instanceUtils.connectToDoubleStarTopology(allInstances);
        waitForNextMessage();
        waitForNetworkSilence();

        for (int epoch = 0; epoch < epochs; epoch++) {

            prepareWaitForNextMessage();
            VirtualInstance instance = instanceUtils.getRandomInstance(allInstances);
            String id = instance.getRoutingService().getProtocolManager().broadcastLsa();
            waitForNextMessage();
            waitForNetworkSilence();

            for (int i = 0; i < allInstances.length; i++) {
                assertTrue(allInstances[i].checkMessageReceivedById(id));
            }
        }
    }

}
