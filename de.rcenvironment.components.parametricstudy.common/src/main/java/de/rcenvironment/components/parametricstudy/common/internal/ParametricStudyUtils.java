/*
 * Copyright (C) 2006-2014 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */
 
package de.rcenvironment.components.parametricstudy.common.internal;

import de.rcenvironment.components.parametricstudy.common.Study;

/**
 * Utility class for identifier construction.
 * @author Christian Weiss
 */
public final class ParametricStudyUtils {

    /** Constant. */
    public static final String STRUCTURE_PATTERN = "study.structure.%s";

    private static final String DATA_PATTERN = "study.data.%s";
    
    private ParametricStudyUtils() {}
    
    protected static String createStructureIdentifier(final Study study) {
        return String.format(STRUCTURE_PATTERN, study.getIdentifier());
    }

    protected static String createDataIdentifier(final Study study) {
        return String.format(DATA_PATTERN, study.getIdentifier());
    }
}
