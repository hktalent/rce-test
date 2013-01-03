/*
 * Copyright (C) 2006-2010 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */
 
package de.rcenvironment.rce.components.excel.gui.view;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;

import de.rcenvironment.commons.excel.ExcelFileExporter;
import de.rcenvironment.rce.component.ComponentInstanceDescriptor;
import de.rcenvironment.rce.components.excel.commons.ChannelValue;
import de.rcenvironment.rce.components.excel.commons.SimpleExcelService;
import de.rcenvironment.rce.gui.workflow.view.ComponentRuntimeView;

/**
 * View of Excel component during run of workflow.
 *
 * @author Markus Kunde
 */
public class ExcelView extends ViewPart implements ComponentRuntimeView, Observer {

    /**
     * File Type of Excel-files.
     */
    private static final String FILETYPENAME = "Excel";
    
    /**
     * Logger.
     */
    private static final Log LOGGER = LogFactory.getLog(ExcelView.class);
    
    
    /**
     * Data model of channel values.
     */
    private ModelProvider model;

    /**
     * Component information of this component view.
     */
    private ComponentInstanceDescriptor componentInformation = null;
    
    /**
     * Parent composite.
     */
    private Composite parentComposite = null;
    
    /**
     * The form for the widgets.
     */
    private ScrolledForm form = null;
    
    
    /**
     * TableViewer for input channel values.
     */
    private TableViewer inputTableViewer = null;
    
    /**
     * TableViewer for output channel values.
     */
    private TableViewer outputTableViewer = null;
    
    /**
     * TableSorter.
     */
    private TableSorter tableSorter = null;
    
    /**
     * Filter in table for input channel type.
     */
    private ChannelFilter inputChannelFilter = null;
    
    /**
     * Filter in table for output channel type.
     */
    private ChannelFilter outputChannelFilter = null;
    
    /**
     * SelectionProvider for multiple selection providers.
     */
    private SelectionProviderIntermediate spi = null;
    
    
    /**
     * Copy to clipboard action.
     */
    private Action copyClipboardAction = null;
    
    /**
     * Export to Excel action.
     */
    private Action exportToExcel = null;
        
    /**
     * Constructor.
     * 
     */
    public ExcelView() {
        super();
        model = new ModelProvider();
        model.addObserver(this);
        inputChannelFilter = new ChannelFilter();
        inputChannelFilter.setChannelFilter(true);
        outputChannelFilter = new ChannelFilter();
        outputChannelFilter.setChannelFilter(false);
        spi = new SelectionProviderIntermediate();
    }
    

    @Override
    public void createPartControl(final Composite parent) {
        parentComposite = parent;
        final FormToolkit tk = new FormToolkit(parentComposite.getDisplay());
        form = tk.createScrolledForm(parentComposite);
        form.setText(Messages.viewName);
        if (componentInformation != null) {
            form.setText(Messages.viewName + componentInformation.getName());
        }
        
        parentComposite = form.getBody();
        
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        gridLayout.makeColumnsEqualWidth = true;
        gridLayout.horizontalSpacing = ExcelViewConstants.NORMALCOLUMNWIDTH/2;        
        parentComposite.setLayout(gridLayout);

        createTableViewer(parentComposite);
        
        tableSorter = new TableSorter();
        inputTableViewer.setInput(model.getChannelValues());
        inputTableViewer.setSorter(tableSorter);
        inputTableViewer.addFilter(inputChannelFilter);
        
        outputTableViewer.setInput(model.getChannelValues());
        outputTableViewer.setSorter(tableSorter);
        outputTableViewer.addFilter(outputChannelFilter);
        
        
        getSite().setSelectionProvider(spi);
        
        createActions();
        createToolbar();
        
    }
 
   
    /**
     * Create table viewer.
     * 
     * @param parent parent composite
     */
    private void createTableViewer(final Composite parent) {
        inputTableViewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
        createColumns(parent, inputTableViewer);
        inputTableViewer.setContentProvider(new ChannelValueContentProvider());
        inputTableViewer.setLabelProvider(new ChannelValueLabelProvider());
        inputTableViewer.getTable().setToolTipText(Messages.inputChannelNameType);
        inputTableViewer.addDoubleClickListener(new DoubleClickListener(parentComposite));

        
        outputTableViewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
        createColumns(parent, outputTableViewer);
        outputTableViewer.setContentProvider(new ChannelValueContentProvider());
        outputTableViewer.setLabelProvider(new ChannelValueLabelProvider());
        outputTableViewer.getTable().setToolTipText(Messages.outputChannelNameType);
        outputTableViewer.addDoubleClickListener(new DoubleClickListener(parentComposite));
        
        GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
        inputTableViewer.getTable().setLayoutData(gridData);
        
        gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
        outputTableViewer.getTable().setLayoutData(gridData);
    }

    
    /**
     * Create columns in table.
     */
    private void createColumns(final Composite parent, final TableViewer tv) {
        final Menu headerMenu = new Menu(parent);
        String[] titles = {Messages.valueColumnName, 
                           Messages.channelColumnName,
                           Messages.valueColumnIteration};
        int[] bounds = {ExcelViewConstants.NORMALCOLUMNWIDTH, 
                        ExcelViewConstants.NORMALCOLUMNWIDTH, 
                        ExcelViewConstants.NORMALCOLUMNWIDTH / 2};

        for (int i = 0; i < titles.length; i++) {
            final int index = i;
            final TableViewerColumn viewerColumn = new TableViewerColumn(tv, SWT.NONE);
            final TableColumn column = viewerColumn.getColumn();
            
            column.setAlignment(SWT.RIGHT);
            column.setText(titles[i]);
            column.setWidth(bounds[i]);
            column.setResizable(true);
            column.setMoveable(true);
            createMenuItem(headerMenu, column);
            
            column.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    tableSorter.setColumn(index);
                    int dir = tv.getTable().getSortDirection();
                    if (tv.getTable().getSortColumn() == column) {
                        //dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
                        if (dir == SWT.UP) {
                            dir = SWT.DOWN;
                        } else {
                            dir = SWT.UP;
                        }
                    } else {
                        dir = SWT.DOWN;
                    }
                    tv.getTable().setSortDirection(dir);
                    tv.getTable().setSortColumn(column);
                    tv.refresh();
                }
            });
        }
        final Table table = tv.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        
        table.addListener(SWT.MenuDetect, new Listener() {
            public void handleEvent(Event event) {
                table.setMenu(headerMenu);
            }
        });
    }
    
    /**
     * Create menu-items for table.
     * 
     * @param parent parent menu
     * @param column column of table
     */
    private void createMenuItem(Menu parent, final TableColumn column) {
        final MenuItem itemName = new MenuItem(parent, SWT.CHECK);
        itemName.setText(column.getText());
        itemName.setSelection(column.getResizable());
        itemName.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (itemName.getSelection()) {
                    column.setWidth(ExcelViewConstants.NORMALCOLUMNWIDTH);
                    column.setResizable(true);
                } else {
                    column.setWidth(0);
                    column.setResizable(false);
                }
            }
        });

    }
    
    @Override
    public void setFocus() {
        if (componentInformation != null) {
            form.setText(Messages.viewName + componentInformation.getName());
        }
        form.reflow(true);
        form.getParent().setFocus();
    }

    @Override
    public void update(Observable o, Object arg) {
        //Push into model
        parentComposite.getDisplay().asyncExec(new Runnable() {
            @Override
            public void run() {
                inputTableViewer.refresh();
                outputTableViewer.refresh();
            }
        });
    }
    
    /**
     * Create toolbar.
     */
    private void createToolbar() {
        IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
        mgr.add(copyClipboardAction);
        mgr.add(exportToExcel);
    }

    /**
     * Create actions.
     */
    private void createActions() {
        copyClipboardAction = new Action(Messages.copyToClipboard) {
            public void run() {
                copyToClipboard();
                LOGGER.debug("Copy to clipboard...");
            }
        };
        copyClipboardAction.setImageDescriptor(getImageDescriptor(ExcelViewConstants.ICONNAME_CLIPBOARD));
        
        
        exportToExcel = new Action(Messages.exportExcel) {
            public void run() { 
                final NewFileWizard wizz;
                boolean isNewXLFile = 
                    model.getChannelValues().get(0).getFile().getName().endsWith(ExcelFileExporter.FILEEXTENSION_XL2010);
                if (isNewXLFile) { 
                    wizz = new NewFileWizard(FILETYPENAME, 
                                             ExcelFileExporter.FILEEXTENSION_XL2010, 
                                             new String("empty").getBytes(), 
                                             null);
                } else {
                    wizz = new NewFileWizard(FILETYPENAME, 
                        ExcelFileExporter.FILEEXTENSION_XL2003, 
                        new String("empty").getBytes(), 
                        null);
                }
                wizz.init(PlatformUI.getWorkbench(), (IStructuredSelection) new StructuredSelection());
                final WizardDialog dialog = new WizardDialog(parentComposite.getShell(), wizz);
                dialog.create();
                int a = dialog.open();
                if (a == Dialog.OK) {
                    exportToExcel(new File(wizz.getFile().getLocation().toPortableString()));
                }
                try {
                    dialog.close();
                } catch (SWTException e) {
                    LOGGER.debug("Widget is disposed."); //Some error in threading, but it works. Fix later.
                }

                LOGGER.debug("Export to Excel...");
            }
        };
        exportToExcel.setImageDescriptor(getImageDescriptor(ExcelViewConstants.ICONNAME_EXCEL_16));
        
    }
    
    
    /**
     * Returns the image descriptor with the given relative path.
     * 
     * @param relativePath relative path to image
     * @return ImageDescriptor of image
     */
    public static ImageDescriptor getImageDescriptor(final String relativePath) {        
        ImageDescriptor id = ImageDescriptor.createFromURL(ExcelView.class.getClassLoader()
                                                                          .getResource(ExcelViewConstants.ICONBASEPATH + relativePath));
        return id;
    }
    
    /**
     * Export values to Excel.
     */
    private void exportToExcel(final File saveTo) {
        final SimpleExcelService excel = new SimpleExcelService();

        Thread t = new Thread("ExportExcel") {

            public void run() {
                try {
                    // Fill Excel with all input values
                    List<ChannelValue> allValues = model.getChannelValues();

                    boolean firstIteration = true;
                    for (ChannelValue cval : allValues) {
                        if (!cval.isInputValue()) {
                            continue;
                        }

                        if (firstIteration) {
                            excel.setValues(allValues.get(0).getFile(), saveTo, cval.getExcelAddress(), cval.getValues());
                            firstIteration = false;
                        } else {
                            excel.setValues(saveTo, cval.getExcelAddress(), cval.getValues());
                        }
                    }

                    // Show message box - action done
                    parentComposite.getDisplay().asyncExec(new Runnable() {

                        @Override
                        public void run() {
                            Image image = new Image(parentComposite.getDisplay(),
                                                    getImageDescriptor(ExcelViewConstants.ICONNAME_EXCEL_16).getImageData());
                            MessageDialog md = new MessageDialog(parentComposite.getShell(),
                                                                 Messages.exportExcel,
                                                                 image,
                                                                 Messages.actionDone,
                                                                 MessageDialog.INFORMATION,
                                                                 new String[] { Messages.actionButton }, 0);
                            md.open();
                        }
                    });
                } catch (RuntimeException re) {
                    LOGGER.error("Could not interact with Excel.", re);

                    // Show message box - action error
                    parentComposite.getDisplay().asyncExec(new Runnable() {

                        @Override
                        public void run() {
                            Image image = new Image(parentComposite.getDisplay(),
                                getImageDescriptor(ExcelViewConstants.ICONNAME_EXCEL_16).getImageData());
                            MessageDialog md = new MessageDialog(parentComposite.getShell(),
                                             Messages.exportExcel,
                                             image,
                                             Messages.actionError,
                                             MessageDialog.ERROR,
                                             new String[] { Messages.actionButton }, 0);
                            md.open();
                        }
                    });
                }
            }
        };
        t.start();
    }
    
    /**
     * Copy selected rows to clipboard.
     */
    private void copyToClipboard() {               
        Clipboard cb = new Clipboard(Display.getDefault());
        ISelection selection = null;
        List<ChannelValue> channelValueList = new ArrayList<ChannelValue>();

        //Input selection
        spi.setSelectionProviderDelegate(inputTableViewer);
        selection = this.getSite().getSelectionProvider().getSelection();
        if (selection != null && selection instanceof IStructuredSelection) {
            IStructuredSelection sel = (IStructuredSelection) selection;
            
            @SuppressWarnings("unchecked")
            Iterator<ChannelValue> iterator = sel.iterator();
            while (iterator.hasNext()) {
                ChannelValue channelValue = iterator.next();
                channelValueList.add(channelValue);
            }
        }
        
        //Output selection
        spi.setSelectionProviderDelegate(outputTableViewer);
        selection = this.getSite().getSelectionProvider().getSelection();
        if (selection != null && selection instanceof IStructuredSelection) {
            IStructuredSelection sel = (IStructuredSelection) selection;
            
            @SuppressWarnings("unchecked")
            Iterator<ChannelValue> iterator = sel.iterator();
            while (iterator.hasNext()) {
                ChannelValue channelValue = iterator.next();
                channelValueList.add(channelValue);
            }
        }
     
        
        StringBuilder sb = new StringBuilder();
        for (ChannelValue cval : channelValueList) {
            sb.append(channelValueToString(cval));
        }
        TextTransfer textTransfer = TextTransfer.getInstance();
        try {
            cb.setContents(new Object[] { sb.toString() }, new Transfer[] { textTransfer });
        } catch (IllegalArgumentException e) {
            LOGGER.info("Clipboard cannot be set. Nothing selected.");
        }
        
        
        //Deselect all rows.
        inputTableViewer.getTable().deselectAll();
        outputTableViewer.getTable().deselectAll();
    }
    
    
    /**
     * Returns String representation of channel value.
     * 
     * @param channelValue to convert
     * @return string representation of channel value
     */
    private String channelValueToString(final ChannelValue channelValue) {
        String value = channelValue.getValues().getValueAsString();
        
        String isInput = null;
        if (channelValue.isInputValue()) {
            isInput = Messages.inputChannelNameType;
        } else {
            isInput = Messages.outputChannelNameType;
        }
        

        return value + ExcelViewConstants.CLIPBOARDSEPARATOR
               + channelValue.getChannelName() + ExcelViewConstants.CLIPBOARDSEPARATOR
               + isInput
               + System.getProperty("line.separator");
    }

    @Override
    public void setComponentInstanceDescriptor(ComponentInstanceDescriptor compInstanceDescr) {
        componentInformation = compInstanceDescr;
        model.subscribeToAllPlatForms(componentInformation.getIdentifier());
        if (form != null) {
            form.setText(Messages.viewName + componentInformation.getName());
        }
    }
}
