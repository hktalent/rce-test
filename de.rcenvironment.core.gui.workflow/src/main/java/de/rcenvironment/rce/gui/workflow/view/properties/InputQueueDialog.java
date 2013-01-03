/*
 * Copyright (C) 2006-2011 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */
 
package de.rcenvironment.rce.gui.workflow.view.properties;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;


/**
 * Dialog showing all values of an input.
 *
 * @author Doreen Seider
 */
public class InputQueueDialog extends Dialog {

    private static final int COLUMN_WIDTH_TYPE = 150;
    
    private TableViewer inputValuesTableViewer;
    
    private TableViewerColumn tableViewerColumn;
    
    private Button scrollLockButton;
    
    protected InputQueueDialog(Shell parentShell) {
        super(parentShell);
        setShellStyle(SWT.RESIZE | SWT.MAX);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        
        container.setLayout(new GridLayout());
        
        // create table viewer
        inputValuesTableViewer = new TableViewer(container, SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.MULTI | SWT.VIRTUAL);
        inputValuesTableViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        tableViewerColumn = new TableViewerColumn(inputValuesTableViewer, SWT.NONE);
        final TableColumn column = tableViewerColumn.getColumn();

        // set column properties
        column.setText(Messages.currentInput);
        column.setWidth(COLUMN_WIDTH_TYPE);
        column.setResizable(true);

        // set table layout data
        final Table table = inputValuesTableViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        scrollLockButton = new Button(container, SWT.CHECK);
        scrollLockButton.setText(Messages.scrollLock);
        scrollLockButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        
        return container;
    }
    
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
    }
    
    @Override
    protected Point getInitialSize() {
        final int width = 200;
        final int height = 300;
        return new Point(width, height);
    }
    
    public TableViewer getInputQueueTableViewer() {
        return inputValuesTableViewer;
    }
    
    public TableViewerColumn getInputQueueTableViewerColumn() {
        return tableViewerColumn;
    }
    
    public Button getScrollLockButton() {
        return scrollLockButton;
    }
}
