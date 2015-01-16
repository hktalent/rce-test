/*
 * Copyright (C) 2006-2014 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.components.script.execution.jython;

import java.io.File;

import de.rcenvironment.components.script.common.registry.ScriptExecutor;
import de.rcenvironment.components.script.common.registry.ScriptExecutorFactory;
import de.rcenvironment.core.configuration.bootstrap.BootstrapConfiguration;
import de.rcenvironment.core.utils.scripting.ScriptLanguage;

/**
 * Factory for the Jython script language executor.
 * 
 * @author Sascha Zur
 * @author Robert Mischke
 */
public class JythonScriptExecutorFactory implements ScriptExecutorFactory {

    public JythonScriptExecutorFactory() {
        File internalDataDirectory = BootstrapConfiguration.getInstance().getInternalDataDirectory();
        System.setProperty("python.cachedir", new File(internalDataDirectory, "cache/jython").getAbsolutePath());
    }

    @Override
    public ScriptLanguage getSupportingScriptLanguage() {
        return ScriptLanguage.Jython;
    }

    @Override
    public ScriptExecutor createScriptExecutor() {
        return new JythonScriptExecutor();
    }

}
