/*
 * Copyright (C) 2006-2011 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.rce.configuration.discovery.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.rcenvironment.rce.configuration.discovery.client.DiscoveryClientService;
import de.rcenvironment.rce.jetty.JettyService;

/**
 * SOAP/Jetty implementation of {@link DiscoveryClientService}.
 * 
 * @author Robert Mischke
 */
public class DiscoveryClientServiceImpl implements DiscoveryClientService {

    private JettyService jettyService;

    private final Log logger = LogFactory.getLog(getClass());

    @Override
    public String getReflectedIpFromDiscoveryServer(String address, int port) {
        String serviceURL = String.format(DiscoveryConstants.SOAP_SERVICE_URL_PATTERN, address, port);
        logger.info("Querying discovery service at " + serviceURL + " for the reflected local IP address");
        final RemoteDiscoveryService client =
            (RemoteDiscoveryService) jettyService.createWebServiceClient(RemoteDiscoveryService.class, serviceURL);
        String callerAddress;
        try {
            callerAddress = client.getReflectedCallerAddress();
            return callerAddress;
        } catch (RuntimeException e) {
            logger.error("Error querying discovery service at " + serviceURL + " for the reflected local IP address", e);
            return null;
        }
    }

    protected void bindJettyService(JettyService newService) {
        this.jettyService = newService;
    }

}
