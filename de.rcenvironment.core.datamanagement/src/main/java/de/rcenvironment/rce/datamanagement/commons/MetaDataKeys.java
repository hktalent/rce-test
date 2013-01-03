/*
 * Copyright (C) 2006-2010 DLR, Fraunhofer SCAI, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.rce.datamanagement.commons;

/**
 * Common meta data keys.
 * 
 * @author Robert Mischke
 */
public final class MetaDataKeys {

    /**
     * Managed meta data keys. These keys are set by the data management, and should only be used
     * for read/query operations from client code.
     * 
     * @author Dirk Rossow (original {@link MetaData} class)
     * @author Robert Mischke (conversion)
     */
    public final class Managed {

        /** External keys may not start with this prefix. */
        public static final String PROTECTED_KEY_PREFIX = "de.rcenvironment.rce.datamanagement.";

        /** The user that wrote the data. */
        public static final String AUTHOR = PROTECTED_KEY_PREFIX + "author";

        /** Date the data was written. Format: "yyyy-MM-dd HH:mm:ss". */
        public static final String DATE = PROTECTED_KEY_PREFIX + "date";

        /** Size of data. */
        public static final String SIZE = PROTECTED_KEY_PREFIX + "size";

        /** The user that initial created the {@link DataReference}; revision independent. */
        public static final String CREATOR = PROTECTED_KEY_PREFIX + "creator";
        
        private Managed() {}
    }

    /** An associated filename; optional. */
    public static final String FILENAME = "rce.common.filename";

    /** UUID of the associated component context; recommended for future cleanup operations. */
    public static final String COMPONENT_CONTEXT_UUID = "rce.common.component_context_uuid";

    /** End-user name of the associated component context; optional. */
    public static final String COMPONENT_CONTEXT_NAME = "rce.common.component_context_name";

    /** UUID of the associated component; optional. */
    public static final String COMPONENT_UUID = "rce.common.component_uuid";

    /** End-user name of the associated component; optional. */
    public static final String COMPONENT_NAME = "rce.common.component_name";
    
    private MetaDataKeys() {}
}
