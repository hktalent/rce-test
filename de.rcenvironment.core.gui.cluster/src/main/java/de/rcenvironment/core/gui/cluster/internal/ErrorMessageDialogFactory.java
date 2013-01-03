/*
 * Copyright (C) 2006-2012 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */
 
package de.rcenvironment.core.gui.cluster.internal;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;


/**
 * Creates error messages for specified error handling scenarios which might occur.
 *
 * @author Doreen Seider
 */
public final class ErrorMessageDialogFactory {

    private ErrorMessageDialogFactory() {}
    
    /**
     * Create a {@link MessageDialog}.
     * @param parent parent shell
     * @return {@link MessageDialog}
     */
    public static MessageDialog createMessageDialogForWrongQueuingSystem(Composite parent) {
        MessageDialog dialog = new MessageDialog(parent.getShell(), Messages.wrongQueuingDialogTitle, null,
            Messages.wrongQueuingDialogMessage, MessageDialog.ERROR, new String[] { Messages.ok }, 0);
        return dialog;
    }
    
    /**
     * Create a {@link MessageDialog}.
     * @param parent parent shell
     * @return {@link MessageDialog}
     */
    public static MessageDialog createMessageDialogForConnectionFailure(Composite parent) {
        MessageDialog dialog = new MessageDialog(parent.getShell(), Messages.connectionFailureDialogTitle, null,
            Messages.connectionFailureDialogMessage, MessageDialog.ERROR, new String[] { Messages.ok }, 0);
        return dialog;
    }

    /**
     * Create a {@link MessageDialog}.
     * @param parent parent shell
     * @return {@link MessageDialog}
     */
    public static MessageDialog createMessageDialogForFetchingFailure(Composite parent) {
        MessageDialog dialog = new MessageDialog(parent.getShell(), Messages.fetchingFailureDialogTitle, null,
            Messages.fetchingFailureDialogMessage, MessageDialog.ERROR, new String[] { Messages.ok }, 0);
        return dialog;
    }
    
    /**
     * Create a {@link MessageDialog}.
     * @param parent parent shell
     * @param errorMessage standard error of cancel request command
     * @return {@link MessageDialog}
     */
    public static MessageDialog createMessageDialogForCancelingJobsFailure(Composite parent, String errorMessage) {
        MessageDialog dialog = new MessageDialog(parent.getShell(), Messages.cancelingJobsFailureDialogTitle, null,
            String.format(Messages.cancelingJobsFailureDialogMessage, errorMessage), MessageDialog.ERROR, new String[] { Messages.ok }, 0);
        return dialog;
    }
    
    /**
     * Create a {@link MessageDialog}.
     * @param parent parent shell
     * @return {@link MessageDialog}
     */
    public static MessageDialog createMessageDialogForReadingConfigurationsFailure(Composite parent) {
        MessageDialog dialog = new MessageDialog(parent.getShell(), Messages.readingConfigurationsFailureDialogTitle, null,
            Messages.readingConfigurationsFailureDialogMessage, MessageDialog.ERROR, new String[] { Messages.ok }, 0);
        return dialog;
    }
    
    /**
     * Create a {@link MessageDialog}.
     * @param parent parent shell
     * @return {@link MessageDialog}
     */
    public static MessageDialog createMessageDialogForStoringConfigurationFailure(Composite parent) {
        MessageDialog dialog = new MessageDialog(parent.getShell(), Messages.storingConfigurationFailureDialogTitle, null,
            Messages.storingConfigurationFailureDialogMessage, MessageDialog.ERROR, new String[] { Messages.ok }, 0);
        return dialog;
    }

}
