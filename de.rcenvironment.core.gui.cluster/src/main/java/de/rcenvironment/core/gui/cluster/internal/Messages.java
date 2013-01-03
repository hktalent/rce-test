/*
 * Copyright (C) 2006-2012 DLR Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.gui.cluster.internal;

import org.eclipse.osgi.util.NLS;

/**
 * I18N.
 *
 * @author Doreen Seider
 */
public class Messages extends NLS {
    
    /** Constant. */
    public static String ok;
    
    /** Constant. */
    public static String wrongQueuingDialogTitle;

    /** Constant. */
    public static String wrongQueuingDialogMessage;

    /** Constant. */
    public static String connectionFailureDialogTitle;

    /** Constant. */
    public static String connectionFailureDialogMessage;

    /** Constant. */
    public static String fetchingFailureDialogTitle;

    /** Constant. */
    public static String fetchingFailureDialogMessage;

    /** Constant. */
    public static String readingConfigurationsFailureDialogTitle;

    /** Constant. */
    public static String readingConfigurationsFailureDialogMessage;

    /** Constant. */
    public static String storingConfigurationFailureDialogTitle;

    /** Constant. */
    public static String storingConfigurationFailureDialogMessage;
    
    /** Constant. */
    public static String cancelingJobsFailureDialogTitle;

    /** Constant. */
    public static String cancelingJobsFailureDialogMessage;

    private static final String BUNDLE_NAME = "de.rcenvironment.core.gui.cluster.internal.messages"; //$NON-NLS-1$
    
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
}
