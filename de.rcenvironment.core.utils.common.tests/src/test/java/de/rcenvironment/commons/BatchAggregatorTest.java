/*
 * Copyright (C) 2006-2011 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.commons;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.rcenvironment.commons.BatchAggregator.BatchProcessor;

/**
 * Unit test for {@link BatchAggregator}.
 * 
 * @author Robert Mischke
 */
public class BatchAggregatorTest {

    /**
     * A dummy receiver for generated batches. Also provides a Semaphore for convenient waiting.
     * 
     * @author Robert Mischke
     */
    private class CollectingBatchProcessor<T> implements BatchProcessor<T> {

        public List<List<T>> receivedBatches = new ArrayList<List<T>>();

        public Semaphore receiveCountSemaphore = new Semaphore(0);

        @Override
        public void processBatch(List<T> batch) {
            receivedBatches.add(batch);
            receiveCountSemaphore.release();
        }

    }

    /**
     * A batch processor that throws a {@link RuntimeException} from {@link #processBatch(List)}.
     * 
     * @author Robert Mischke
     */
    private static final class FailingBatchProcessor implements BatchProcessor<String> {

        @Override
        public void processBatch(List<String> batch) {
            LogFactory.getLog(getClass()).debug("Simulating processor failure for element " + batch.get(0));
            // throw arbitrary RuntimeException
            throw new IllegalStateException();
        }
    }

    // arbitrary "short wait" length
    private static final int SHORT_WAIT_MSEC = 100;

    // the timeout of the "blocking callback" test; extracted for CheckStyle
    private static final int BLOCKING_TEST_TIMEOUT = 1000;

    private Log originalLogger;

    private Log mockLogger;

    private Log unitTestLogger = LogFactory.getLog(getClass());

    /**
     * Test setup.
     */
    @Before
    public void setUp() {
        originalLogger = BatchAggregator.getLogger();
        mockLogger = EasyMock.createMock(Log.class);
        BatchAggregator.setLogger(mockLogger);
        unitTestLogger.debug("Replaced BatchAggregator logger with mock");
    }

    /**
     * Test teardown.
     */
    @After
    public void tearDown() {
        unitTestLogger.debug("Restoring BatchAggregator logger");
        BatchAggregator.setLogger(originalLogger);
    }

    /**
     * Test concurrent writing to a single aggregator, and whether the posted messages are in the
     * correct order for each posting thread.
     * 
     * @throws InterruptedException on Thread interruption
     */
    @Test
    public void testMultiThreadSingleBatchPosting() throws InterruptedException {
        final int numThreads = 10;
        final int numMsgPerThread = 500;
        final int maxLatency = 10 * 1000; // test should finish in 10 secs
        final AtomicInteger threadIdGenerator = new AtomicInteger();

        final CollectingBatchProcessor<String> collector = new CollectingBatchProcessor<String>();

        final BatchAggregator<String> aggregator =
            new BatchAggregator<String>(numThreads * numMsgPerThread, maxLatency, collector);

        ExecutorService executor = Executors.newFixedThreadPool(10);
        Runnable threadRunnable = new Runnable() {

            @Override
            public void run() {
                int threadId = threadIdGenerator.incrementAndGet();
                for (int i = 1; i <= numMsgPerThread; i++) {
                    aggregator.enqueue(String.format("Thread %d, Msg %d", threadId, i));
                }
            }
        };

        for (int t = 1; t <= numThreads; t++) {
            executor.execute(threadRunnable);
        }

        collector.receiveCountSemaphore.acquire();

        List<String> batch = collector.receivedBatches.get(0);

        assertEquals(numThreads * numMsgPerThread, batch.size());

        // test per-thread ordering of messages
        Pattern p = Pattern.compile("Thread (\\d+), Msg (\\d+)");
        for (int t = 1; t <= numThreads; t++) {
            String threadId = Integer.toString(t);
            int lastMsg = 0; // not arbitrary; must be 1 less than 1st message
            for (String element : batch) {
                Matcher matcher = p.matcher(element);
                if (!matcher.find()) {
                    throw new IllegalArgumentException(element);
                }
                if (matcher.group(1).equals(threadId)) {
                    int msgId = Integer.parseInt(matcher.group(2));
                    assertEquals("Message out of order: " + element, lastMsg + 1, msgId);
                    lastMsg = msgId;
                }
            }
            assertEquals("Received less messages per thread than expected", numMsgPerThread, lastMsg);
        }

    }

    /**
     * Tests whether batches are properly sent out on maximum latency timeout (but not earlier).
     * 
     * @throws InterruptedException on Thread interruption
     */
    @Test
    public void testBatchSendingByMaxLatency() throws InterruptedException {
        final CollectingBatchProcessor<String> collector = new CollectingBatchProcessor<String>();

        final int maxLatency = 300;

        final BatchAggregator<String> aggregator =
            new BatchAggregator<String>(Integer.MAX_VALUE, maxLatency, collector);

        aggregator.enqueue("latency.msg1");

        assertEquals("Message 1 received before timer should have elapsed", 0,
            collector.receivedBatches.size());

        Thread.sleep(maxLatency * 2);

        assertEquals("No batch received after timer should have elapsed", 1,
            collector.receivedBatches.size());

        // the old batch must have ended, so this message should trigger a new batch
        // (and therefore, a new timer)
        aggregator.enqueue("latency.msg2");

        assertEquals("Message 2 received before timer should have elapsed", 1,
            collector.receivedBatches.size());

        Thread.sleep(maxLatency * 2);

        assertEquals("No batch received after timer should have elapsed", 2,
            collector.receivedBatches.size());

        // check consistency
        assertEquals("latency.msg1", collector.receivedBatches.get(0).get(0));
        assertEquals("latency.msg2", collector.receivedBatches.get(1).get(0));
    }

    /**
     * Tests whether batches are properly sent out when max size is reached.
     * 
     * @throws InterruptedException on Thread interruption
     */
    @Test
    public void testSizeLimiting() throws InterruptedException {

        final int numBatches = 20;
        final int maxBatchSize = 5;
        final int numMessages = maxBatchSize * numBatches;
        final int maxLatency = 100;

        final CollectingBatchProcessor<String> collector = new CollectingBatchProcessor<String>();
        final BatchAggregator<String> aggregator =
            new BatchAggregator<String>(maxBatchSize, maxLatency, collector);

        for (int i = 1; i <= numMessages; i++) {
            aggregator.enqueue("msg" + i);
        }

        // let timers finish
        Thread.sleep(maxLatency * 2);

        assertEquals("Wrong number of batches from size limiting", numBatches, collector.receivedBatches.size());

        // check consistency (some samples)
        assertEquals("msg1", collector.receivedBatches.get(0).get(0));
        assertEquals("msg5", collector.receivedBatches.get(0).get(maxBatchSize - 1));
        assertEquals("msg96", collector.receivedBatches.get(numBatches - 1).get(0));
        assertEquals("msg100", collector.receivedBatches.get(numBatches - 1).get(maxBatchSize - 1));
    }

    /**
     * Verifies that elements can still be added after the processor failed for a batch triggered by
     * maximum size.
     * 
     * @throws InterruptedException on test interruption
     */
    @Test
    public void testFailingBatchProcessorWithSizeBatching() throws InterruptedException {
        BatchProcessor<String> failingProcessor = new FailingBatchProcessor();
        final int unusedMaxLatency = 100;
        BatchAggregator<String> aggregator = new BatchAggregator<String>(1, unusedMaxLatency, failingProcessor);

        // expect two errors to be logged
        mockLogger.error(EasyMock.anyObject(), (Throwable) EasyMock.anyObject());
        mockLogger.error(EasyMock.anyObject(), (Throwable) EasyMock.anyObject());
        EasyMock.replay(mockLogger);

        // send first element; this triggers a batch to process
        aggregator.enqueue("String 1");

        // wait a moment, just to be sure
        Thread.sleep(SHORT_WAIT_MSEC);

        // check that elements can still be added
        aggregator.enqueue("String 2");

        // wait a moment so the aggregator thread has finished
        Thread.sleep(SHORT_WAIT_MSEC);

        EasyMock.verify(mockLogger);
    }

    /**
     * Verifies that elements can still be added after the processor failed for a batch triggered by
     * maximum latency.
     * 
     * @throws InterruptedException on test interruption
     */
    @Test
    public void testFailingBatchProcessorWithTimeBatching() throws InterruptedException {
        BatchProcessor<String> failingProcessor = new FailingBatchProcessor();
        final int unusedMaxBatchSize = 50;
        final int shortTimerWait = 50;
        BatchAggregator<String> aggregator = new BatchAggregator<String>(unusedMaxBatchSize, shortTimerWait, failingProcessor);

        // expect two errors to be logged
        mockLogger.error(EasyMock.anyObject(), (Throwable) EasyMock.anyObject());
        mockLogger.error(EasyMock.anyObject(), (Throwable) EasyMock.anyObject());
        EasyMock.replay(mockLogger);

        // send first element
        aggregator.enqueue("String 3");

        // wait until the timer triggers a batch to process
        Thread.sleep(shortTimerWait * 3);

        // check that elements can still be added
        aggregator.enqueue("String 4");

        // wait so possible "late" timer failures can surface
        Thread.sleep(shortTimerWait * 3);

        EasyMock.verify(mockLogger);
    }

    /**
     * Verifies that a blocking callback handler does not interfere with simultaneous enqueue()
     * calls.
     * 
     * @throws InterruptedException on thread interruption
     */
    @Test(timeout = BLOCKING_TEST_TIMEOUT)
    public void testBlockingCallbackHandler() throws InterruptedException {
        final CountDownLatch callbackBlocker = new CountDownLatch(1);

        // create an aggregator with batch size 1, so each enqueue() should trigger a callback
        BatchAggregator<String> aggregator = new BatchAggregator<String>(1, Integer.MAX_VALUE, new BatchProcessor<String>() {

            @Override
            public void processBatch(List<String> batch) {
                unitTestLogger.debug("Blocking callback method...");
                try {
                    callbackBlocker.await();
                } catch (InterruptedException e) {
                    unitTestLogger.warn("Callback thread interrupted", e);
                }
                unitTestLogger.debug("Leaving callback method");
            }
        });

        aggregator.enqueue("dummy");
        unitTestLogger.debug("Enqueued object 1; object 2 should follow immediately");
        // this is the call that would "hang" on failure
        aggregator.enqueue("dummy2");
        unitTestLogger.debug("Enqueued object 2");

        // wait briefly for possible threading effects
        Thread.sleep(SHORT_WAIT_MSEC);

        // signal to "release" the callback method
        callbackBlocker.countDown();
    }
}
