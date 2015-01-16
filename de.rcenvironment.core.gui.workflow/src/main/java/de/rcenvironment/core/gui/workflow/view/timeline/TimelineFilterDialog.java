/*
 * Copyright (C) 2006-2014 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.gui.workflow.view.timeline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * Dialog that helps to manage connections.
 *
 * @author Hendrik Abbenhaus
 */
public class TimelineFilterDialog extends Dialog implements KeyListener, ICheckStateListener {

    /** Initial string. */
    public String initialFilterText = "";

    /** The viewer. */
    private CheckboxTreeViewer viewer;

    /** The content provider. */
    private TreeContentProvider contentProvider;

    private Tree tree;

    private ComponentViewerFilter filter;

    private Set<String> componentNameFilter = new HashSet<String>();
    
    private TimelineFilterTreeNode[] currentCheckedElements = null;
    

    public TimelineFilterDialog(Shell parentShell, String[] currentFilter) {
        super(parentShell);
        this.setShellStyle(SWT.RESIZE | SWT.MAX | SWT.PRIMARY_MODAL);
        for (String currentString : currentFilter) {
            componentNameFilter.add(currentString);
        }

    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        GridLayout gridLayout = new GridLayout(3, false);
        gridLayout.horizontalSpacing = 0;
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        container.setLayout(gridLayout);

        Group sourceGroup = new Group(container, SWT.NONE);
        sourceGroup.setText(Messages.selectComponents);
        GridData gridData1 = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        gridData1.widthHint = 1;
        sourceGroup.setLayoutData(gridData1);
        GridLayout gridLayout1 = new GridLayout(1, false);
        gridLayout1.marginTop = 5;
        gridLayout1.marginWidth = 0;
        gridLayout1.verticalSpacing = 0;
        gridLayout1.marginHeight = 0;
        gridLayout1.horizontalSpacing = 0;

        sourceGroup.setLayout(gridLayout1);
        viewer = new CheckboxTreeViewer(sourceGroup, SWT.NONE);
        tree = viewer.getTree();
        tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        tree.setLinesVisible(true);

        // viewer.addCheckStateListener(
        filter = new ComponentViewerFilter(initialFilterText);
        viewer.addFilter(filter);
        viewer.setUseHashlookup(true);

        TreeContentProvider contentprovider = new TreeContentProvider();
        this.contentProvider = contentprovider;
        viewer.setContentProvider(contentprovider);
        viewer.setLabelProvider(new TreeLabelProvider());
        viewer.addCheckStateListener(this);

        GridData gridData31 = new GridData();
        gridData31.grabExcessHorizontalSpace = true;
        gridData31.horizontalAlignment = GridData.FILL;

        Text sourceFilterText = new Text(sourceGroup, SWT.BORDER);
        sourceFilterText.setMessage(Messages.filterDialogFilterDefault);
        sourceFilterText.setToolTipText(Messages.filterDialogToolTipText);
        sourceFilterText.setLayoutData(gridData31);
        if (!initialFilterText.equals("")) {
            sourceFilterText.setText(initialFilterText);
        }
        sourceFilterText.addKeyListener(this);

        return container;
    }

    @Override
    public void keyPressed(KeyEvent arg0) { // sourceFilterText KeyListner
        //first 
        this.currentCheckedElements = getCheckedElements().toArray(new TimelineFilterTreeNode[getCheckedElements().size()]);
        filter.setFilterString(((Text) arg0.getSource()).getText());
        viewer.refresh();
        viewer.expandAll();
    }

    @Override
    public void keyReleased(KeyEvent arg0) { // sourceFilterText KeyListner
        keyPressed(arg0);
        viewer.setCheckedElements(this.currentCheckedElements);
        for (TimelineFilterTreeNode currentNode : this.currentCheckedElements){
            updateTree(currentNode, getCheckedElements(), true);
        }
    }
    
    @Override
    protected void okPressed() {
        componentNameFilter.clear();
        for (TreeItem currentObj : viewer.getTree().getItems()) {
            TimelineFilterTreeNode currentNode = (TimelineFilterTreeNode) currentObj.getData();
            for (TimelineFilterTreeNode c : currentNode.getChildren()){
                if (viewer.getChecked(c)){
                    componentNameFilter.add(c.getDisplayName());
                }
            }
            
        }
        
        super.okPressed();
    };

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);

    }

    @Override
    protected Point getInitialSize() {
        final int width = 227;
        final int height = 384;
        return new Point(width, height);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(Messages.filterDialogTitle);
    }

    /**
     * 
     * @param newrows the rows
     */
    public void setContent(TimelineComponentRow[] newrows) {
        if (newrows == null || newrows.length == 0){
            return;
        }
        List<TimelineFilterTreeNode> checkedElements = new ArrayList<TimelineFilterTreeNode>();
        TimelineFilterTreeNode root = new TimelineFilterTreeNode();
        for (TimelineComponentRow current : newrows) {
            TimelineFilterTreeNode parentNode = root.hasChildWithComponentID(current.getComponentID());
            if (parentNode == null) {
                parentNode = new TimelineFilterTreeNode();
                parentNode.setComponentID(current.getComponentID());
                parentNode.setParent(root);
                root.addChild(parentNode);
            }
            TimelineFilterTreeNode n = new TimelineFilterTreeNode();
            n.setRow(current);
            if (isAllowedComponentName(current.getName())){
                checkedElements.add(n);
            }
            parentNode.addChild(n);
        }

        viewer.setInput(root);
        viewer.refresh();
        viewer.expandAll();
        this.currentCheckedElements = checkedElements.toArray(new TimelineFilterTreeNode[checkedElements.size()]);
        viewer.setCheckedElements(this.currentCheckedElements);
        for (TimelineFilterTreeNode currentNode : checkedElements){
            updateTree(currentNode, getCheckedElements(), true);
        }
    }
    
    private boolean isAllowedComponentName(String name){
        if (componentNameFilter == null){
            return true;
        }
        for (String currentFilterString : componentNameFilter){
            if (name.equals(currentFilterString)){
                return true;
            }
        }
        return false;
    }

    @Override
    public void checkStateChanged(CheckStateChangedEvent event) {
        Object node = event.getElement();
        if (viewer.getGrayed(node)) {
            viewer.setGrayChecked(node, false);
        }
        List<Object> checkedElements = getCheckedElements();
        updateTree(node, checkedElements, event.getChecked());
    }

    /**
     * 
     * 
     * @return Returns an array of filterd Componentnames
     */
    public String[] getFilteredNames() {
        return componentNameFilter.toArray(new String[componentNameFilter.size()]);
    }

    /**
     * Update tree.
     *
     * @param node the node
     * @param checkedElements the checked elements
     * @param checked the checked
     */
    private void updateTree(Object node, List<Object> checkedElements, boolean checked) {
        List<Object> descendants = getDescendants(node);
        Set<Object> checkedSet = new HashSet<Object>(checkedElements);
        for (Object n : descendants) {
            viewer.setGrayChecked(n, false);
            viewer.setChecked(n, checked);
            if (checked) {
                checkedSet.add(n);
            } else {
                checkedSet.remove(n);
            }
        }
        updateAncestors(node, checkedSet);
    }

    /**
     * Update ancestors.
     *
     * @param child the child
     * @param checkedElements the checked elements
     */
    private void updateAncestors(Object child, Set<Object> checkedElements) {
        Object parent = contentProvider.getParent(child);
        if (parent == null) {
            return;
        }
        boolean isGreyed = viewer.getChecked(child) && viewer.getGrayed(child);
        if (isGreyed) {
            // if child is greyed then everying up should be greyed as well
            viewer.setGrayChecked(parent, true);
        } else {
            Object[] children = contentProvider.getChildren(parent);
            List<Object> cloned = new ArrayList<Object>();
            cloned.addAll(Arrays.asList(children));
            cloned.removeAll(checkedElements);
            if (cloned.isEmpty()) {
                // every child is checked
                viewer.setGrayed(parent, false);
                viewer.setChecked(parent, true);
                checkedElements.add(parent);
            } else {

                if (viewer.getChecked(parent) && !viewer.getGrayed(parent)) {
                    checkedElements.remove(parent);
                }
                viewer.setGrayChecked(parent, false);

                // some children selected but not all
                if (cloned.size() < children.length) {
                    viewer.setGrayChecked(parent, true);
                }

            }
        }
        updateAncestors(parent, checkedElements);
    }

    /**
     * Gets the descendants.
     *
     * @param node the node
     * @return the descendants
     */
    private List<Object> getDescendants(Object node) {
        List<Object> desc = new ArrayList<Object>();
        getDescendantsHelper(desc, node);
        return desc;
    }

    /**
     * Gets the descendants helper.
     *
     * @param descendants the descendants
     * @param node the node
     * @return the descendants helper
     */
    private void getDescendantsHelper(List<Object> descendants, Object node) {
        Object[] children = contentProvider.getChildren(node);
        if (children == null || children.length == 0) {
            return;
        }
        descendants.addAll(Arrays.asList(children));
        for (Object child : children) {
            getDescendantsHelper(descendants, child);
        }
    }

    /**
     * Gets the checked elements (excluding grayed out elements).
     *
     * @return the checked elements
     */
    public List<Object> getCheckedElements() {
        List<Object> checkedElements = new ArrayList<Object>(Arrays.asList(viewer.getCheckedElements()));
        checkedElements.removeAll(getGrayedElements());
        return checkedElements;
    }

    /**
     * Gets the grayed elements.
     *
     * @return the grayed elements
     */
    public List<Object> getGrayedElements() {
        return Arrays.asList(viewer.getGrayedElements());
    }

    /**
     * 
     *
     * @author Hendrik Abbenhaus
     */
    public class TreeLabelProvider extends LabelProvider {

        @Override
        public String getText(Object element) {
            if (element instanceof TimelineFilterTreeNode) {
                TimelineFilterTreeNode current = (TimelineFilterTreeNode) element;
                return current.getDisplayName();
            }
            return null;
        }

        @Override
        public Image getImage(Object element) {
            if (element instanceof TimelineFilterTreeNode) {
                TimelineFilterTreeNode current = (TimelineFilterTreeNode) element;
                if (current.hasRow()) {
                    return current.getRow().getIcon();
                } else {
                    return TimelineView.getImageIconFromId(current.getComponentID(), this);
                }
            }
            return null;
        }

    }

    /**
     * 
     *
     * @author Hendrik Abbenhaus
     */
    public class TreeContentProvider implements ITreeContentProvider {

        @Override
        public Object[] getChildren(Object parentElement) {
            return ((TimelineFilterTreeNode) parentElement).getChildren().toArray();
        }

        @Override
        public Object getParent(Object element) {
            return ((TimelineFilterTreeNode) element).getParent();
        }

        @Override
        public boolean hasChildren(Object element) {
            return !((TimelineFilterTreeNode) element).getChildren().isEmpty();
        }

        @Override
        public Object[] getElements(Object inputElement) {
            return getChildren(inputElement);
        }

        @Override
        public void dispose() {}

        @Override
        public void inputChanged(Viewer arg0, Object arg1, Object arg2) {}

    }

    /**
     * Filter to remove unwanted components in TimeTableFilter dialog.
     * 
     * @author Hendrik Abbenhaus
     */
    public class ComponentViewerFilter extends ViewerFilter {

        private String filterString = "";

        public ComponentViewerFilter(String initialText) {
            if (!initialText.equals("")) {
                filterString = initialText;
            }
        }
        
        @Override
        public boolean select(Viewer arg0, Object arg1, Object arg2) {
            if (arg2 instanceof TimelineFilterTreeNode) {
                TimelineFilterTreeNode item = ((TimelineFilterTreeNode) arg2);
                if (item.getDisplayName().toLowerCase().toString().contains(filterString.toLowerCase())) {
                    return true;
                }
                if (item.getParent() != null){
                    if (item.getParent().getDisplayName() != null){
                        if (item.getParent().getDisplayName().toLowerCase().toString().contains(filterString.toLowerCase())){
                            return true;
                        }

                    }
                }
                if (item.getChildren() != null){
                    if (!item.getChildren().isEmpty()){
                        for (TimelineFilterTreeNode current : item.getChildren()){
                            if (current.getDisplayName().toLowerCase().toString().contains(filterString.toLowerCase())) {
                                return true;
                            }
                        }
                    }
                }              
            }
            return false;
        }

        public String getFilterString() {
            return filterString;
        }

        public void setFilterString(String filterString) {
            this.filterString = filterString;
        }
    }

}
