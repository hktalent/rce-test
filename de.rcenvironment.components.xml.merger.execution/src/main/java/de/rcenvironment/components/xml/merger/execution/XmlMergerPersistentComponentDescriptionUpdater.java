/*
 * Copyright (C) 2006-2014 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */
package de.rcenvironment.components.xml.merger.execution;

import java.io.IOException;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.node.TextNode;

import de.rcenvironment.components.xml.merger.common.XmlMergerComponentConstants;
import de.rcenvironment.core.component.update.api.PersistentComponentDescription;
import de.rcenvironment.core.component.update.api.PersistentComponentDescriptionUpdaterUtils;
import de.rcenvironment.core.component.update.api.PersistentDescriptionFormatVersion;
import de.rcenvironment.core.component.update.spi.PersistentComponentDescriptionUpdater;
import de.rcenvironment.cpacs.utils.common.components.PersistentCpacsComponentDescriptionUpdaterUtilsForVersionThree;

/**
 * Implementation of {@link PersistentComponentDescriptionUpdater}.
 * 
 * @author Markus Kunde
 * @author Doreen Seider
 */
public class XmlMergerPersistentComponentDescriptionUpdater implements PersistentComponentDescriptionUpdater {

    private static final String CPACS = "CPACS";

    private static final String V3_0 = "3.0";
    
    private static final String V3_1 = "3.1";
    
    private static final String V3_2 = "3.2";
    
    private final String currentVersion = V3_2;
    
    private JsonFactory jsonFactory = new JsonFactory();

    @Override
    public String[] getComponentIdentifiersAffectedByUpdate() {
        return XmlMergerComponentConstants.COMPONENT_IDS;
    }

    @Override
    public int getFormatVersionsAffectedByUpdate(String persistentComponentDescriptionVersion, boolean silent) {

        int versionsToUpdate = PersistentDescriptionFormatVersion.NONE;

        if (!silent && persistentComponentDescriptionVersion != null) {
            if (persistentComponentDescriptionVersion.compareTo(V3_0) < 0) {
                versionsToUpdate = versionsToUpdate | PersistentDescriptionFormatVersion.FOR_VERSION_THREE;
            }
            if (persistentComponentDescriptionVersion.compareTo(currentVersion) < 0) {
                versionsToUpdate = versionsToUpdate | PersistentDescriptionFormatVersion.AFTER_VERSION_THREE;
            }
        }
        return versionsToUpdate;
    }

    @Override
    public PersistentComponentDescription performComponentDescriptionUpdate(int formatVersion,
        PersistentComponentDescription description, boolean silent) throws IOException {
        if (!silent) {
            if (formatVersion == PersistentDescriptionFormatVersion.FOR_VERSION_THREE) {
                description = updateToV30(description);
            } else if (formatVersion == PersistentDescriptionFormatVersion.AFTER_VERSION_THREE) {
                if (description.getComponentVersion().compareTo(V3_1) < 0) {
                    description = updateFrom3To31(description);
                }
                if (description.getComponentVersion().compareTo(V3_2) < 0) {
                    description = updateFrom31To32(description);
                }
            }
        }
        return description;
    }
    
    /**
     * Updates the component from version 0 to 3.0.
     * */
    private PersistentComponentDescription updateToV30(PersistentComponentDescription description) throws JsonParseException, IOException {

        description =
            PersistentComponentDescriptionUpdaterUtils.updateAllDynamicEndpointsToIdentifier("dynamicOutputs", "default", description);
        description =
            PersistentComponentDescriptionUpdaterUtils.updateAllDynamicEndpointsToIdentifier("dynamicInputs", "default", description);

        
        // StaticOutput CPACS=FileReference, Static Input CPACS=FileReference, Integrate=FileReference
        description = PersistentCpacsComponentDescriptionUpdaterUtilsForVersionThree.addStaticInput(description, CPACS); 
        description = PersistentCpacsComponentDescriptionUpdaterUtilsForVersionThree.addStaticInput(description, "CPACS to integrate");
        description = PersistentCpacsComponentDescriptionUpdaterUtilsForVersionThree.addStaticOutputCPACS(description);
        
        
        // if ConfigValue consumeCPACS==true; CPACS and Integrate CPACS StaticInputs = required
        // else StaticInput CPACS=initialized, Integrate = required
        // Delete ConfigValue consumeCPACS 
        description = PersistentCpacsComponentDescriptionUpdaterUtilsForVersionThree.updateConsumeCPACSFlag(description);
        
        
        // Sets all incoming channels usage to "optional."
        description = PersistentCpacsComponentDescriptionUpdaterUtilsForVersionThree.updateDynamicInputsOptional(description);

        description.setComponentVersion(V3_0);
        
        return description;
    }
    
    private PersistentComponentDescription updateFrom3To31(PersistentComponentDescription description)
        throws JsonParseException, IOException {
        JsonParser jsonParser = jsonFactory.createJsonParser(description.getComponentDescriptionAsString());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(jsonParser);
        
        final String name = "name";
        TextNode nameNode = (TextNode) rootNode.get(name);
        String nodeName = nameNode.getTextValue();
        if (nodeName.contains("CPACS Joiner")) {
            nodeName = nodeName.replaceAll("CPACS Joiner", "XML Merger");
            ((ObjectNode) rootNode).put(name, TextNode.valueOf(nodeName));
        }
        
        JsonNode dynInputs = rootNode.get("staticInputs");
        for (JsonNode staticInput : dynInputs) {
            ((ObjectNode) staticInput).put(name, TextNode.valueOf(staticInput.get(name).getTextValue().replace(CPACS, "XML")));
        }
        
        JsonNode staticOutputs = rootNode.get("staticOutputs");
        for (JsonNode staticOutput : staticOutputs) {
            ((ObjectNode) staticOutput).put(name, TextNode.valueOf(staticOutput.get(name).getTextValue().replace(CPACS, "XML")));
        }

        ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
        description = new PersistentComponentDescription(writer.writeValueAsString(rootNode));
        description.setComponentVersion(V3_1);
        return description;
    }
    
    private PersistentComponentDescription updateFrom31To32(PersistentComponentDescription description)
        throws JsonParseException, IOException {
        description = PersistentComponentDescriptionUpdaterUtils.updateSchedulingInformation(description);
        description.setComponentVersion(V3_2);
        return description;
    }

}
