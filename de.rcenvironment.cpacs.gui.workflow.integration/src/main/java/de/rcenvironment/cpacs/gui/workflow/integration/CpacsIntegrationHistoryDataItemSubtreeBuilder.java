/*
 * Copyright (C) 2006-2014 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.cpacs.gui.workflow.integration;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import de.rcenvironment.core.datamodel.api.TypedDatumSerializer;
import de.rcenvironment.core.datamodel.api.TypedDatumService;
import de.rcenvironment.core.gui.datamanagement.browser.spi.CommonHistoryDataItemSubtreeBuilderUtils;
import de.rcenvironment.core.gui.datamanagement.browser.spi.ComponentHistoryDataItemSubtreeBuilder;
import de.rcenvironment.core.gui.datamanagement.browser.spi.DMBrowserNode;
import de.rcenvironment.core.gui.datamanagement.browser.spi.DMBrowserNodeType;
import de.rcenvironment.core.gui.workflow.integration.IntegrationHistoryDataItemSubtreeBuilder;
import de.rcenvironment.core.utils.incubator.ServiceRegistry;
import de.rcenvironment.core.utils.incubator.ServiceRegistryAccess;
import de.rcenvironment.cpacs.component.integration.CpacsIntegrationHistoryDataItem;
import de.rcenvironment.cpacs.component.integration.CpacsToolIntegrationConstants;

/**
 * Implementation of {@link ComponentHistoryDataItemSubtreeBuilder} for integrated CPACS tools.
 * 
 * @author Jan Flink
 */
public class CpacsIntegrationHistoryDataItemSubtreeBuilder extends IntegrationHistoryDataItemSubtreeBuilder {

    private static final String STRING_TOOLINPUT = "Tool Input: %s";

    private static final String STRING_TOOLOUTPUT = "Tool Output: %s";

    private static final String STRING_DEFAULT_TOOLINPUT_FILENAME = "toolInput.xml";

    private static final String STRING_DEFAULT_TOOLOUTPUT_FILENAME = "toolOutput.xml";

    private static final String STRING_WITHOUT_TOOLSPECIFIC_FILENAME = "without_toolspec.xml";

    private static final String STRING_WITH_VARIABLES_FILENAME = "with_dyn_inputs.xml";

    private DMBrowserNode intermediateInputsNode;

    @Override
    public String[] getSupportedHistoryDataItemIdentifier() {
        return new String[] { CpacsToolIntegrationConstants.CPACS_COMPONENT_ID_PREFIX.replace(".", "\\.") + ".*" };
    }

    @Override
    public Serializable deserializeHistoryDataItem(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        return (Serializable) ois.readObject();
    }

    @Override
    public void buildInitialHistoryDataItemSubtree(Serializable historyDataItem, DMBrowserNode parent) {

        ServiceRegistryAccess registryAccess = ServiceRegistry.createAccessFor(this);
        TypedDatumSerializer serializer = registryAccess.getService(TypedDatumService.class).getSerializer();

        if (historyDataItem instanceof String) {
            CpacsIntegrationHistoryDataItem historyData;
            try {
                historyData = CpacsIntegrationHistoryDataItem.fromString((String) historyDataItem, serializer, "");
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }

            CommonHistoryDataItemSubtreeBuilderUtils.buildCommonHistoryDataItemSubtrees(historyData, parent);
            super.buildInitialHistoryDataItemSubtree(historyDataItem, parent);
            if (historyData.getCpacsWithVariablesFileReference() != null
                || historyData.getToolInputWithoutToolspecificFileReference() != null) {
                intermediateInputsNode =
                    DMBrowserNode.addNewChildNode("Intermediate Inputs", DMBrowserNodeType.IntermediateInputsFolder, parent);
                if (historyData.getCpacsWithVariablesFileReference() != null) {
                    DMBrowserNode cpacsWithVariablesNode =
                        DMBrowserNode.addNewLeafNode(String.format("XML w/ dyn. inputs: %s", STRING_WITH_VARIABLES_FILENAME),
                            DMBrowserNodeType.DMFileResource, intermediateInputsNode);
                    cpacsWithVariablesNode.setAssociatedFilename(STRING_WITH_VARIABLES_FILENAME);
                    cpacsWithVariablesNode.setDataReferenceId(historyData.getCpacsWithVariablesFileReference());
                }
                if (historyData.getToolInputWithoutToolspecificFileReference() != null) {
                    DMBrowserNode toolInputWithoutToolSpecificNode =
                        DMBrowserNode.addNewLeafNode(
                            String.format("Tool Input w/o static tool specifics: %s", STRING_WITHOUT_TOOLSPECIFIC_FILENAME),
                            DMBrowserNodeType.DMFileResource,
                            intermediateInputsNode);
                    toolInputWithoutToolSpecificNode.setAssociatedFilename(STRING_WITHOUT_TOOLSPECIFIC_FILENAME);
                    toolInputWithoutToolSpecificNode.setDataReferenceId(historyData.getToolInputWithoutToolspecificFileReference());
                }
            }

            DMBrowserNode toolFilesNode =
                DMBrowserNode.addNewChildNode("Tool Input/Output", DMBrowserNodeType.ToolInputOutputFolder, parent);

            if (historyData.getToolInputFileReference() != null) {
                String toolInputFileName = historyData.getToolInputFilename();
                if (toolInputFileName == null) {
                    toolInputFileName = STRING_DEFAULT_TOOLINPUT_FILENAME;
                }
                DMBrowserNode toolInputNode =
                    DMBrowserNode.addNewLeafNode(String.format(STRING_TOOLINPUT, toolInputFileName), DMBrowserNodeType.DMFileResource,
                        toolFilesNode);
                toolInputNode.setAssociatedFilename(toolInputFileName);
                toolInputNode.setDataReferenceId(historyData.getToolInputFileReference());
            }

            if (historyData.getToolOutputFileReference() != null) {
                String toolOutputFileName = historyData.getToolOutputFilename();
                if (toolOutputFileName == null) {
                    toolOutputFileName = STRING_DEFAULT_TOOLOUTPUT_FILENAME;
                }
                DMBrowserNode toolOutputNode =
                    DMBrowserNode.addNewLeafNode(String.format(STRING_TOOLOUTPUT, toolOutputFileName), DMBrowserNodeType.DMFileResource,
                        toolFilesNode);
                toolOutputNode.setAssociatedFilename(toolOutputFileName);
                toolOutputNode.setDataReferenceId(historyData.getToolOutputFileReference());
            }
        }
    }
}
