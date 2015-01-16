/*
 * Copyright (C) 2006-2014 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.communication.management.internal;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.Version;

import de.rcenvironment.core.communication.channel.MessageChannelService;
import de.rcenvironment.core.communication.channel.ServerContactPoint;
import de.rcenvironment.core.communication.common.CommunicationException;
import de.rcenvironment.core.communication.configuration.CommunicationConfiguration;
import de.rcenvironment.core.communication.configuration.NodeConfigurationService;
import de.rcenvironment.core.communication.connection.api.ConnectionSetup;
import de.rcenvironment.core.communication.connection.api.ConnectionSetupService;
import de.rcenvironment.core.communication.management.CommunicationManagementService;
import de.rcenvironment.core.communication.messaging.MessageEndpointHandler;
import de.rcenvironment.core.communication.messaging.internal.HealthCheckRequestHandler;
import de.rcenvironment.core.communication.messaging.internal.MessageEndpointHandlerImpl;
import de.rcenvironment.core.communication.messaging.internal.RPCRequestHandler;
import de.rcenvironment.core.communication.model.InitialNodeInformation;
import de.rcenvironment.core.communication.model.MessageChannel;
import de.rcenvironment.core.communication.model.NetworkContactPoint;
import de.rcenvironment.core.communication.model.internal.NodeInformationRegistryImpl;
import de.rcenvironment.core.communication.nodeproperties.NodePropertiesService;
import de.rcenvironment.core.communication.nodeproperties.NodePropertyConstants;
import de.rcenvironment.core.communication.protocol.ProtocolConstants;
import de.rcenvironment.core.communication.routing.NetworkRoutingService;
import de.rcenvironment.core.communication.rpc.ServiceCallHandler;
import de.rcenvironment.core.communication.transport.spi.AbstractMessageChannel;
import de.rcenvironment.core.utils.common.VersionUtils;
import de.rcenvironment.core.utils.common.concurrent.SharedThreadPool;
import de.rcenvironment.core.utils.common.concurrent.TaskDescription;

/**
 * Default {@link CommunicationManagementService} implementation.
 * 
 * @author Robert Mischke
 */
public class CommunicationManagementServiceImpl implements CommunicationManagementService {

    /**
     * The delay between announcing the shutdown to all neighbors, and actually shutting down.
     */
    private static final int DELAY_AFTER_SHUTDOWN_ANNOUNCE_MSEC = 200;

    private MessageChannelService connectionService;

    private NetworkRoutingService networkRoutingService;

    private InitialNodeInformation ownNodeInformation;

    private NodeConfigurationService configurationService;

    private List<ServerContactPoint> initializedServerContactPoints = new ArrayList<ServerContactPoint>();

    private ScheduledFuture<?> connectionHealthCheckTaskHandle;

    private ServiceCallHandler serviceCallHandler;

    private NodePropertiesService nodePropertiesService;

    private ConnectionSetupService connectionSetupService;

    private long sessionStartTimeMsec;

    private boolean autoStartNetworkOnActivation = true; // disabled by integration tests

    private final Log log = LogFactory.getLog(getClass());

    @Override
    public synchronized void startUpNetwork() {

        sessionStartTimeMsec = System.currentTimeMillis();

        // add to local metadata
        Map<String, String> localMetadata = createLocalMetadataContribution();
        nodePropertiesService.addOrUpdateLocalNodeProperties(localMetadata);

        // start server contact points
        log.debug("Starting server contact points");
        for (NetworkContactPoint ncp : configurationService.getServerContactPoints()) {
            // log.debug(String.format("Virtual instance '%s': Starting server at %s",
            // ownNodeInformation.getLogName(), ncp));
            try {
                synchronized (initializedServerContactPoints) {
                    ServerContactPoint newSCP = connectionService.startServer(ncp);
                    initializedServerContactPoints.add(newSCP);
                }
            } catch (CommunicationException e) {
                log.warn("Error while starting server at " + ncp, e);
            }
        }
        // FIXME temporary fix until connection retry (or similar) is implemented;
        // without this, simultaneous startup of instance groups will usually fail,
        // because some instances will try to connect before others have fully started
        try {
            Thread.sleep(configurationService.getDelayBeforeStartupConnectAttempts());
        } catch (InterruptedException e1) {
            log.error("Interrupted while waiting during startup; not connecting to neighbors", e1);
            return;
        }

        connectionService.setShutdownFlag(false);

        // trigger connections to initial peers
        log.debug("Starting preconfigured connections");
        for (final NetworkContactPoint ncp : configurationService.getInitialNetworkContactPoints()) {
            // TODO add custom display name when available; move string reconstruction into NCP
            final String displayName = String.format("%s:%s", ncp.getHost(), ncp.getPort());
            boolean connectOnStartup = !"false".equals(ncp.getAttributes().get("connectOnStartup"));
            ConnectionSetup setup = connectionSetupService.createConnectionSetup(ncp, displayName, connectOnStartup);
            log.debug("Loaded pre-configured network connection \"%s\" (Settings: %s)" + setup.getDisplayName());
            if (setup.getConnnectOnStartup()) {
                setup.signalStartIntent();
            }
        }

        connectionHealthCheckTaskHandle = SharedThreadPool.getInstance().scheduleAtFixedRate(new Runnable() {

            @Override
            @TaskDescription("Connection health check (trigger task)")
            public void run() {
                try {
                    connectionService.triggerHealthCheckForAllChannels();
                } catch (RuntimeException e) {
                    log.error("Uncaught exception during connection health check", e);
                }
            }
        }, CommunicationConfiguration.CONNECTION_HEALTH_CHECK_INTERVAL_MSEC);
    }

    @Override
    @Deprecated
    public MessageChannel connectToRuntimePeer(NetworkContactPoint ncp) throws CommunicationException {
        Future<MessageChannel> future = connectionService.connect(ncp, true);
        try {
            return future.get();
        } catch (ExecutionException e) {
            throw new CommunicationException(e);
        } catch (InterruptedException e) {
            throw new CommunicationException(e);
        }
    }

    @Override
    @Deprecated
    public void asyncConnectToNetworkPeer(final NetworkContactPoint ncp) {
        SharedThreadPool.getInstance().execute(new Runnable() {

            @Override
            @TaskDescription("Connect to remote node (trigger task)")
            public void run() {
                try {
                    log.debug("Initiating asynchronous connection to " + ncp);
                    connectToRuntimePeer(ncp);
                } catch (CommunicationException e) {
                    log.warn("Failed to contact initial peer at NCP " + ncp, e);
                }
            }
        });
    }

    @Override
    public synchronized void shutDownNetwork() {
        connectionService.setShutdownFlag(true);

        connectionHealthCheckTaskHandle.cancel(true);

        // workaround for old tests that assume a network message on shutdown
        // TODO rework to proper solution
        nodePropertiesService.addOrUpdateLocalNodeProperty("state", "shutting down");

        // FIXME dirty hack until the shutdown LSA broadcast waits for a response or timeout itself;
        // without this, the asynchronous sending might not happen before the connections are closed
        // TODO wait for confirmations from all neighbors (with a short timeout) instead?
        try {
            Thread.sleep(DELAY_AFTER_SHUTDOWN_ANNOUNCE_MSEC);
        } catch (InterruptedException e) {
            log.warn("Interrupted while waiting", e);
        }

        // close outgoing connections
        connectionService.closeAllOutgoingChannels();

        // shut down server contact points
        synchronized (initializedServerContactPoints) {
            for (ServerContactPoint scp : initializedServerContactPoints) {
                // log.debug(String.format("Virtual instance '%s': Stopping server at %s",
                // ownNodeInformation.getLogName(), ncp));
                scp.shutDown();
            }
            initializedServerContactPoints.clear();
        }
    }

    @Override
    public void simulateUncleanShutdown() {
        // simulate crash of outgoing channels
        for (MessageChannel channel : connectionService.getAllOutgoingChannels()) {
            ((AbstractMessageChannel) channel).setSimulatingBreakdown(true);
        }
        connectionService.closeAllOutgoingChannels();
        // simulate crash of server contact points
        synchronized (initializedServerContactPoints) {
            for (ServerContactPoint scp : initializedServerContactPoints) {
                scp.setSimulatingBreakdown(true);
            }
            initializedServerContactPoints.clear();
        }
    }

    /**
     * OSGi-DS bind method; public for integration test access.
     * 
     * @param newService the service to bind
     */
    public void bindMessageChannelService(MessageChannelService newService) {
        // do not allow rebinding for now
        if (connectionService != null) {
            throw new IllegalStateException();
        }
        connectionService = newService;
    }

    /**
     * OSGi-DS bind method; public for integration test access.
     * 
     * @param newService the service to bind
     */
    public void bindNetworkRoutingService(NetworkRoutingService newService) {
        // do not allow rebinding for now
        if (networkRoutingService != null) {
            throw new IllegalStateException();
        }
        networkRoutingService = newService;
    }

    /**
     * OSGi-DS bind method; public for integration test access.
     * 
     * @param newService the service to bind
     */
    public void bindNodeConfigurationService(NodeConfigurationService newService) {
        // do not allow rebinding for now
        if (this.configurationService != null) {
            throw new IllegalStateException();
        }
        this.configurationService = newService;
    }

    /**
     * Define the {@link ServiceCallHandler} implementation to use for incoming RPC calls; made public for integration testing.
     * 
     * @param newInstance the {@link ServiceCallHandler} to use
     */
    public void bindServiceCallHandler(ServiceCallHandler newInstance) {
        serviceCallHandler = newInstance;
    }

    /**
     * OSGi-DS bind method; public for integration test access.
     * 
     * @param newInstance the new service instance to bind
     */
    public void bindNodePropertiesService(NodePropertiesService newInstance) {
        this.nodePropertiesService = newInstance;
    }

    /**
     * OSGi-DS bind method; public for integration test access.
     * 
     * @param newInstance the new service instance to bind
     */
    public void bindConnectionSetupService(ConnectionSetupService newInstance) {
        this.connectionSetupService = newInstance;
    }

    /**
     * OSGi-DS lifecycle method.
     */
    public void activate() {
        ownNodeInformation = configurationService.getInitialNodeInformation();
        NodeInformationRegistryImpl.getInstance().updateFrom(ownNodeInformation);

        MessageEndpointHandler messageEndpointHandler = new MessageEndpointHandlerImpl();
        messageEndpointHandler.registerRequestHandler(ProtocolConstants.VALUE_MESSAGE_TYPE_RPC, new RPCRequestHandler(serviceCallHandler));
        messageEndpointHandler.registerRequestHandler(ProtocolConstants.VALUE_MESSAGE_TYPE_HEALTH_CHECK, new HealthCheckRequestHandler());

        connectionService.setMessageEndpointHandler(messageEndpointHandler);

        // register LSA protocol handler
        // messageEndpointHandler.registerRequestHandlers(networkRoutingService.getProtocolManager().getNetworkRequestHandlers());

        // register metadata protocol handler
        messageEndpointHandler.registerRequestHandlers(nodePropertiesService.getNetworkRequestHandlers());

        if (autoStartNetworkOnActivation) { // default is true; only disabled in tests
            SharedThreadPool.getInstance().execute(new Runnable() {

                @Override
                @TaskDescription("Communication Layer Startup")
                public void run() {
                    startUpNetwork();
                }
            });
        }
    }

    /**
     * OSGi-DS lifecycle method.
     */
    public void deactivate() {}

    /**
     * Allows unit or integration tests to prevent {@link #startUpNetwork()} from being called automatically as part of the
     * {@link #activate()} method.
     * 
     * @param autoStartNetworkOnActivation the new value; default is "true"
     */
    public void setAutoStartNetworkOnActivation(boolean autoStartNetworkOnActivation) {
        this.autoStartNetworkOnActivation = autoStartNetworkOnActivation;
    }

    private Map<String, String> createLocalMetadataContribution() {
        Map<String, String> localData = new HashMap<String, String>();
        localData.put(NodePropertyConstants.KEY_NODE_ID, ownNodeInformation.getNodeIdString());
        localData.put(NodePropertyConstants.KEY_DISPLAY_NAME, ownNodeInformation.getDisplayName());
        localData.put(NodePropertyConstants.KEY_SESSION_START_TIME, Long.toString(sessionStartTimeMsec));
        // TODO @5.0: review: provide options to disable? - misc_ro
        localData.put("debug.sessionStartInfo",
            DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(new Date(sessionStartTimeMsec))); // temporary
        Version coreVersion = VersionUtils.getVersionOfCoreBundles();
        if (coreVersion != null) {
            localData.put("debug.coreVersion", coreVersion.toString());
        } else {
            localData.put("debug.coreVersion", "<unknown>");
        }
        localData.put("debug.osInfo", String.format("%s (%s/%s)",
            System.getProperty("os.name"), System.getProperty("os.version"), System.getProperty("os.arch")));
        localData.put("debug.isRelay", Boolean.toString(configurationService.isRelay()));
        return localData;
    }
}
