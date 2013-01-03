/*
 * Copyright (C) 2006-2011 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */
 
package de.rcenvironment.rce.components.sql.gui.properties;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.rcenvironment.rce.components.sql.commons.InputMapping;
import de.rcenvironment.rce.components.sql.commons.InputMapping.ColumnMapping;


/**
 * Content provider for input mapping.
 *
 * @author Christian Weiss
 */
public class InputMappingContentProvider implements IStructuredContentProvider {

    @Override
    public Object[] getElements(final Object inputElement) {
        if (!(inputElement instanceof InputMapping)) {
            throw new IllegalArgumentException("Input must be a valid InputMapping instance.");
        }
        final InputMapping inputMapping = (InputMapping) inputElement;
        final ColumnMapping[] columnMappings = inputMapping.getColumnMappingsArray();
        return columnMappings;
    }

    @Override
    public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
        if (newInput != null && !(newInput instanceof InputMapping)) {
            throw new IllegalArgumentException("Input must be a valid InputMapping instance.");
        }
    }

    @Override
    public void dispose() {
        // do nothing
    }

}
