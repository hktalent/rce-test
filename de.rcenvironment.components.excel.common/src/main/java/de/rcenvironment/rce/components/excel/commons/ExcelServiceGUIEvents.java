/*
 * Copyright (C) 2006-2012 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */
 
package de.rcenvironment.rce.components.excel.commons;

import java.io.File;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.activeX.ActiveXInvocationProxy;

/**
 * Excel service GUI events interface for interaction with MS Excel GUI.
 * Implementations should be stateless (e. g., "static oriented") regarding usage in stateless services.
 *
 * @author Markus Kunde
 */
public interface ExcelServiceGUIEvents {

    /**
     * Open Microsoft Excel with window for user-interaction.
     * A listener for user-interaction can be registered.
     * 
     * ComThread.InitSTA() and ComThread.Release() must be done outside of this method.
     * 
     * @param xlFile Excel file
     * @param address Excel address range or null
     * @param listener listener for Excel GUI events
     * @return Excel application activeX object
     */
    ActiveXComponent openExcelApplicationRegisterListener(final File xlFile, final String address, final ActiveXInvocationProxy listener);
    
    
    /**
     * Quits Excel without saving and without question.
     * 
     * ComThread.InitSTA() and ComThread.Release() must be done outside of this method.
     * 
     * @param axc excel application
     * @param displayAlerts true if Excel application should ask the user if save excel file or not
     */
    void quitExcel(final ActiveXComponent axc, boolean displayAlerts);
    
}
