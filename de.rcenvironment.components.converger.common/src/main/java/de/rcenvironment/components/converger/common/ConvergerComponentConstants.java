/*
 * Copyright (C) 2006-2014 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.components.converger.common;

import de.rcenvironment.core.component.api.ComponentConstants;

/**
 * Constants.
 * 
 * @author Sascha Zur
 */
public final class ConvergerComponentConstants {

    /** Component ID. */
    public static final String COMPONENT_ID = ComponentConstants.COMPONENT_IDENTIFIER_PREFIX + "converger";
    
    /** Constant. */
    public static final String[] COMPONENT_IDS = new String[] { COMPONENT_ID,
        "de.rcenvironment.components.converger.execution.ConvergerComponent_Converger" };

    /** Constant. */
    public static final String META_HAS_STARTVALUE = "hasStartValue";

    /** Constant. */
    public static final String META_STARTVALUE = "startValue";

    /** Constant. */
    public static final String KEY_EPS_R = "epsR";

    /** Constant. */
    public static final String KEY_EPS_A = "epsA";

    /** Constant. */
    public static final String KEY_MAX_ITERATIONS = "maxIterations";
    
    /** Constant. */
    public static final String KEY_ITERATIONS_TO_CONSIDER = "iterationsToConsider";
    
    /** Constant. */
    public static final String DEFAULT_VALUE_ITERATIONS_TO_CONSIDER = "1";

    /** Constant. */
    public static final String CONVERGED_OUTPUT_SUFFIX = "_converged";

    /** Constant. */
    public static final String CONVERGED = "Converged";

    /** Constant. */
    public static final String CONVERGED_ABSOLUTE = "Converged absolute";

    /** Constant. */
    public static final String CONVERGED_RELATIVE = "Converged relative";

    /** Constant. */
    public static final String ID_CONVERGED_VALUE = "convergedValue";

    /** Constant. */
    public static final String ID_VALUE_TO_CONVERGE = "valueToConverge";

    private ConvergerComponentConstants() {

    }
}
