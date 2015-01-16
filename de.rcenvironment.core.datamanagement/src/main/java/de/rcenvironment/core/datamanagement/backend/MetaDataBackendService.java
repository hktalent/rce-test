/*
 * Copyright (C) 2006-2014 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.datamanagement.backend;

import de.rcenvironment.core.datamanagement.MetaDataService;
import de.rcenvironment.core.datamanagement.commons.BinaryReference;
import de.rcenvironment.core.datamanagement.commons.ComponentInstance;
import de.rcenvironment.core.datamanagement.commons.ComponentRun;
import de.rcenvironment.core.datamanagement.commons.DataReference;
import de.rcenvironment.core.datamanagement.commons.WorkflowRun;

/**
 * Interface of the data management meta data backend.
 * 
 * @author Jan Flink
 */
public interface MetaDataBackendService extends MetaDataService {

    /**
     * Key for a service property.
     */
    String PROVIDER = "de.rcenvironment.core.datamanagement.backend.metadata.provider";

    /**
     * Sets or updates the string representation of a timeline data item of an {@link WorkflowRun}.
     * 
     * @param workflowRunId The identifier of the {@link WorkflowRun}
     * @param timelineDataItem The string representation of the timeline data item.
     */
    void setOrUpdateTimelineDataItem(Long workflowRunId, String timelineDataItem);

    /**
     * Adds a {@link DataReference} to the {@link ComponentRun} with the given identifier.
     * 
     * @param componentRunId The identifier of the {@link ComponentRun}.
     * @param dataReference The {@link DataReference} to add.
     * @return The identifier of the generated dataReference
     */
    Long addDataReferenceToComponentRun(Long componentRunId, DataReference dataReference);

    /**
     * Adds a {@link DataReference} to the {@link ComponentInstance} with the given identifier.
     * 
     * @param componentInstanceId The identifier of the {@link ComponentInstance}.
     * @param dataReference The {@link DataReference} to add.
     * @return The identifier of the generated dataReference
     */
    Long addDataReferenceToComponentInstance(Long componentInstanceId, DataReference dataReference);

    /**
     * Adds a {@link DataReference} to the {@link ComponentRun} with the given identifier.
     * 
     * @param workflowRunId The identifier of the {@link ComponentRun}.
     * @param dataReference The {@link DataReference} to add.
     * @return The identifier of the generated dataReference
     */
    Long addDataReferenceToWorkflowRun(Long workflowRunId, DataReference dataReference);

    /**
     * Gets the {@link DataReference} for the given uuid from the meta data backend.
     * 
     * @param dataReferenceKey The key of the {@link DataReference} to return.
     * @return The {@link DataReference}.
     */
    DataReference getDataReference(String dataReferenceKey);

    /**
     * Adds a {@link BinaryReference} to the {@link DataReference} with the given identifier.
     * 
     * @param dataReferenceId The identifier of the {@link DataReference}.
     * @param binaryReference The {@link BinaryReference} to add.
     */
    void addBinaryReference(Long dataReferenceId, BinaryReference binaryReference);

}
