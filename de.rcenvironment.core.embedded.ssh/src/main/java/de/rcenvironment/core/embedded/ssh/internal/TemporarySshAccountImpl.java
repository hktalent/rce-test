/*
 * Copyright (C) 2006-2014 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.embedded.ssh.internal;

import java.io.File;

import de.rcenvironment.core.embedded.ssh.api.TemporarySshAccount;

/**
 * Represents a temporary login account, authenticated with a public key or password. Currently used for restricted SCP uploads/downloads
 * only.
 * 
 * @author Sebastian Holtappels
 * @author Robert Mischke
 */
public class TemporarySshAccountImpl extends SshAccountImpl implements TemporarySshAccount {

    private String virtualScpRootPath = null;

    private File localScpRootPath = null;

    public TemporarySshAccountImpl() {}

    public String getVirtualScpRootPath() {
        return virtualScpRootPath;
    }

    public void setVirtualScpRootPath(String path) {
        this.virtualScpRootPath = path;
    }

    public File getLocalScpRootPath() {
        return localScpRootPath;
    }

    public void setLocalScpRootPath(File localScpRootPath) {
        this.localScpRootPath = localScpRootPath;
    }

}
