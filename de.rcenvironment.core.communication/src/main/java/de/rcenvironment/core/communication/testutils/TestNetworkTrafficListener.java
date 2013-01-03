/*
 * Copyright (C) 2006-2012 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.communication.testutils;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import de.rcenvironment.core.communication.connection.NetworkTrafficListener;
import de.rcenvironment.core.communication.model.NetworkRequest;
import de.rcenvironment.core.communication.model.NetworkResponse;
import de.rcenvironment.core.communication.model.NodeIdentifier;
import de.rcenvironment.core.communication.utils.MetaDataWrapper;

/**
 * A {@link NetworkTrafficListener} intended for keeping track of all message traffic in virtual
 * network tests. Can be used to wait until no message has been sent for a certain time using 
 * {@link #waitForNetworkSilence(int, int).
 * 
 * @author Robert Mischke
 */
public class TestNetworkTrafficListener implements NetworkTrafficListener {

    private long lastTrafficTimestamp = 0;

    private long requestCount;

    private long lsaMessages = 0;

    private long routedMessages = 0;

    private long largestObservedHopCount = 0;

    private long unsuccessfulResponses = 0;

    private Semaphore trafficOccured = new Semaphore(0);

    @Override
    public void onRequestReceived(NetworkRequest request, NodeIdentifier sourceId) {

        onTraffic(true);
    }

    @Override
    public void onResponseGenerated(NetworkResponse response, NetworkRequest request, NodeIdentifier sourceId) {
        // note: strictly speaking, the traffic has not happened yet, but is about to
        if (MetaDataWrapper.createLsaMessage().matches(request.accessRawMetaData())) {
            lsaMessages++;
        }

        if (MetaDataWrapper.createRoutedMessage().matches(request.accessRawMetaData())) {
            routedMessages++;
        }

        if (MetaDataWrapper.wrap(request.accessRawMetaData()).getHopCount() > largestObservedHopCount) {
            largestObservedHopCount = MetaDataWrapper.wrap(request.accessRawMetaData()).getHopCount();
        }

        if (!response.isSuccess()) {
            unsuccessfulResponses++;
        }

        onTraffic(false);
    }

    private synchronized void onTraffic(boolean isRequest) {
        lastTrafficTimestamp = System.currentTimeMillis();
        if (isRequest) {
            requestCount++;
            trafficOccured.release();
        }
    }

    public synchronized long getRequestCount() {
        return requestCount;
    }

    public synchronized long getLastTrafficTimestamp() {
        return lastTrafficTimestamp;
    }

    /**
     * Clears the flag that indicates that traffic has occured.
     */
    public synchronized void clearCustomTrafficFlag() {
        trafficOccured.drainPermits();
    }

    /**
     * Waits until traffic has occured, or the timeout has expired.
     * 
     * @param maxWait the timeout
     * @throws TimeoutException on timeout
     * @throws InterruptedException on interruption
     */
    public void waitForCustomTrafficFlag(int maxWait) throws TimeoutException, InterruptedException {
        if (!trafficOccured.tryAcquire(maxWait, TimeUnit.MILLISECONDS)) {
            throw new TimeoutException("Maximum wait time for custom traffic flag (" + maxWait + " msec) exceeded");
        }
    }

    /**
     * Waits until no network traffic has been reported for (at least) the given timespan.
     * 
     * @param minSilenceTime the minimum no-traffic time
     * @param maxWait the maximum time to wait for the no-traffic condition
     * @throws TimeoutException on timeout
     * @throws InterruptedException on interruption
     */
    public void waitForNetworkSilence(int minSilenceTime, int maxWait) throws TimeoutException, InterruptedException {
        int pollInterval = minSilenceTime / 2;
        int totalWait = 0;
        while (true) {
            if (totalWait > maxWait) {
                throw new TimeoutException("Maximum wait time for network silence (" + maxWait + " msec) exceeded");
            }
            // note: synchronized through getter
            if (System.currentTimeMillis() - getLastTrafficTimestamp() >= minSilenceTime) {
                return;
            }
            Thread.sleep(pollInterval);
            totalWait += pollInterval;
        }
    }

    /**
     * @return the number of received LSA messages (?)
     * 
     *         TODO verify description
     */
    public long getLsaMessages() {
        return lsaMessages;
    }

    /**
     * @return Returns the largest observed hop count.
     */
    public long getLargestObservedHopCount() {
        return largestObservedHopCount;
    }

    /**
     * @return the number of unsuccessful requests
     * 
     *         TODO verify semantics and current behavior
     */
    public long getUnsuccessfulResponses() {
        return unsuccessfulResponses;
    }

    /**
     * @return the number of routed messages
     * 
     *         TODO verify semantics and current behavior
     */
    public long getRoutedMessages() {
        return routedMessages;
    }

}
