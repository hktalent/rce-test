/*
 * Copyright (C) 2006-2012 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.rce.components.excel.gui.properties;



import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

import de.rcenvironment.rce.components.excel.commons.ExcelComponentConstants;
import de.rcenvironment.rce.components.excel.commons.ExcelUtils;
import de.rcenvironment.rce.components.excel.commons.ExcelService;
import de.rcenvironment.rce.components.excel.commons.SimpleExcelService;
import de.rcenvironment.rce.gui.workflow.editor.properties.ValidatingWorkflowNodePropertySection;

/**
 * "Properties" view tab for defining macros to run.
 * 
 * @author Patrick Schaefer
 * @author Markus Kunde
 */
public class MacrosSection extends ValidatingWorkflowNodePropertySection {

    private Composite macroGroup;

    private CCombo comboMacroPre;

    private CCombo comboMacroRun;

    private CCombo comboMacroPost;
    
    private Button discoverMacros;
    
    @Override
    protected void createCompositeContent(final Composite parent, final TabbedPropertySheetPage aTabbedPropertySheetPage) {
        final TabbedPropertySheetWidgetFactory toolkit = aTabbedPropertySheetPage.getWidgetFactory();

        final Composite content = new LayoutComposite(parent);
        content.setLayout(new GridLayout(1, true));

        final Composite macrosChoosingSection = toolkit.createFlatFormComposite(content);
        initMacrosChoosingSection(toolkit, macrosChoosingSection);
    }

    /**
     * Initialize macro choosing section.
     * 
     * @param toolkit the toolkit to create section content
     * @param container parent
     */
    private void initMacrosChoosingSection(final TabbedPropertySheetWidgetFactory toolkit, final Composite container) {
        GridData layoutData;
        layoutData = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
        container.setLayoutData(layoutData);
        container.setLayout(new FillLayout());
        final Section section = toolkit.createSection(container, Section.TITLE_BAR | Section.EXPANDED);
        section.setText(Messages.macrosChoosingSectionName);
        final Composite client = toolkit.createComposite(section);
        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        client.setLayoutData(layoutData);
        client.setLayout(new GridLayout(1, false));
        
        CLabel lblDescription = toolkit.createCLabel(client, Messages.macrosSectionDescription);

        macroGroup = toolkit.createComposite(client);
        macroGroup.setLayout(new GridLayout(2, true));
        
        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        
        toolkit.createCLabel(macroGroup, Messages.preMacro);
        comboMacroPre = toolkit.createCCombo(macroGroup);
        comboMacroPre.setEditable(true);
        comboMacroPre.setData(CONTROL_PROPERTY_KEY, ExcelComponentConstants.PRE_MACRO);
        toolkit.createCLabel(macroGroup, Messages.runMacro);
        comboMacroRun = toolkit.createCCombo(macroGroup);
        comboMacroRun.setEditable(true);
        comboMacroRun.setData(CONTROL_PROPERTY_KEY, ExcelComponentConstants.RUN_MACRO);
        toolkit.createCLabel(macroGroup, Messages.postMacro);
        comboMacroPost = toolkit.createCCombo(macroGroup);
        comboMacroPost.setEditable(true);
        comboMacroPost.setData(CONTROL_PROPERTY_KEY, ExcelComponentConstants.POST_MACRO);

        toolkit.createCLabel(macroGroup, "");
        discoverMacros = toolkit.createButton(macroGroup, Messages.macrosDiscoverButtonLabel, SWT.PUSH);

        lblDescription.setLayoutData(gridData);
        comboMacroPre.setLayoutData(gridData);        
        comboMacroRun.setLayoutData(gridData);
        comboMacroPost.setLayoutData(gridData);
        
        
        section.setClient(client);
        
    }


    /**
     * Discover all macros available in Excel file and fill Combo-lists with them.
     * 
     */
    private void discoverMacros() {
        ExcelService excelService = new SimpleExcelService();
        
        File xlFile = ExcelUtils.getAbsoluteFile(getProperty(ExcelComponentConstants.XL_FILENAME, String.class));
        if (xlFile != null) {
            String[] macrosAvailable = excelService.getMacros(xlFile);
            comboMacroPre.setItems(macrosAvailable);
            comboMacroRun.setItems(macrosAvailable);
            comboMacroPost.setItems(macrosAvailable);
        }
    }

    @Override
    protected void refreshBeforeValidation() {
        macroGroup.pack(true);
    }
    
    @Override
    protected Controller createController() {
        return new MacrosController();
    }

    /**
     * Custom {@link DefaultController} implementation to handle the activation of the GUI
     * controls.
     * 
     * @author Markus Kunde
     */
    private final class MacrosController extends DefaultController {

        @Override
        protected void widgetSelected(final SelectionEvent event, final Control source) {
            super.widgetSelected(event, source);
            if (source == discoverMacros) {
                discoverMacros();
            }
        }

    }
    
}
