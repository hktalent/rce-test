/*
 * Copyright (C) 2006-2014 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */
package de.rcenvironment.components.inputprovider.gui;

import org.eclipse.osgi.util.NLS;


/**
 * Supports language specific messages.
 * 
 * @author Sascha Zur
 * @author Mark Geiger
 */
public class Messages extends NLS {

    /** Constant.  */
    public static String chooseAtWorkflowStart;

    /** Constant.  */
    public static String value;

    /** Constant.  */
    public static String file;

    /** Constant.  */
    public static String outputs;

    /** Constant.  */
    public static String newOutput;

    /** Constant.  */
    public static String edit;

    /** Constant.  */
    public static String selectFromProject;

    /** Constant.  */
    public static String select;

    /** Constant.  */
    public static String selectFromFileSystem;

    /** Constant.  */
    public static String selectFileFromProject;
    
    /** Constant.  */
    public static String selectFile;

    /** Constant.  */
    public static String selectDirectoryFromFileSystem;
    
    /** Constant.  */
    public static String selectDirectoryFromProject;

    /** Constant.  */
    public static String selectDirectory;
    
    /** Constant.  */
    public static String note;

    private static final String BUNDLE_NAME = Messages.class.getPackage().getName() + ".messages";

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
}
