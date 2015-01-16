/*
 * Copyright (C) 2006-2014 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.communication.management.internal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;

import de.rcenvironment.core.communication.api.CommunicationService;
import de.rcenvironment.core.communication.common.NodeIdentifier;
import de.rcenvironment.core.communication.management.BenchmarkService;
import de.rcenvironment.core.communication.management.BenchmarkSetup;
import de.rcenvironment.core.utils.common.concurrent.SharedThreadPool;
import de.rcenvironment.core.utils.common.concurrent.TaskDescription;
import de.rcenvironment.core.utils.common.textstream.TextOutputReceiver;

/**
 * A {@link Runnable} that performs a communication layer benchmark.
 * 
 * @author Robert Mischke
 */
public class BenchmarkProcess implements Runnable {

    // private static final int STATUS_OUTPUT_INTERVAL_MSEC = 5000;

    private final Log log = LogFactory.getLog(getClass());

    /**
     * A {@link Runnable} that acts as a single-threaded sender within a benchmark.
     * 
     * @author Robert Mischke
     */
    private final class SenderTask implements Runnable {

        private NodeIdentifier targetNode;

        private AtomicInteger messageCounter;

        private BenchmarkSubtaskImpl subtask;

        private BenchmarkService remoteService;

        public SenderTask(BenchmarkSubtaskImpl subtask, NodeIdentifier nodeId, AtomicInteger messageCounter) {
            this.targetNode = nodeId;
            this.messageCounter = messageCounter;
            this.subtask = subtask;
            this.remoteService = (BenchmarkService) communicationService.getService(BenchmarkService.class,
                (NodeIdentifier) targetNode, bundleContext);
        }

        @Override
        @TaskDescription("Communication layer benchmark: sender task")
        public void run() {
            // this ensures that all threads perform the predefined number of requests
            while (messageCounter.decrementAndGet() >= 0) {
                long startTime = System.nanoTime();
                RuntimeException error = null;
                try {
                    performRequest();
                } catch (RuntimeException e) {
                    // optional logging of connection errors is left to the calling code
                    error = e;
                }
                long duration = System.nanoTime() - startTime;
                subtask.recordSingleResult(targetNode, duration, error);
            }
        }

        private void performRequest() {
            Serializable response = remoteService.respond(new byte[subtask.getRequestSize()], subtask.getResponseSize(),
                subtask.getResponseDelay());
            // basic verification of response: is the payload a byte array of expected size?
            byte[] responseBytes = (byte[]) response;
            if (responseBytes == null || responseBytes.length != subtask.getResponseSize()) {
                throw new IllegalStateException("Unexpected benchmark response payload");
            }
        }
    }

    private List<BenchmarkSubtaskImpl> subtasks;

    private TextOutputReceiver outputReceiver;

    private CommunicationService communicationService;

    private BundleContext bundleContext;

    @SuppressWarnings("unchecked")
    public BenchmarkProcess(BenchmarkSetup setup, TextOutputReceiver outputReceiver, CommunicationService communicationService,
        BundleContext context) {
        this.subtasks = new ArrayList<BenchmarkSubtaskImpl>();
        // cast to expected BenchmarkSubtask implementation; rework if necessary
        subtasks.addAll((Collection<? extends BenchmarkSubtaskImpl>) setup.getSubtasks());
        this.communicationService = communicationService;
        this.outputReceiver = outputReceiver;
        this.bundleContext = context;
    }

    @Override
    @TaskDescription("Communication layer benchmark: main task")
    public void run() {
        outputReceiver.onStart();

        // initialize and start
        printOutput("Starting " + subtasks.size() + " benchmark task(s)");
        int index = 1;
        for (BenchmarkSubtaskImpl subtask : subtasks) {
            printOutput("  Task " + (index++) + ": " + subtask.formatDescription());
            subtask.recordStartTime();
            for (NodeIdentifier nodeId : subtask.getTargetNodes()) {
                AtomicInteger messageCounter = new AtomicInteger(subtask.getNumMessages());
                for (int senderIndex = 0; senderIndex < subtask.getThreadsPerTarget(); senderIndex++) {
                    SenderTask sender = new SenderTask(subtask, nodeId, messageCounter);
                    SharedThreadPool.getInstance().execute(sender);
                }
            }
        }

        // TODO spawn progress watcher thread

        // await completion
        printOutput("Awaiting benchmark results...");
        for (BenchmarkSubtaskImpl subtask : subtasks) {
            try {
                subtask.awaitTermination();
            } catch (InterruptedException e) {
                log.warn("Benchmark subtask interrupted", e);
                outputReceiver.onFatalError(e);
            }
        }

        // print results
        printOutput("Benchmark results:");
        index = 1;
        for (BenchmarkSubtaskImpl subtask : subtasks) {
            printOutput("  Task " + (index++) + ": " + subtask.formatDescription());
            for (String line : subtask.formatResults()) {
                printOutput("    " + line);
            }
        }

        outputReceiver.onFinished();
    }

    /**
     * Sends a line of output to the configured receiver.
     * 
     * @param line the output line
     */
    private void printOutput(String line) {
        outputReceiver.addOutput(line);
    }
}
