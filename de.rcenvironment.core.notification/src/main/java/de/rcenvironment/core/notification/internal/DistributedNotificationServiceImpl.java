/*
 * Copyright (C) 2006-2014 DLR, Germany, 2006-2010 Fraunhofer SCAI, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.notification.internal;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;

import de.rcenvironment.core.communication.api.CommunicationService;
import de.rcenvironment.core.communication.common.NodeIdentifier;
import de.rcenvironment.core.notification.DistributedNotificationService;
import de.rcenvironment.core.notification.Notification;
import de.rcenvironment.core.notification.NotificationHeader;
import de.rcenvironment.core.notification.NotificationService;
import de.rcenvironment.core.notification.NotificationSubscriber;
import de.rcenvironment.core.utils.common.ServiceUtils;
import de.rcenvironment.core.utils.common.concurrent.AsyncExceptionListener;
import de.rcenvironment.core.utils.common.concurrent.CallablesGroup;
import de.rcenvironment.core.utils.common.concurrent.SharedThreadPool;
import de.rcenvironment.core.utils.common.concurrent.TaskDescription;

/**
 * Implementation of {@link DistributedNotificationService}.
 * 
 * @author Doreen Seider
 * 
 */
// FIXME clarify behavior on failure: return null, empty collections or throw exceptions? -- misc_ro
// (see related Mantis issue #6542)
public class DistributedNotificationServiceImpl implements DistributedNotificationService {

    private static final Log LOGGER = LogFactory.getLog(DistributedNotificationServiceImpl.class);

    private static NotificationService notificationService;

    private static CommunicationService nullCommunicationService = ServiceUtils.createFailingServiceProxy(CommunicationService.class);

    private static CommunicationService communicationService = nullCommunicationService;

    private static BundleContext context;

    protected void activate(BundleContext bundleContext) {
        context = bundleContext;
    }

    protected void bindNotificationService(NotificationService newNotificationService) {
        notificationService = newNotificationService;
    }

    protected void bindCommunicationService(CommunicationService newCommunicationService) {
        communicationService = newCommunicationService;
    }

    @Override
    public void setBufferSize(String notificationId, int bufferSize) {
        notificationService.setBufferSize(notificationId, bufferSize);
    }

    @Override
    public void removePublisher(String notificationId) {
        notificationService.removePublisher(notificationId);
    }

    @Override
    public <T extends Serializable> void send(String notificationId, T notificationBody) {
        notificationService.send(notificationId, notificationBody);
    }

    @Override
    public Map<String, Long> subscribe(String notificationId, NotificationSubscriber subscriber,
        NodeIdentifier publishPlatform) {
        try {
            Pattern.compile(notificationId);
        } catch (RuntimeException e) {
            LOGGER.error("Notification Id is not a valid RegExp: " + notificationId, e);
            throw e;
        }
        try {
            return ((NotificationService) communicationService.getService(NotificationService.class, publishPlatform, context))
                .subscribe(notificationId, subscriber);
        } catch (RuntimeException e) {
            String message = MessageFormat.format("Failed to subscribe to publisher @{0}: ", publishPlatform);
            LOGGER.error(message, e);
            // TODO use better exception type here? - misc_ro
            throw new IllegalStateException(message, e);
        }
    }

    @Override
    public Map<NodeIdentifier, Map<String, Long>> subscribeToAllReachableNodes(final String notificationId,
        final NotificationSubscriber subscriber) {

        final Map<NodeIdentifier, Map<String, Long>> missedNumbersMap =
            Collections.synchronizedMap(new HashMap<NodeIdentifier, Map<String, Long>>());

        // do not filter by "workflow host" flag for now, as components may send out
        // notifications from nodes that are not workflow hosts - misc_ro, July 2013
        Set<NodeIdentifier> nodesToSubscribeTo = communicationService.getReachableNodes();

        // create the parallel subscription tasks; no return value as results are added to the map
        CallablesGroup<Void> callables = SharedThreadPool.getInstance().createCallablesGroup(Void.class);
        for (final NodeIdentifier nodeId : nodesToSubscribeTo) {
            callables.add(new Callable<Void>() {

                @Override
                @TaskDescription("Distributed notification subscription")
                public Void call() throws Exception {
                    Map<String, Long> missedNumbers = subscribe(notificationId, subscriber, nodeId);
                    missedNumbersMap.put(nodeId, missedNumbers);
                    return (Void) null;
                }
            });
        }
        callables.executeParallel(new AsyncExceptionListener() {

            @Override
            public void onAsyncException(Exception e) {
                LOGGER.error("Asynchronous exception while subscribing for notification " + notificationId);
            }
        });

        return missedNumbersMap;
    }

    @Override
    public void unsubscribe(String notificationId, NotificationSubscriber subscriber, NodeIdentifier publishPlatform) {
        try {
            ((NotificationService) communicationService.getService(NotificationService.class, publishPlatform, context))
                .unsubscribe(notificationId, subscriber);
        } catch (RuntimeException e) {
            LOGGER.error(MessageFormat.format("Failed to unsubscribe from remote publisher @{0}: ", publishPlatform), e);
        }
    }

    @Override
    public Map<String, SortedSet<NotificationHeader>> getNotificationHeaders(String notificationId, NodeIdentifier publishPlatform) {
        try {
            return ((NotificationService) communicationService.getService(NotificationService.class, publishPlatform, context))
                .getNotificationHeaders(notificationId);
        } catch (RuntimeException e) {
            String message = MessageFormat.format("Failed to get remote notification headers @{0}: ", publishPlatform);
            LOGGER.error(message, e);
            throw new IllegalStateException(message, e);
        }
    }

    @Override
    public Map<String, List<Notification>> getNotifications(String notificationId, NodeIdentifier publishPlatform) {
        try {
            return ((NotificationService) communicationService.getService(NotificationService.class, publishPlatform, context))
                .getNotifications(notificationId);
        } catch (RuntimeException e) {
            String message = MessageFormat.format("Failed to get remote notifications @{0}: ", publishPlatform);
            LOGGER.error(message, e);
            throw new IllegalStateException(message, e);
        }
    }

    @Override
    public Notification getNotification(NotificationHeader header) {
        try {
            return ((NotificationService) communicationService
                .getService(NotificationService.class, header.getPublishPlatform(), context)).getNotification(header);
        } catch (RuntimeException e) {
            String message = MessageFormat.format("Failed to get remote notification @{0}: ", header.getPublishPlatform());
            LOGGER.error(message, e);
            throw new IllegalStateException(message, e);
        }
    }
}
