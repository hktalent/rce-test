/*
 * Copyright (C) 2006-2014 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */
package de.rcenvironment.components.cpacs.vampzeroinitializer.gui;

import java.io.File;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * Abstract super class to abstract over stand-alone SWT and Eclipse-SWT widget generation.
 * 
 * @author Arne Bachmann
 * @author Markus Kunde
 */
public abstract class AbstractSwtHelper {

    private static final String ICONS_FILE = "resources/icons";

    /**
     * Icon for revert button.
     */
    protected Image revertImage;

    /**
     * The apply image.
     */
    protected Image okImage;

    /**
     * The save image.
     */
    protected Image saveImage;

    /**
     * Initialize necessary resources.
     * 
     * @param display The parent display
     */
    protected AbstractSwtHelper(final Display display) {
        
        try {
            revertImage = new Image(display, getClass().getClassLoader().getResourceAsStream("resources/icons/undo_edit.gif"));
            okImage = new Image(display, getClass().getClassLoader().getResourceAsStream("resources/icons/ok.png"));
            saveImage = new Image(display, getClass().getClassLoader().getResourceAsStream("resources/icons/save_edit.gif"));
        } catch (final IllegalArgumentException e) {
            revertImage = new Image(display, new File(ICONS_FILE, "undo_edit.gif").getAbsolutePath());
            okImage = new Image(display, new File(ICONS_FILE, "ok.png").getAbsolutePath());
            saveImage = new Image(display, new File(ICONS_FILE, "save_edit.gif").getAbsolutePath());
        }
    }

    /**
     * Clean up.
     */
    protected void dispose() {
        revertImage.dispose();
        okImage.dispose();
        saveImage.dispose();
    }

    /**
     * Helper to dispose composites.
     * 
     * @param composite The composite to dispose
     */
    public void disposeRecursively(final Composite composite) {
        for (final Control c : composite.getChildren()) {
            if (c instanceof Composite) {
                disposeRecursively((Composite) c);
            }
            c.dispose();
        }
    }

    /**
     * Currently nothing. Subclasses may override.
     */
    protected void refresh() {}

    /**
     * Create a main composite.
     * 
     * @return The composite
     */
    public abstract Composite createMainComposite();

    /**
     * Create a sub-composite.
     * 
     * @param parent The parent
     * @return The composite
     */
    public abstract Composite createComposite(final Composite parent);

    /**
     * Create a sub-composite with n columns.
     * 
     * @param parent The parent
     * @param columnsContained Layout columns
     * @param columns Column spread of widget
     * @return The widget
     */
    public abstract Composite createComposite(final Composite parent, final int columnsContained, int... columns);

    /**
     * Create a separator label with n columns.
     * 
     * @param parent The parent
     * @param vertical Direction
     * @param columns Column spread of widget
     * @return The widget
     */
    public abstract Label createSeparator(final Composite parent, final boolean vertical, final int... columns);

    /**
     * Create a text with n columns.
     * 
     * @param parent The parent
     * @param initialText Text to create
     * @param colSpan Column spread of widget
     * @return The widget
     */
    public abstract Text createText(final Composite parent, final String initialText, final int... colSpan);

    /**
     * Create a button with n columns.
     * 
     * @param parent The parent
     * @param text Text to create
     * @param listener The listener to notify when clicked
     * @param alignment Alignment
     * @return The widget
     */
    public abstract Button createButton(final Composite parent, final String text, final Listener listener, final int... alignment);

    /**
     * Create a label with n columns.
     * 
     * @param parent The parent
     * @param text Label text
     * @param columns Column spread of widget
     * @return The widget
     */
    public abstract Label createLabel(final Composite parent, final String text, int... columns);

    /**
     * Create a drop-down read-only combo box.
     * 
     * @param parent The parent
     * @param initialTexts Texts to show
     * @param listener The listener to notify upon selection changes
     * @return The widget
     */
    public abstract Combo createCombo(final Composite parent, final String[] initialTexts, final Listener listener);

    /**
     * Create a default layout (TableWrapLayout).
     * 
     * @param columnsContained No of columns contained
     * 
     * @return The layout
     */
    public abstract Layout createDefaultLayout(final int columnsContained);

}
