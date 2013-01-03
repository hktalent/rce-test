/*
 * Copyright (C) 2006-2010 DLR, Fraunhofer SCAI, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.rce.notification;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import de.rcenvironment.rce.communication.PlatformIdentifier;
import de.rcenvironment.rce.communication.PlatformIdentifierFactory;

/**
 * Test constants for this bundle tests.
 * 
 * @author Doreen Seider
 */
public final class NotificationTestConstants {

    /** Remote Host. */
    public static final PlatformIdentifier LOCALHOST = PlatformIdentifierFactory.fromHostAndNumber("localhost", 1);

    /** Local Host. */
    public static final PlatformIdentifier REMOTEHOST = PlatformIdentifierFactory.fromHostAndNumber("remote", 1);

    /** Local Host. */
    public static final PlatformIdentifier UNREACHABLE_HOST = PlatformIdentifierFactory.fromHostAndNumber("unreachable", 1);

    /** Notification identifier. */
    public static final String PERSISTENT_NOTIFICATION_ID = "de.rcenvironment.rce.notification.persistent";

    /** Notification identifier. */
    public static final String ANOTHER_PERSISTENT_NOTIFICATION_ID = "de.rcenvironment.rce.notification.persistent.another";

    /** Notification identifier. */
    public static final String PERSISTENT_NOTIFICATION_REGEX = "de.rcenvironment.rce.notification.pers.*";
    
    /** Notification identifier of a persistent notification. */
    public static final String NOTIFICATION_ID = "de.rcenvironment.rce.notification";

    /** Other publisher name. */
    public static final String OTHER_NOTIFICATION_IDENTIFIER = "de.rcenvironment.rce.notification.other";

    /** Subscriber identifier. */
    public static final String SUBSCRIBER_IDENTIFIER = "de.rcenvironment.rce.notification.subscriber";

    /** Publisher name. */
    public static final long NOTIFICATION_EDITION = 0;
    
    /** Notification header. */
    public static final NotificationHeader NOTIFICATION_HEADER = new NotificationHeader(NOTIFICATION_ID,
        NOTIFICATION_EDITION, LOCALHOST);

    /** Notification headers. */
    public static final SortedSet<NotificationHeader> NOTIFICATION_HEADERS = new TreeSet<NotificationHeader>();

    /** Notifications. */
    public static final List<Notification> NOTIFICATIONS = new ArrayList<Notification>();
    
    /** Notification subscriber. */
    public static final NotificationSubscriber NOTIFICATION_SUBSCRIBER = new DefaultNotificationSubscriber() {
        
        private static final long serialVersionUID = 1L;

        @Override
        public void notify(Notification notification) {
        }

        @Override
        public Class<? extends Serializable> getInterface() {
            return NotificationSubscriber.class;
        }
    };

    /** Notification. */
    public static final Notification NOTIFICATION = new Notification(NOTIFICATION_ID,
        NOTIFICATION_EDITION, LOCALHOST, new String());

    /**
     * Private constructor of this utility class.
     */
    private NotificationTestConstants() {

    }

}
