/*
 * Copyright (C) 2006-2010 DLR, Fraunhofer SCAI, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.rce.gui.workflow.execute;

import org.eclipse.osgi.util.NLS;

/**
 * Supports language specific messages.
 *
 * @author Christian Weiss
 */
public final class Messages extends NLS {

    /**
     * Constant.
     */
    public static String workflowPageName;

    /**
     * Constant.
     */
    public static String workflowPageTitle;

    /**
     * Constant.
     */
    public static String workflowColon;

    /**
     * Constant.
     */
    public static String controlTP;

    /**
     * Constant.
     */
    public static String componentsTP;

    /**
     * Constant.
     */
    public static String illegalExecutionSelectionTitle = null;

    /**
     * Constant.
     */
    public static String illegalExecutionSelectionMessage = null;

    /**
     * Constant.
     */
    public static String workflowExecutionWizardTitle;

    /**
     * Constant.
     */
    public static String localPlatformSelectionTitle;

    /**
     * Constant.
     */
    public static String localPlatformExplicitSelectionTitle;

    /**
     * Constant.
     */
    public static String component;

    /**
     * Constant.
     */
    public static String targetPlatform;

    /**
     * Constant.
     */
    public static String nameGroupTitle;

    /**
     * Constant.
     */
    public static String defaultWorkflowName;

    /**
     * Constant.
     */
    public static String platformSelectionValueIsSuggestionToolTip;

    /**
     * Constant.
     */
    public static String askToSaveUnsavedEditorChangesTitle;

    /**
     * Constant.
     */
    public static String askToSaveUnsavedEditorChangesMessage;

    /**
     * Constant.
     */
    public static String workflowLaunchFailed;

    /**
     * Constant.
     */
    public static String workflowSaveFailed;

    /**
     * Constant.
     */
    public static String executionWizardFinishButtonLabel;

    /**
     * Constant.
     */
    public static String illegalConfigTitle;

    /**
     * Constant.
     */
    public static String illegalConfigMessage;
    
    /** Constant. */
    public static String setupWorkflow;
    
    /** Constant. */
    public static String placeholderInformationHeader;
    
    /** Constant. */
    public static String clear;
    
    /** Constant. */
    public static String clearHistoryDialogTitle;
    
    /** Constant. */
    public static String clearHistoryButton;
    
    /** Constant. */
    public static String applyToAll;

    private static final String BUNDLE_NAME = Messages.class.getPackage().getName() + ".messages"; //$NON-NLS-1$

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
}
