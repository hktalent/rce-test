/*
 * Copyright (C) 2006-2011 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.commons.executor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Arrays;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.OS;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A {@link CommandLineExecutor} that executes the given commands locally.
 * 
 * @author Robert Mischke
 * 
 */
public class LocalApacheCommandLineExecutor extends AbstractCommandLineExecutor implements CommandLineExecutor {

    /** Top-level command token template for Linux invocation. */
    private static final String[] LINUX_SHELL_TOKENS = { "/bin/sh", "-c", "[command]" };

    /** Top-level command token template for Windows invocation. */
    private static final String[] WINDOWS_SHELL_TOKENS = { "cmd.exe", "/c", "[command]" };

    private File workDir;

    private Log log = LogFactory.getLog(getClass());

    private DefaultExecutor executor;

    private PipedInputStream pipedStdInputStream;

    private PipedInputStream pipedErrInputStream;

    private DefaultExecuteResultHandler resultHandler;

    private PipedOutputStream pipedStdOutputStream;

    private PipedOutputStream pipedErrOutputStream;

    private ExtendedPumpStreamHandler streamHandler;

    /**
     * Creates a local executor with the given path as its working directory. If the given path is
     * not a directory, it is created.
     * 
     * @param workDirPath the directory on the local system to use for execution
     * 
     * @throws IOException if the given {@link File} is not a directory and also could not be
     *         created
     */
    public LocalApacheCommandLineExecutor(File workDirPath) throws IOException {

        this.workDir = workDirPath;
    }

    @Override
    public void start(String commandString) throws IOException {
        start(commandString, null);
    }

    @Override
    public void start(String commandString, final InputStream stdinStream) throws IOException {


        if (!workDir.isDirectory()) {
            // try to create the work directory
            workDir.mkdirs();
            if (!workDir.isDirectory()) {
                throw new IOException("Failed to create provided work directory " + workDir.getAbsolutePath());
            }
        }

        // build the top-level token array
        String[] commandTokens;
        CommandLine cmd;
        if (OS.isFamilyWindows()) {
            commandTokens = Arrays.copyOf(WINDOWS_SHELL_TOKENS, WINDOWS_SHELL_TOKENS.length);
            commandTokens[WINDOWS_SHELL_TOKENS.length - 1] = commandString;
            String command = "";
            for (String str : commandTokens){
                command += str + " ";
            }
            cmd = CommandLine.parse(command);
            
        } else {
            commandTokens = Arrays.copyOf(LINUX_SHELL_TOKENS, LINUX_SHELL_TOKENS.length);
            commandTokens[LINUX_SHELL_TOKENS.length - 1] = commandString;
            cmd = new CommandLine(commandTokens[0]);
            for (int i = 1; i < commandTokens.length; i++) {
                cmd.addArgument(commandTokens[i], false);
            }
        }
        pipedStdOutputStream = new PipedOutputStream();
        pipedErrOutputStream = new PipedOutputStream();
        pipedStdInputStream = new PipedInputStream(pipedStdOutputStream);
        pipedErrInputStream = new PipedInputStream(pipedErrOutputStream);

        executor = new DefaultExecutor();
        resultHandler = new DefaultExecuteResultHandler();
        streamHandler = new ExtendedPumpStreamHandler(pipedStdOutputStream, pipedErrOutputStream, stdinStream);
        executor.setStreamHandler(streamHandler);
        executor.setWorkingDirectory(workDir);
        if (env.isEmpty()){
            executor.execute(cmd, resultHandler);
        } else {
            executor.execute(cmd, env, resultHandler);
        }

    }

    @Override
    public String getWorkDirPath() {
        return workDir.getAbsolutePath();
    }

    @Override
    public InputStream getStdout() {
        return pipedStdInputStream;
    }

    @Override
    public InputStream getStderr() {
        return pipedErrInputStream;
    }

    @Override
    public int waitForTermination() throws IOException, InterruptedException {
        resultHandler.waitFor();
        return resultHandler.getExitValue();
    }

    @Override
    public void uploadToWorkdir(File localFile, String remoteLocation) throws IOException {
        File targetFile = new File(workDir, remoteLocation);
        log.debug("Local copy from " + localFile.getAbsolutePath() + " to " + targetFile.getAbsolutePath());
        FileUtils.copyFile(localFile, targetFile);
    };

    @Override
    public void downloadFromWorkdir(String remoteLocation, File localFile) throws IOException {
        FileUtils.copyFile(new File(workDir, remoteLocation), localFile);
    }

    @Override
    public void downloadWorkdir(File localDir) throws IOException {
        FileUtils.copyDirectory(workDir, localDir);
    }

    @Override
    public void remoteCopy(String remoteSource, String remoteTarget) throws IOException {
        FileUtils.copyFile(new File(remoteSource), new File(remoteTarget));
    }

    public DefaultExecuteResultHandler getResultHandler(){
        return resultHandler;
    }

    /**
     * This class overrides the normal {@link PumpStreamHandler} because the closeWhenExhausted flag when creating a pump must be set.
     * @author Sascha Zur
     *
     */
    private class ExtendedPumpStreamHandler extends PumpStreamHandler{
        public ExtendedPumpStreamHandler(
                PipedOutputStream pipedStdOutputStream,
                PipedOutputStream pipedErrOutputStream, InputStream stdinStream) {
            super(pipedStdOutputStream, pipedErrOutputStream, stdinStream);
        }

        @Override
        protected Thread createPump(final InputStream is, final OutputStream os) {
            return createPump(is, os, true);
        }
    }
}
