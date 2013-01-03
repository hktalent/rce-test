/*
 * Copyright (C) 2006-2010 DLR, Fraunhofer SCAI, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.gui.commons.configuration;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import de.rcenvironment.gui.commons.configuration.ConfigurationViewerContentProvider.Element;

/**
 * The {@link ITableLabelProvider} for a {@link ConfigurationViewer}.
 * 
 * @author Christian Weiss
 */
public class ConfigurationViewerLabelProvider implements ITableLabelProvider {

    private final List<ILabelProviderListener> listeners = new ArrayList<ILabelProviderListener>();

    @Override
    public void dispose() {
        //
    }

    @Override
    public boolean isLabelProperty(final Object element, final String property) {
        return false;
    }

    @Override
    public Image getColumnImage(final Object element, final int columnIndex) {
        return null;
    }

    @Override
    public String getColumnText(final Object element, final int columnIndex) {
        final String result;
        if (element instanceof Element) {
            final Element treeElement = (Element) element;
            switch (columnIndex) {
            case 0:
                result = treeElement.getDisplayName();
                break;
            case 1:
                final String displayValue = treeElement.getDisplayValue();
                if (displayValue != null) {
                    result = displayValue;
                } else {
                    result = "";
                }
                break;
            default:
                throw new AssertionError();
            }
        } else {
            result = null;
        }
        return result;
    }

    @Override
    public void addListener(final ILabelProviderListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(final ILabelProviderListener listener) {
        listeners.remove(listener);
    }

}
