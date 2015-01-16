/*
 * Copyright (C) 2006-2014 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.communication.connection.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.rcenvironment.core.communication.channel.MessageChannelLifecycleListener;
import de.rcenvironment.core.communication.channel.MessageChannelLifecycleListenerAdapter;
import de.rcenvironment.core.communication.channel.MessageChannelService;
import de.rcenvironment.core.communication.connection.api.ConnectionSetup;
import de.rcenvironment.core.communication.connection.api.ConnectionSetupListener;
import de.rcenvironment.core.communication.connection.api.ConnectionSetupListenerAdapter;
import de.rcenvironment.core.communication.connection.api.ConnectionSetupService;
import de.rcenvironment.core.communication.connection.api.ConnectionSetupState;
import de.rcenvironment.core.communication.connection.api.DisconnectReason;
import de.rcenvironment.core.communication.model.MessageChannel;
import de.rcenvironment.core.communication.model.NetworkContactPoint;
import de.rcenvironment.core.utils.common.concurrent.AsyncCallback;
import de.rcenvironment.core.utils.common.concurrent.AsyncCallbackExceptionPolicy;
import de.rcenvironment.core.utils.common.concurrent.AsyncOrderedCallbackManager;
import de.rcenvironment.core.utils.common.concurrent.SharedThreadPool;
import de.rcenvironment.core.utils.incubator.ListenerDeclaration;
import de.rcenvironment.core.utils.incubator.ListenerProvider;

/**
 * Default {@link ConnectionSetupService} implementation.
 * 
 * @author Robert Mischke
 */
public class ConnectionSetupServiceImpl implements ConnectionSetupService, ListenerProvider {

    private MessageChannelService messageChannelService;

    private final List<ConnectionSetup> setups = new ArrayList<ConnectionSetup>();

    // sequential id generator
    private final AtomicLong lastSetupId = new AtomicLong();

    private final AsyncOrderedCallbackManager<ConnectionSetupListener> callbackManager =
        new AsyncOrderedCallbackManager<ConnectionSetupListener>(SharedThreadPool.getInstance(),
            AsyncCallbackExceptionPolicy.LOG_AND_CANCEL_LISTENER);

    private final Log log = LogFactory.getLog(getClass());

    @Override
    public Collection<ListenerDeclaration> defineListeners() {
        List<ListenerDeclaration> result = new ArrayList<ListenerDeclaration>();
        result.add(new ListenerDeclaration(MessageChannelLifecycleListener.class, new MessageChannelLifecycleListenerAdapter() {

            @Override
            public void onOutgoingChannelTerminated(MessageChannel connection) {
                handleOutgoingChannelTerminated(connection);
            }
        }));
        return result;
    }

    @Override
    public ConnectionSetup createConnectionSetup(NetworkContactPoint ncp, String displayName, boolean connnectOnStartup) {
        // create minimal adapter to delegate/forward state changes to all listeners
        ConnectionSetupListenerAdapter stateChangeListenerAdapter = new ConnectionSetupListenerAdapter() {

            @Override
            public void onStateChanged(final ConnectionSetup setup, final ConnectionSetupState oldState,
                final ConnectionSetupState newState) {
                callbackManager.enqueueCallback(new AsyncCallback<ConnectionSetupListener>() {

                    @Override
                    public void performCallback(ConnectionSetupListener listener) {
                        listener.onStateChanged(setup, oldState, newState);
                    }
                });
            }

            @Override
            public void onConnectionAttemptFailed(final ConnectionSetup setup, final boolean firstConsecutiveFailure,
                final boolean willAutoRetry) {
                callbackManager.enqueueCallback(new AsyncCallback<ConnectionSetupListener>() {

                    @Override
                    public void performCallback(ConnectionSetupListener listener) {
                        listener.onConnectionAttemptFailed(setup, firstConsecutiveFailure, willAutoRetry);
                    }
                });
            }

            @Override
            public void onConnectionClosed(final ConnectionSetup setup, final DisconnectReason disconnectReason,
                final boolean willAutoRetry) {
                callbackManager.enqueueCallback(new AsyncCallback<ConnectionSetupListener>() {

                    @Override
                    public void performCallback(ConnectionSetupListener listener) {
                        listener.onConnectionClosed(setup, disconnectReason, willAutoRetry);
                    }
                });
            }
        };

        long id = lastSetupId.incrementAndGet();
        final ConnectionSetupImpl newSetup =
            new ConnectionSetupImpl(ncp, displayName, id, connnectOnStartup, messageChannelService, stateChangeListenerAdapter);

        final List<ConnectionSetup> snapshotOfCollection;
        synchronized (setups) {
            setups.add(newSetup);
            // create a detached snapshot to guard against race conditions
            snapshotOfCollection = createDetachedSnapshotOfCollection();

            // trigger callback about new ConnectionSetup
            callbackManager.enqueueCallback(new AsyncCallback<ConnectionSetupListener>() {

                @Override
                public void performCallback(ConnectionSetupListener listener) {
                    listener.onCreated(newSetup);
                    listener.onCollectionChanged(snapshotOfCollection);
                }
            });
        }

        return newSetup;
    }

    @Override
    public void disposeConnectionSetup(final ConnectionSetup setup) {

        setup.signalStopIntent();

        final List<ConnectionSetup> snapshotOfCollection;
        synchronized (setups) {
            boolean wasContained = setups.remove(setup);
            if (!wasContained) {
                log.warn("Connection setup '" + setup.getDisplayName() + "' was not found in the registry when dispose() was called");
            }
            // create a detached snapshot to guard against race conditions
            snapshotOfCollection = createDetachedSnapshotOfCollection();

            callbackManager.enqueueCallback(new AsyncCallback<ConnectionSetupListener>() {

                @Override
                public void performCallback(ConnectionSetupListener listener) {
                    listener.onDisposed(setup);
                    listener.onCollectionChanged(snapshotOfCollection);
                }
            });
        }
    }

    @Override
    public Collection<ConnectionSetup> getAllConnectionSetups() {
        synchronized (setups) {
            return createDetachedSnapshotOfCollection();
        }
    }

    @Override
    public ConnectionSetup getConnectionSetupById(long id) {
        // small list -> simple search
        for (ConnectionSetup setup : setups) {
            if (setup.getId() == id) {
                return setup;
            }
        }
        return null;
    }

    /**
     * Registers a {@link ConnectionSetupListener}.
     * 
     * @param listener the new listener
     */
    public void addConnectionSetupListener(ConnectionSetupListener listener) {
        final List<ConnectionSetup> snapshotOfCollection;
        synchronized (setups) {
            // create a detached snapshot to guard against race conditions
            snapshotOfCollection = createDetachedSnapshotOfCollection();
            callbackManager.addListenerAndEnqueueCallback(listener, new AsyncCallback<ConnectionSetupListener>() {

                @Override
                public void performCallback(ConnectionSetupListener listener) {
                    // perform initial callback
                    listener.onCollectionChanged(snapshotOfCollection);
                }
            });
        }
    }

    /**
     * Unregisters a {@link ConnectionSetupListener}.
     * 
     * @param listener the listener to remove
     */
    public void removeConnectionSetupListener(ConnectionSetupListener listener) {
        callbackManager.removeListener(listener);
    }

    /**
     * OSGi-DS bind method; public for integration test access.
     * 
     * @param newInstance the service to bind
     */
    public void bindMessageChannelService(MessageChannelService newInstance) {
        messageChannelService = newInstance;
    }

    private void handleOutgoingChannelTerminated(MessageChannel messageChannel) {
        ConnectionSetupImpl matchedSetup = null;
        String channelId = messageChannel.getChannelId();
        synchronized (setups) {
            for (ConnectionSetup setup : setups) {
                // note: race conditions are still possible if the channel was established and disconnected right away, although unlikely -
                // misc_ro
                if (channelId.equals(setup.getLastChannelId())) {
                    matchedSetup = (ConnectionSetupImpl) setup;
                    break;
                }
            }
        }

        if (messageChannel.getInitiatedByRemote()) {
            // consistency check: remote-initiated connections should never have a connection setup
            if (matchedSetup != null) {
                log.error("Internal consistency error: Remote-initiated message channel had an associated connection setup: " + channelId);
            }
            return;
        }

        if (matchedSetup != null) {
            matchedSetup.onMessageChannelClosed(messageChannel);
        } else {
            // unexpected state: self-initiated connection without a connection setup
            log.warn("Self-initiated message channel closed, but no associated connection setup found: " + channelId);
            // if this actually happens, retrying after a moment should fix it
            return;
        }
    }

    private List<ConnectionSetup> createDetachedSnapshotOfCollection() {
        // note: should only be called while synchronizing on "setups"
        return Collections.unmodifiableList(new ArrayList<ConnectionSetup>(setups));
    }

}
