/*
 * Copyright (C) 2006-2014 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.gui.wizards.toolintegration;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;

import de.rcenvironment.core.component.integration.ToolIntegrationConstants;
import de.rcenvironment.core.component.model.endpoint.api.EndpointDefinition;
import de.rcenvironment.core.datamodel.api.DataType;
import de.rcenvironment.core.gui.wizards.toolintegration.api.ToolIntegrationWizardPage;
import de.rcenvironment.core.utils.common.StringUtils;

/**
 * @author Sascha Zur
 */
public class InOutputConfigurationPage extends ToolIntegrationWizardPage {

    /** Key for in-/output from configuration map. */
    public static final String FOLDER = ToolIntegrationConstants.KEY_ENDPOINT_FOLDER;

    /** Key for in-/output from configuration map. */
    public static final String DATA_TYPE = ToolIntegrationConstants.KEY_ENDPOINT_DATA_TYPE;

    /** Key for in-/output from configuration map. */
    public static final String NAME = ToolIntegrationConstants.KEY_ENDPOINT_NAME;

    /** Key for in-/output from configuration map. */
    public static final String FILENAME = ToolIntegrationConstants.KEY_ENDPOINT_FILENAME;

    /** Key for in-/output from configuration map. */
    public static final String USAGE = ToolIntegrationConstants.KEY_ENDPOINT_USAGE;
    
    /** Key for in-/output from configuration map. */
    public static final String HANDLING = ToolIntegrationConstants.KEY_INPUT_HANDLING;
    
    /** Key for in-/output from configuration map. */
    public static final String CONSTRAINT = ToolIntegrationConstants.KEY_INPUT_EXECUTION_CONSTRAINT;

    /** Key for the input keyword. */
    public static final String INPUTS = ToolIntegrationConstants.KEY_ENDPOINT_INPUTS;

    private static final String OUTPUTS = ToolIntegrationConstants.KEY_ENDPOINT_OUTPUTS;

    private Map<String, Object> configurationMap;

    private Table inputTable;

    private Table outputTable;

    protected InOutputConfigurationPage(String pageName, Map<String, Object> configurationMap) {
        super(pageName);
        setTitle(pageName);
        setDescription(Messages.inoutputPageDescription);
        this.configurationMap = configurationMap;
        if (configurationMap.get(INPUTS) == null) {
            configurationMap.put(INPUTS, new LinkedList<Object>());
        }
        if (configurationMap.get(OUTPUTS) == null) {
            configurationMap.put(OUTPUTS, new LinkedList<Object>());
        }
    }

    @Override
    public void createControl(Composite parent) {
        CTabFolder tabFolder = new CTabFolder(parent, SWT.BORDER);
        inputTable = createTabItem(Messages.inputs, INPUTS, tabFolder);
        outputTable = createTabItem(Messages.outputs, OUTPUTS, tabFolder);
        updateTable(INPUTS);
        updateTable(OUTPUTS);
        tabFolder.setSelection(0);
        setControl(tabFolder);
        setPageComplete(true);
    }

    private Table createTabItem(String name, String type, CTabFolder tabFolder) {
        CTabItem item = new CTabItem(tabFolder, SWT.NONE);
        item.setText(name);

        Composite client = new Composite(tabFolder, SWT.NONE);
        client.setLayout(new GridLayout(2, false));
        item.setControl(client);

        final Composite tableComposite = new Composite(client, SWT.NONE);
        TableColumnLayout tableLayout = new TableColumnLayout();
        tableComposite.setLayout(tableLayout);
        final Table table = new Table(tableComposite, SWT.V_SCROLL | SWT.H_SCROLL | SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER);

        table.setHeaderVisible(true);

        GridData tableLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 5);
        final int minHeight = 140;
        tableLayoutData.heightHint = minHeight; // effectively min height
        tableComposite.setLayoutData(tableLayoutData);

        // first column - name
        TableColumn col1 = new TableColumn(table, SWT.NONE);
        col1.setText(name.substring(0, name.length() - 1));
        // second column - data type
        TableColumn col2 = new TableColumn(table, SWT.NONE);
        col2.setText(Messages.dataType);
        TableColumn col3 = null;
        TableColumn col4 = null;
        TableColumn col5 = null;

        if (type.equals(INPUTS)) {
            col3 = new TableColumn(table, SWT.NONE);
            col3.setText(Messages.inputHandling);
            
            col4 = new TableColumn(table, SWT.NONE);
            col4.setText(Messages.inputExecutionConstraint);

            col5 = new TableColumn(table, SWT.NONE);
            col5.setText(Messages.filename);
        }

        // layout data for the columns
        final int columnWeight = 20;
        tableLayout.setColumnData(col1, new ColumnWeightData(columnWeight, true));
        tableLayout.setColumnData(col2, new ColumnWeightData(columnWeight, true));
        if (type.equals(INPUTS)) {
            tableLayout.setColumnData(col3, new ColumnWeightData(columnWeight, true));
            tableLayout.setColumnData(col4, new ColumnWeightData(columnWeight, true));
            tableLayout.setColumnData(col5, new ColumnWeightData(columnWeight, true));
        }

        Button buttonAdd = new Button(client, SWT.FLAT);
        buttonAdd.setText(Messages.add);
        buttonAdd.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
        buttonAdd
            .addSelectionListener(new ButtonSelectionListener(buttonAdd, table, name.toLowerCase()));
        Button buttonEdit = new Button(client, SWT.FLAT);
        buttonEdit.setText(Messages.edit);
        buttonEdit.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
        buttonEdit.addSelectionListener(new ButtonSelectionListener(buttonEdit, table, name.toLowerCase()));
        buttonEdit.setEnabled(false);

        Button buttonRemove = new Button(client, SWT.FLAT);
        buttonRemove.setText(Messages.remove);
        buttonRemove.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
        buttonRemove.addSelectionListener(new ButtonSelectionListener(buttonRemove, table, name.toLowerCase()));
        buttonRemove.setEnabled(false);

        table.addSelectionListener(new TableSelectionListener(table, buttonEdit, buttonRemove));
        return table;
    }

    private void updateButtonActivation(Table table, Button edit, Button remove) {
        boolean enabled = (table.getSelection() != null && table.getSelectionCount() > 0 && table.getItemCount() != 0);
        edit.setEnabled(enabled);
        remove.setEnabled(enabled);
    }

    @SuppressWarnings("unchecked")
    protected void fillTable(String type) {
        Table table;
        if (type.equals(INPUTS)) {
            table = inputTable;
        } else {
            table = outputTable;
        }
        table.removeAll();
        List<Map<String, String>> staticEndpoints = (List<Map<String, String>>) configurationMap.get(type);
        fillCells(staticEndpoints, table, type);
    }

    private void fillCells(List<Map<String, String>> staticEndpoints, Table table, String type) {
        if (staticEndpoints != null) {
            Map<String, Map<String, String>> endpointNames = new TreeMap<String, Map<String, String>>();
            for (Map<String, String> endpoint : staticEndpoints) {
                endpointNames.put(endpoint.get(NAME), endpoint);
            }
            for (Entry<String, Map<String, String>> endpointName : endpointNames.entrySet()) {
                Map<String, String> endpoint = endpointName.getValue();
                TableItem item = new TableItem(table, SWT.None);
                item.setText(0, endpoint.get(NAME));
                item.setText(1, DataType.valueOf(endpoint.get(DATA_TYPE)).getDisplayName());
                // migration code: initial, required, optional -> immutable, consuming; required, required if connected
                if (type.equals(INPUTS)) {
                    if (endpoint.containsKey(HANDLING)) {
                        String text = "";
                        for (String handling : StringUtils.splitAndUnescape(endpoint.get(HANDLING))) {
                            text += EndpointDefinition.InputDatumHandling.valueOf(handling).getDisplayName();
                            text += ", ";
                        }
                        item.setText(2, text.substring(0, text.length() - 2));
                    } else if (endpoint.containsKey(USAGE) && endpoint.get(USAGE).equals("initial")) {
                        item.setText(2, EndpointDefinition.InputDatumHandling.Constant.getDisplayName());
                    } else {
                        item.setText(2, EndpointDefinition.InputDatumHandling.Single.getDisplayName());
                    }
                    if (endpoint.containsKey(CONSTRAINT)) {
                        String text = "";
                        for (String constraint : StringUtils.splitAndUnescape(endpoint.get(CONSTRAINT))) {
                            text += EndpointDefinition.InputExecutionContraint.valueOf(constraint).getDisplayName();
                            text += ", ";
                        }
                        item.setText(3, text.substring(0, text.length() - 2));
                    } else if (endpoint.containsKey(USAGE) && endpoint.get(USAGE).equals("optional")) {
                        item.setText(3, EndpointDefinition.InputExecutionContraint.NotRequired.getDisplayName());
                    } else {
                        item.setText(3, EndpointDefinition.InputExecutionContraint.Required.getDisplayName());
                    }
                    if ((DataType.valueOf(endpoint.get(DATA_TYPE)) == DataType.FileReference
                        || DataType.valueOf(endpoint.get(DATA_TYPE)) == DataType.DirectoryReference)
                        && endpoint.get(FILENAME) != null) {
                        if (endpoint.get(FILENAME).isEmpty()) {
                            item.setText(4, Messages.emptyFilenameTable);
                        } else if (endpoint.get(FILENAME).equals("-")) {
                            item.setText(4, ToolIntegrationConstants.DIRECTORIES_PLACEHOLDER[2] + File.separator
                                + endpoint.get(FILENAME));
                        } else {
                            item.setText(4, ToolIntegrationConstants.DIRECTORIES_PLACEHOLDER[2] + File.separator
                                + endpoint.get(NAME) + File.separator + endpoint.get(FILENAME));
                        }
                    } else {
                        item.setText(4, "" + "-");
                    }
                }
            }
        }
    }

    /**
     * 
     * @param type if it is for inputs or for outputs
     * @return all current configured in- and outputs.
     */
    @SuppressWarnings("unchecked")
    public List<String> getAllEndpointNames(String type) {
        List<String> result = new LinkedList<String>();
        List<Object> inputEndpoints = (List<Object>) configurationMap.get(type);
        if (inputEndpoints != null) {
            for (Object endpoint : inputEndpoints) {
                Map<String, String> endpointMap = (Map<String, String>) endpoint;
                result.add(endpointMap.get(NAME));
            }
        }
        return result;
    }

    protected void updateTable(String name) {
        fillTable(name);
    }

    /**
     * Lisetener for the in-/output tables.
     * 
     * @author Sascha Zur
     */
    private final class TableSelectionListener extends SelectionAdapter {

        private final Table table;

        private final Button editButton;

        private final Button removeButton;

        private TableSelectionListener(Table table, Button editButton, Button removeButton) {
            this.table = table;
            this.editButton = editButton;
            this.removeButton = removeButton;
        }

        @Override
        public void widgetSelected(SelectionEvent e) {
            updateButtonActivation(table, editButton, removeButton);
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
        }
    }

    /**
     * Listener for the table buttons.
     * 
     * @author Sascha Zur
     */
    private class ButtonSelectionListener implements SelectionListener {

        private final Button button;

        private final Table table;

        private final String type;

        public ButtonSelectionListener(Button button, Table table, String name) {
            this.button = button;
            this.table = table;
            type = name;
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent arg0) {
            widgetSelected(arg0);

        }

        @SuppressWarnings("unchecked")
        @Override
        public void widgetSelected(SelectionEvent arg0) {
            TableItem[] selection = table.getSelection();
            String title = type.substring(0, 1).toUpperCase() + type.substring(1, type.length() - 1);

            if (button.getText().equals(Messages.add)) {
                WizardEndpointEditDialog weed =
                    new WizardEndpointEditDialog(null, Messages.add + " " + title, type, getAllEndpointNames(type));
                int exit = weed.open();
                if (exit == Dialog.OK) {
                    if (((List<Map<String, String>>) configurationMap.get(type)) == null) {
                        configurationMap.put(type, new LinkedList<Map<String, String>>());
                    }
                    ((List<Map<String, String>>) configurationMap.get(type)).add(weed.getConfig());
                    updateTable(type);
                    ((ToolIntegrationWizard) getWizard()).updateAllPages();
                }
            } else if (button.getText().equals(Messages.edit)) {
                if (selection != null && selection.length > 0) {
                    List<Map<String, String>> endpointList = (List<Map<String, String>>) configurationMap.get(type);
                    Map<String, String> oldConfig = null;
                    Map<String, String> oldConfigCopy = new HashMap<String, String>();
                    for (Map<String, String> endpoint : endpointList) {
                        if (endpoint.get(NAME) != null && endpoint.get(NAME).equals(selection[0].getText())) {
                            oldConfigCopy.putAll(endpoint);
                            oldConfig = endpoint;
                        }
                    }
                    WizardEndpointEditDialog weed =
                        new WizardEndpointEditDialog(null, Messages.edit + " " + title, type, oldConfigCopy, getAllEndpointNames(type));
                    int exit = weed.open();
                    if (exit == Dialog.OK) {
                        ((List<Map<String, String>>) configurationMap.get(type)).remove(oldConfig);
                        ((List<Map<String, String>>) configurationMap.get(type)).add(weed.getConfig());
                        updateTable(type);
                        ((ToolIntegrationWizard) getWizard()).updateAllPages();
                    }
                }
            } else if (button.getText().equals(Messages.remove)) {
                if (selection != null && selection.length > 0) {
                    List<Map<String, String>> endpointList = (List<Map<String, String>>) configurationMap.get(type);
                    Map<String, String> endpointToRemove = null;
                    for (Map<String, String> endpoint : endpointList) {
                        if (endpoint.get(NAME) != null && endpoint.get(NAME).equals(selection[0].getText())) {
                            endpointToRemove = endpoint;
                        }
                    }
                    endpointList.remove(endpointToRemove);
                    updateTable(type);
                    ((ToolIntegrationWizard) getWizard()).updateAllPages();
                }
            }

        }
    }

    /**
     * Sets a new configurationMap and updates all fields.
     * 
     * @param newConfigurationMap new map
     */
    @Override
    public void setConfigMap(Map<String, Object> newConfigurationMap) {
        configurationMap = newConfigurationMap;
        updatePageValues();
    }

    private void updatePageValues() {
        updateTable(INPUTS);
        updateTable(OUTPUTS);
    }

    @Override
    public void performHelp() {
        super.performHelp();
        IWorkbenchHelpSystem helpSystem = PlatformUI.getWorkbench().getHelpSystem();
        helpSystem.displayHelp("de.rcenvironment.core.gui.wizard.toolintegration.integration_inputOutput");
    }

    @Override
    public void updatePage() {
    }
}
