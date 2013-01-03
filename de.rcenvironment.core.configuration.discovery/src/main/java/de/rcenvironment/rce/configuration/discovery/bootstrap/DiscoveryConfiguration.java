/*
 * Copyright (C) 2006-2010 DLR, Fraunhofer SCAI, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.rce.configuration.discovery.bootstrap;

/**
 * Class providing the configuration of the discovery {@link Bundle}. Additionally it defines the
 * default configuration.
 * 
 * @author Robert Mischke
 */
public class DiscoveryConfiguration {

    private DiscoveryClientSetup useDiscovery = null;

    private DiscoveryServerSetup runDiscoveryServer = null;

    public DiscoveryClientSetup getUseDiscovery() {
        return useDiscovery;
    }

    public void setUseDiscovery(DiscoveryClientSetup useDiscovery) {
        this.useDiscovery = useDiscovery;
    }

    public DiscoveryServerSetup getRunDiscoveryServer() {
        return runDiscoveryServer;
    }

    public void setRunDiscoveryServer(DiscoveryServerSetup runDiscoveryServer) {
        this.runDiscoveryServer = runDiscoveryServer;
    }

}
