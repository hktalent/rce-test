/*
 * Copyright (C) 2006-2014 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */
 
package de.rcenvironment.core.utils.ssh.jsch;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;


/**
 * Provides test behavior for incoming commands.
 * 
 * @author Doreen Seider
 */
public class DummyCommand implements Command {
    
    /** Test constant. */
    public static final String EMPTY_STRING = "";
    
    /** Test constant. */
    // TODO document: what is this relative to? - misc_ro, May 2013
    public static final String WORKDIR_LOCAL = "./local-workdir/";

    /** Test constant. */
    public static final String WORKDIR_REMOTE = "./remote-workdir/";
    
    protected ExitCallback exitCallback;

    private String stdout;
    
    private String stderr;
    
    private int exitValue;
    
    private OutputStream stdoutStream;

    private OutputStream stderrStream;
    

    public DummyCommand() {
        this(null, null, 0);
    }
    
    public DummyCommand(String stdout, String stderr) {
        this(stdout, stderr, 0);
    }
    
    public DummyCommand(String stdout, String stderr, int exitValue) {
        this.stdout = stdout;
        this.stderr = stderr;
        this.exitValue = exitValue;
    }
    
    @Override
    public void setInputStream(InputStream in) {}

    @Override
    public void setOutputStream(OutputStream out) {
        this.stdoutStream = out;
    }

    @Override
    public void setErrorStream(OutputStream err) {
        this.stderrStream = err;
    }

    @Override
    public void setExitCallback(ExitCallback callback) {
        this.exitCallback = callback;
    }

    @Override
    public void start(Environment env) throws IOException {

        if (stdout != null) {
            stdoutStream.write(stdout.getBytes());
        } else {
            stdoutStream.write(EMPTY_STRING.getBytes());
        }
        if (stderr != null) {
            stderrStream.write(stderr.getBytes());
        } else {
            stderrStream.write(EMPTY_STRING.getBytes());
        }

        IOUtils.closeQuietly(stdoutStream);
        IOUtils.closeQuietly(stderrStream);
        exitCallback.onExit(exitValue);
    }

    @Override
    public void destroy() {}
    
}
