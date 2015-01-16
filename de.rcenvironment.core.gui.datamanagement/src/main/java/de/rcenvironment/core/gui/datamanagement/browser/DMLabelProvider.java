/*
 * Copyright (C) 2006-2014 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.gui.datamanagement.browser;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import de.rcenvironment.core.gui.datamanagement.browser.spi.DMBrowserNode;
import de.rcenvironment.core.gui.datamanagement.browser.spi.DMBrowserNodeType;
import de.rcenvironment.core.gui.resources.api.ImageManager;
import de.rcenvironment.core.gui.resources.api.StandardImages;

/**
 * Label provider for the {@link DataManagementBrowser}.
 * 
 * @author Markus Litz
 * @author Robert Mischke
 * 
 */
public class DMLabelProvider extends ColumnLabelProvider {

    @Override
    public Image getImage(Object element) {
        ImageManager imageManager = ImageManager.getInstance();
        Image result = DMBrowserImages.IMG_DEFAULT;
        DMBrowserNode node = (DMBrowserNode) element;
        if (node.getType() == DMBrowserNodeType.Workflow) {
            result = ImageManager.getInstance().getSharedImage(StandardImages.WORKFLOW_16);
        } else if (node.getType() == DMBrowserNodeType.Workflow_Disabled) {
            result = ImageManager.getInstance().getSharedImage(StandardImages.WORKFLOW_DISABLED_16);
        } else if (node.getType() == DMBrowserNodeType.Timeline) {
            result = DMBrowserImages.IMG_TIMELINE;
        } else if (node.getType() == DMBrowserNodeType.Components) {
            result = DMBrowserImages.IMG_COMPONENTS;
        } else if (node.getType() == DMBrowserNodeType.Component
            || node.getType() == DMBrowserNodeType.HistoryObject) {
            if (node.getIcon() != null) {
                result = node.getIcon();
            }
        } else if (node.getType() == DMBrowserNodeType.DMDirectoryReference) {
            result = ImageManager.getInstance().getSharedImage(StandardImages.FOLDER_16);
        } else if (node.getType() == DMBrowserNodeType.DMFileResource) {
            result = ImageManager.getInstance().getSharedImage(StandardImages.FILE_16);
        } else if (node.getType() == DMBrowserNodeType.InformationText) {
            result = ImageManager.getInstance().getSharedImage(StandardImages.INFORMATION_16);
        } else if (node.getType() == DMBrowserNodeType.WarningText) {
            result = ImageManager.getInstance().getSharedImage(StandardImages.WARNING_16);
        } else if (node.getType() == DMBrowserNodeType.HistoryRoot) {
            result = DMBrowserImages.IMG_DEFAULT;
            // return DMBrowserImages.IMG_DATA_MANAGEMENT;
            // } else if (file.type == DMBrowserNodeType.Resource) {
            // return DMBrowserImages.IMG_FILE;
            // } else if (file.type == DMBrowserNodeType.VersionizedResource) {
            // return DMBrowserImages.imageRevision;
            // } else if (file.type == DMBrowserNodeType.Component) {
            // return DMBrowserImages.imageComponent;
        } else if (node.getType() == DMBrowserNodeType.WorkflowRunInformation) {
            result = DMBrowserImages.IMG_WF_RUN_INFO;
        } else if (node.getType() == DMBrowserNodeType.Loading) {
            result = ImageManager.getInstance().getSharedImage(StandardImages.REFRESH_16);
        } else if (node.getType() == DMBrowserNodeType.ShortText) {
            result = imageManager.getSharedImage(StandardImages.DATATYPE_SHORTTEXT_16);
        } else if (node.getType() == DMBrowserNodeType.Boolean) {
            result = imageManager.getSharedImage(StandardImages.DATATYPE_BOOLEAN_16);
        } else if (node.getType() == DMBrowserNodeType.Integer) {
            result = imageManager.getSharedImage(StandardImages.DATATYPE_INTEGER_16);
        } else if (node.getType() == DMBrowserNodeType.Float) {
            result = imageManager.getSharedImage(StandardImages.DATATYPE_FLOAT_16);
        } else if (node.getType() == DMBrowserNodeType.Vector) {
            result = imageManager.getSharedImage(StandardImages.DATATYPE_VECTOR_16);
        } else if (node.getType() == DMBrowserNodeType.SmallTable) {
            result = imageManager.getSharedImage(StandardImages.DATATYPE_SMALLTABLE_16);
        } else if (node.getType() == DMBrowserNodeType.Indefinite) {
            result = imageManager.getSharedImage(StandardImages.DATATYPE_INDEFINITE_16);
        } else if (node.getType() == DMBrowserNodeType.File) {
            result = imageManager.getSharedImage(StandardImages.DATATYPE_FILE_16);
        } else if (node.getType() == DMBrowserNodeType.Directory) {
            result = imageManager.getSharedImage(StandardImages.DATATYPE_DIRECTORY_16);
        } else if (node.getType() == DMBrowserNodeType.Input) {
            result = imageManager.getSharedImage(StandardImages.INPUT_16);
        } else if (node.getType() == DMBrowserNodeType.Output) {
            result = imageManager.getSharedImage(StandardImages.OUTPUT_16);
        } else if (node.getType() == DMBrowserNodeType.LogFolder) {
            result = imageManager.getSharedImage(StandardImages.FILES_16);
        } else if (node.getType() == DMBrowserNodeType.ToolInputOutputFolder) {
            result = imageManager.getSharedImage(StandardImages.TOOL_INPUT_OUTPUT_16);
        } else if (node.getType() == DMBrowserNodeType.IntermediateInputsFolder) {
            result = imageManager.getSharedImage(StandardImages.INTERMEDIATE_INPUT_16);
        }
        return result;
    }

    @Override
    public String getText(Object element) {
        DMBrowserNode node = (DMBrowserNode) element;
        return node.getTitle();
    }

    @Override
    public String getToolTipText(Object element) {
        DMBrowserNode node = (DMBrowserNode) element;
        return node.getToolTip();
    }

    @Override
    public Color getForeground(Object element) {
        DMBrowserNode node = (DMBrowserNode) element;
        if (!node.isEnabled()) {
            return Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_FOREGROUND);
        }
        return super.getForeground(element);
    }

}
