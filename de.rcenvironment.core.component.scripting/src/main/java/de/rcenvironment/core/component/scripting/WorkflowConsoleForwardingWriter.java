/*
 * Copyright (C) 2006-2014 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.component.scripting;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import de.rcenvironment.core.component.execution.api.ComponentContext;
import de.rcenvironment.core.component.execution.api.ConsoleRow;
import de.rcenvironment.core.component.execution.api.ConsoleRow.Type;
import de.rcenvironment.core.scripting.python.PythonOutputWriter;

/**
 * Implementation of {@link PythonOutputWriter}, which forwards output to the workflow console.
 * 
 * @author Doreen Seider
 */
public final class WorkflowConsoleForwardingWriter extends PythonOutputWriter {

    private ComponentContext componentContext;

    private final Type consoleType;
    
    private final CountDownLatch printingLinesFinishedLatch = new CountDownLatch(1);

    public WorkflowConsoleForwardingWriter(Object lock, ComponentContext componentContext, ConsoleRow.Type type) {
        this(lock, componentContext, type, null);
    }

    public WorkflowConsoleForwardingWriter(Object lock, ComponentContext componentContext, ConsoleRow.Type consoleType, File logFile) {
        super(lock, logFile);
        this.componentContext = componentContext;
        this.consoleType = consoleType;
    }

    @Override
    public void close() throws IOException {
        super.close();
        synchronized (lock) {
            // enqueues a task, which set the compInfo variable to null
            // doing it that way (and not setting the compInfo variable directly here to null), because that ensures that the compInfo
            // variable is set to null not before the last line was forwarded
            executionQueue.enqueue(new Runnable() {

                @Override
                public void run() {
                    // set to null as the WorkflowConsoleForwardingWriter instance are hold by the Jython sript engine
                    // for any length of time
                    componentContext = null;
                }
            });
        }
    }

    @Override
    protected void onNewLineToForward(String line) {
        if (line == null) {
            printingLinesFinishedLatch.countDown();
        } else {
            componentContext.printConsoleLine(line, consoleType);
        }
    }
    
    /**
     * Awaits the writer be get closed.
     * 
     * @throws InterruptedException if wait is interrupted
     */
    public void awaitPrintingLinesFinished() throws InterruptedException {
        printingLinesFinishedLatch.await();
    }
    
}
