/*
 * Copyright (C) 2006-2010 DLR, Fraunhofer SCAI, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.rce.gui.workflow.execute;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import de.rcenvironment.rce.communication.PlatformIdentifier;
import de.rcenvironment.rce.component.ComponentDescription;
import de.rcenvironment.rce.component.workflow.WorkflowDescription;
import de.rcenvironment.rce.component.workflow.WorkflowExecutionConfigurationHelper;
import de.rcenvironment.rce.component.workflow.WorkflowNode;

/**
 * {@link WizardPage} to configure the general workflow execution settings.
 * 
 * @author Christian Weiss
 */
final class WorkflowPage extends WizardPage {

    private static final String PLATFORM_DATA_PREFIX = "platform_index";

    private static final boolean ECLIPSE_STYLE_EDITING = false;

    private static final String KEY_PRESS = "Ctrl+Space";

    private final WorkflowDescription workflowDescription;

    private final WorkflowExecutionConfigurationHelper helper;

    private WorkflowComposite workflowComposite;

    private final Set<Resource> resources = new HashSet<Resource>();

    /**
     * The Constructor.
     */
    public WorkflowPage(final WorkflowExecutionWizard parentWizard) {
        super(Messages.workflowPageName);
        this.workflowDescription = parentWizard.getWorkflowDescription();
        this.helper = parentWizard.getHelper();
        setTitle(Messages.workflowPageTitle);
    }

    /**
     * {@inheritDoc} This includes the {@link Image} resources of table icons.
     * 
     * @see org.eclipse.jface.dialogs.DialogPage#dispose()
     */
    @Override
    public void dispose() {
        for (Resource resource : resources) {
            resource.dispose();
        }
        super.dispose();
    }

    @Override
    public void createControl(Composite parent) {
        // create the composite
        workflowComposite = new WorkflowComposite(parent, SWT.NONE);
        setControl(workflowComposite);
        // configure the workflow name text field
        String workflowName = workflowDescription.getName();
        if (workflowName != null) {
            workflowComposite.workflowNameText.setText(workflowName);
        }
        workflowComposite.workflowNameText.setFocus();
        workflowComposite.workflowNameText.selectAll();
        workflowComposite.workflowNameText.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent event) {
                String name = WorkflowPage.this.workflowComposite.workflowNameText.getText();
                WorkflowPage.this.workflowDescription.setName(name);
            }

        });
        // configure the target platform combo box
        final PlatformIdentifier localPlatform = helper.getLocalPlatform();
        final List<PlatformIdentifier> platforms = helper.getTargetPlatformsSortedByName();
        workflowComposite.targetPlatformCombo.add(Messages.localPlatformSelectionTitle);
        workflowComposite.targetPlatformCombo.setData(PLATFORM_DATA_PREFIX + 0, null);
        int index = 0;
        for (PlatformIdentifier platform : platforms) {
            index++;
            if (platform.equals(localPlatform)) {
                workflowComposite.targetPlatformCombo.add(Messages.bind(Messages.localPlatformExplicitSelectionTitle,
                    platform.getAssociatedDisplayName()));
            } else {
                workflowComposite.targetPlatformCombo.add(platform.getAssociatedDisplayName());
            }
            workflowComposite.targetPlatformCombo.setData(PLATFORM_DATA_PREFIX + index, platform);
        }
        // select the configured platform or default to the local platform
        PlatformIdentifier selectedPlatform = workflowDescription.getTargetPlatform();
        workflowComposite.targetPlatformCombo.select(platforms.indexOf(selectedPlatform) + 1);
        index = workflowComposite.targetPlatformCombo.getSelectionIndex();
        PlatformIdentifier platform = (PlatformIdentifier) workflowComposite.targetPlatformCombo.getData(PLATFORM_DATA_PREFIX + index);
        workflowDescription.setTargetPlatform(platform);
        workflowComposite.targetPlatformCombo.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                int index = workflowComposite.targetPlatformCombo.getSelectionIndex();
                PlatformIdentifier platform =
                    (PlatformIdentifier) workflowComposite.targetPlatformCombo.getData(PLATFORM_DATA_PREFIX + index);
                workflowDescription.setTargetPlatform(platform);
            }

        });
        // configure the workflow components table viewer
        workflowComposite.componentsTableViewer.setContentProvider(new WorkflowDescriptionContentProvider());
        if (ECLIPSE_STYLE_EDITING) {
            workflowComposite.componentsTableViewer.setLabelProvider(new WorkflowNodeLabelProvider(localPlatform));
        }
        workflowComposite.componentsTableViewer.setInput(workflowDescription);

        String additionalInformation = WorkflowPage.this.workflowDescription.getAdditionalInformation();
        if (additionalInformation != null) {
            workflowComposite.additionalInformationText.setText(additionalInformation);
        }
        workflowComposite.additionalInformationText.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent event) {
                String additionalInformation = WorkflowPage.this.workflowComposite.additionalInformationText.getText();
                WorkflowPage.this.workflowDescription.setAdditionalInformation(additionalInformation);
            }

        });

    }

    public WorkflowComposite getWorkflowComposite() {
        return workflowComposite;
    }

    /**
     * The composite containing the controls to configure the workflow execution.
     * 
     * @author Christian Weiss
     */
    public class WorkflowComposite extends Composite {

        /**
         * {@link CellLabelProvider} class equipping every target platform cell with a distinct
         * editor.
         * 
         * @author Christian Weiss
         */
        private final class WorkflowNodeTargetPlatformLabelProvider extends CellLabelProvider {

            private final Table componentsTable;

            private final TargetPlatformEditingSupport editingSupport;

            private Map<WorkflowNode, Image> images = new HashMap<WorkflowNode, Image>();;

            /**
             * The constructor.
             * 
             * @param componentsTable
             */
            private WorkflowNodeTargetPlatformLabelProvider(final Table componentsTable,
                final TargetPlatformEditingSupport editingSupport) {
                this.componentsTable = componentsTable;
                this.editingSupport = editingSupport;
            }

            /**
             * Returns the {@link Image} to be used as icon for the given {@link WorkflowNode} or
             * null if none is set. The image is created if it does not exist yet and added to the
             * {@link WorkflowPage#resources} set to be disposed upon disposal of the
             * {@link WizardPage} instance}.
             * 
             * @param workflowNode The {@link WorkflowNode} to get the icon for.
             * @return The icon of the given {@link WorkflowNode} or null if none is set.
             */
            private Image getImage(WorkflowNode workflowNode) {
                // create the image, if it has not been created yet
                if (!images.containsKey(workflowNode)) {
                    final ComponentDescription componentDescription = workflowNode.getComponentDescription();
                    Image image = null;
                    // prefer the 16x16 icon
                    byte[] imageData = componentDescription.getIcon16();
                    // if there is no 16x16 icon try the 32x32 one
                    if (imageData == null) {
                        imageData = componentDescription.getIcon32();
                    }
                    // only create an image, if icon data are available
                    if (imageData != null) {
                        image =
                            new Image(Display.getCurrent(), new ByteArrayInputStream(imageData));
                        resources.add(image);
                    }
                    images.put(workflowNode, image);
                }
                return images.get(workflowNode);
            }

            @Override
            public void update(ViewerCell cell) {
                final WorkflowNode workflowNode = (WorkflowNode) cell.getElement();
                TableItem item = (TableItem) cell.getViewerRow().getItem();
                Image workflowIcon = getImage(workflowNode);
                item.setImage(workflowIcon);
                TableEditor editor = new TableEditor(componentsTable);
                final CCombo combo = new CCombo(componentsTable, SWT.DROP_DOWN);
                combo.setEditable(false);
                combo.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
                editor.grabHorizontal = true;
                editor.setEditor(combo, item, 1);
                for (String value : editingSupport.getValues(workflowNode)) {
                    combo.add(value);
                }
                final Integer selectionIndex = (Integer) editingSupport.getValue(workflowNode);
                if (selectionIndex != null) {
                    combo.select(selectionIndex);
                } else {
                    // default selection is the first available element
                    combo.select(0);
                }
                String identifier = null;
                for (WorkflowNode node : workflowDescription.getWorkflowNodes()) {
                    if (node.getIdentifier().equals(workflowNode.getIdentifier())) {
                        identifier = node.getIdentifier();
                    }
                }
                editingSupport.setValue(workflowDescription.getWorkflowNode(identifier), combo.getSelectionIndex());

                if (editingSupport.isValueSuggestion(workflowNode)) {
                    combo.setBackground(colorWarn);
                    combo.setToolTipText(Messages.platformSelectionValueIsSuggestionToolTip);
                }
                combo.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        String identifier = null;
                        for (WorkflowNode node : workflowDescription.getWorkflowNodes()) {
                            if (node.getIdentifier().equals(workflowNode.getIdentifier())) {
                                identifier = node.getIdentifier();
                            }
                        }
                        editingSupport.setValue(workflowDescription.getWorkflowNode(identifier), combo.getSelectionIndex());
                        if (combo.getBackground().equals(colorWarn)) {
                            combo.setBackground(null);
                            combo.setToolTipText(null);
                        }
                    }
                });
            }
        }

        /**
         * {@link CellLabelProvider} class providing the name of the {@link WorkflowNode} of the
         * current row.
         * 
         * @author Christian Weiss
         */
        // FIXME static
        private final class WorkflowNodeNameLabelProvider extends CellLabelProvider {

            @Override
            public void update(ViewerCell cell) {
                cell.setText(((WorkflowNode) cell.getElement()).getName());
            }
        }

        // FIXME static
        private final Color colorWarn = Display.getCurrent().getSystemColor(SWT.COLOR_INFO_BACKGROUND);

        /** Text field for the name of the selected workflow. */
        private Text workflowNameText;

        /** Table viewer to select target platforms for all components. */
        private TableViewer componentsTableViewer;

        /** Combo box to select the controllers target platform. */
        private Combo targetPlatformCombo;

        /** Text field for the additional information of the selected workflow. */
        private Text additionalInformationText;

        /**
         * Creates the composite.
         * @param parent The parent composite.
         * @param style The style.
         */
        public WorkflowComposite(final Composite parent, int style) {
            super(parent, style);
            setLayout(new GridLayout(1, false));

            Group groupName = new Group(this, SWT.NONE);
            groupName.setLayout(new GridLayout(1, false));
            groupName.setText(Messages.nameGroupTitle);
            groupName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

            workflowNameText = new Text(groupName, SWT.BORDER);
            workflowNameText.setText("");
            workflowNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

            Group grpTargetPlatform = new Group(this, SWT.NONE);
            grpTargetPlatform.setLayout(new GridLayout(1, false));
            grpTargetPlatform.setText(Messages.controlTP);
            grpTargetPlatform.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

            targetPlatformCombo = new Combo(grpTargetPlatform, SWT.READ_ONLY);
            targetPlatformCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

            Group grpComponentsTp = new Group(this, SWT.NONE);
            grpComponentsTp.setLayout(new GridLayout(1, false));
            grpComponentsTp.setText(Messages.componentsTP);
            grpComponentsTp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

            componentsTableViewer = new TableViewer(grpComponentsTp, SWT.BORDER | SWT.FULL_SELECTION);
            final Table componentsTable = componentsTableViewer.getTable();
            componentsTable.setLinesVisible(true);
            componentsTable.setHeaderVisible(true);
            componentsTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

            // Set table model for individual components
            String[] titles = { Messages.component, Messages.targetPlatform };
            final int width = 250;

            final PlatformIdentifier localPlatform = WorkflowPage.this.helper.getLocalPlatform();
            final TargetPlatformEditingSupport editingSupport = new TargetPlatformEditingSupport(helper, localPlatform,
                componentsTableViewer, 1);
            for (int i = 0; i < titles.length; i++) {
                TableViewerColumn column = new TableViewerColumn(componentsTableViewer, SWT.NONE);
                column.getColumn().setText(titles[i]);
                column.getColumn().setWidth(width);
                column.getColumn().setResizable(true);
                column.getColumn().setMoveable(false);
                if (ECLIPSE_STYLE_EDITING) {
                    if (i == 1) {
                        column.setEditingSupport(new TargetPlatformEditingSupport(helper, localPlatform,
                            componentsTableViewer, i));
                    }
                } else {
                    switch (i) {
                    case 0:
                        column.setLabelProvider(new WorkflowNodeNameLabelProvider());
                        break;
                    case 1:
                        column.setLabelProvider(new WorkflowNodeTargetPlatformLabelProvider(componentsTable, editingSupport));
                        break;
                    default:
                        throw new AssertionError();
                    }
                }
            }

            Group groupAdditionalInformation = new Group(this, SWT.NONE);
            groupAdditionalInformation.setLayout(new GridLayout(1, false));
            groupAdditionalInformation.setText(de.rcenvironment.rce.gui.workflow.view.list.Messages.additionalInformationColon);
            groupAdditionalInformation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

            additionalInformationText = new Text(groupAdditionalInformation, SWT.BORDER);
            additionalInformationText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
            // pd =
            // WorkflowExecutionPlaceholderHelper.createPlaceholderDescriptionFactory(workflowDescription);
            //
            // addPlaceholderGroup();
            //
            // Button clearHistoryButton = new Button(this, SWT.NONE);
            // clearHistoryButton.setText(Messages.clearHistory);
            // clearHistoryButton.addSelectionListener(new SelectionAdapter() {
            // /**
            // * {@inheritDoc}
            // *
            // * @see
            // org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
            // */
            // @Override
            // public void widgetSelected(SelectionEvent e) {
            // WorkflowPageClearHistoryDialog chd = new
            // WorkflowPageClearHistoryDialog(parent.getShell(),
            // Messages.clearHistory, pd, workflowDescription);
            // chd.open();
            // }
            //
            // /**
            // * {@inheritDoc}
            // *
            // * @see
            // org.eclipse.swt.events.SelectionAdapter#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
            // */
            // @Override
            // public void widgetDefaultSelected(SelectionEvent e) {
            // widgetSelected(e);
            // }
            // });
            // if (pd.getComponentIDsWithPlaceholders().size() == 0){
            // clearHistoryButton.setEnabled(false);
            // }
        }

    }

}
