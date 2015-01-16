/*
 * Copyright (C) 2006-2014 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */
package de.rcenvironment.components.script.execution.python.internal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptException;

import de.rcenvironment.components.script.common.ScriptComponentHistoryDataItem;
import de.rcenvironment.components.script.common.registry.ScriptExecutor;
import de.rcenvironment.components.script.execution.DefaultScriptExecutor;
import de.rcenvironment.core.component.api.ComponentException;
import de.rcenvironment.core.component.datamanagement.api.ComponentDataManagementService;
import de.rcenvironment.core.component.execution.api.ComponentContext;
import de.rcenvironment.core.component.execution.api.ConsoleRow;
import de.rcenvironment.core.component.execution.api.ConsoleRow.WorkflowLifecyleEventType;
import de.rcenvironment.core.configuration.ConfigurationService;
import de.rcenvironment.core.datamodel.api.DataType;
import de.rcenvironment.core.datamodel.api.TypedDatum;
import de.rcenvironment.core.datamodel.types.api.VectorTD;
import de.rcenvironment.core.notification.DistributedNotificationService;
import de.rcenvironment.core.scripting.python.PythonComponentConstants;
import de.rcenvironment.core.scripting.python.PythonScriptContext;
import de.rcenvironment.core.scripting.python.PythonScriptEngine;
import de.rcenvironment.core.utils.common.OSFamily;
import de.rcenvironment.core.utils.scripting.ScriptLanguage;

/**
 * 
 * Implementation of {@link ScriptExecutor} to execute python scripts. This is needed because Python
 * is not part of the default ScriptEngine and must be implemented manually. For this we require
 * some different code for the {@link ScriptExecutor} methods.
 * 
 * @author Sascha Zur
 * 
 */
public class PythonScriptExecutor extends DefaultScriptExecutor {

    private static final String OS = "os";

    private PythonScriptContext scriptContext;

    private Map<String, Object> stateMap;

    @Override
    public void reset() {
        stateMap = new HashMap<String, Object>();
    }

    @Override
    public boolean prepareExecutor(ComponentContext compCtx, DistributedNotificationService inNotificationService) {
        super.prepareExecutor(compCtx, inNotificationService);
        notificationService = inNotificationService;
        componentContext = compCtx;
        String pythonInstallation = componentContext.getConfigurationValue(PythonComponentConstants.PYTHON_INSTALLATION);
        scriptContext = new PythonScriptContext();
        scriptContext.setAttribute(PythonComponentConstants.PYTHON_INSTALLATION, pythonInstallation, 0);
        scriptContext.setAttribute(OS, OSFamily.getLocal(), 0);
        scriptContext.setAttribute(PythonComponentConstants.COMPONENT_CONTEXT, componentContext, 0);
        stateMap = new HashMap<String, Object>();
        return true;
    }

    @Override
    public void prepareOutputForRun() {}

    @Override
    public void prepareNewRun(ScriptLanguage scriptLanguage, String userScript, ScriptComponentHistoryDataItem dataItem)
        throws ComponentException {
        historyDataItem = dataItem;
        scriptEngine = scriptingService.createScriptEngine(scriptLanguage);
        wrappingScript = userScript;
        if (wrappingScript == null || wrappingScript.length() == 0) {
            throw new ComponentException("No Python script provided");
        }
        scriptEngine.setContext(scriptContext);
        scriptContext.removeAttribute(PythonComponentConstants.STATE_MAP, 0);
        scriptContext.setAttribute(PythonComponentConstants.STATE_MAP, stateMap, 0);
        scriptContext.removeAttribute(PythonComponentConstants.RUN_NUMBER, 0);
        scriptContext.setAttribute(PythonComponentConstants.RUN_NUMBER, getCurrentRunNumber(), 0);
        ((PythonScriptEngine) scriptEngine).createNewExecutor(historyDataItem);

    }

    @Override
    public void runScript() throws ComponentException {
        int exitCode = 0;
        try {
            // Executing script here
            componentContext.printConsoleLine(WorkflowLifecyleEventType.TOOL_STARTING.name(), ConsoleRow.Type.LIFE_CYCLE_EVENT);
            exitCode = (Integer) scriptEngine.eval(wrappingScript);
            componentContext.printConsoleLine(WorkflowLifecyleEventType.TOOL_FINISHED.name(), ConsoleRow.Type.LIFE_CYCLE_EVENT);
        } catch (ScriptException e) {
            throw new ComponentException("Could not run Python script. Maybe the script has errors? \n\n", e);
        }

        if (exitCode != 0) {
            throw new ComponentException("Could not run Python script. Exit Code: " + exitCode + "\n"
                + (((PythonScriptEngine) scriptEngine)).getStderrAsString());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean postRun() throws ComponentException {
        for (String outputName : componentContext.getOutputs()) {
            DataType type = componentContext.getOutputDataType(outputName);
            List<Object> resultList = (List<Object>) scriptEngine.get(outputName);
            TypedDatum outputValue = null;
            if (scriptEngine.get(outputName) != null) {
                for (Object o : resultList) {
                    if (o != null) {
                        switch (type) {
                        case ShortText:
                            outputValue = typedDatumFactory.createShortText(String.valueOf(o));
                            break;
                        case Boolean:
                            outputValue = typedDatumFactory.createBoolean(Boolean.parseBoolean(String.valueOf(o)));
                            break;
                        case Float:
                            try {
                                outputValue = typedDatumFactory.createFloat(Double.parseDouble(String.valueOf(o)));
                            } catch (NumberFormatException e) {
                                LOGGER.error(String.format("Output %s could not be parsed to data type Float", o.toString()));
                                throw new ComponentException(e);
                            }
                            break;
                        case Integer:
                            try {
                                outputValue = typedDatumFactory.createInteger(Long.parseLong(String.valueOf(o)));
                            } catch (NumberFormatException e) {
                                LOGGER.error(String.format("Output %s could not be parsed to data type Integer", o.toString()));
                                throw new ComponentException(e);
                            }
                            break;
                        case FileReference:
                            outputValue = handleFileOrDirectoryOutput(outputName, outputValue, "file", o);
                            break;
                        case DirectoryReference:
                            outputValue = handleFileOrDirectoryOutput(outputName, outputValue, "directory", o);
                            break;
                        case Empty:
                            outputValue = typedDatumFactory.createEmpty();
                            break;
                        case Vector:
                            List<Object> resultVector = (List<Object>) o;
                            VectorTD vector = typedDatumFactory.createVector(resultVector.size());
                            int index = 0;
                            for (Object element : resultVector) {
                                double convertedValue = 0;
                                if (element instanceof Integer) {
                                    convertedValue = (Integer) element;
                                } else {
                                    convertedValue = (Double) element;
                                }
                                vector.setFloatTDForElement(typedDatumFactory.createFloat(convertedValue), index);
                                index++;
                            }
                            outputValue = vector;
                            break;
                        case SmallTable:
                            List<Object> rowArray = (List<Object>) o;
                            TypedDatum[][] result = new TypedDatum[rowArray.size()][];
                            if (rowArray.size() > 0 && rowArray.get(0).getClass().getName().equals(ArrayList.class.getName())) {
                                int i = 0;
                                for (Object columnObject : rowArray) {
                                    List<Object> columnArray = (List<Object>) columnObject;
                                    result[i] = new TypedDatum[columnArray.size()];
                                    int j = 0;
                                    for (Object element : columnArray) {
                                        result[i][j++] = getTypedDatum(element);
                                    }
                                    i++;
                                }
                                outputValue = typedDatumFactory.createSmallTable(result);
                            } else {
                                int i = 0;
                                for (Object element : rowArray) {
                                    result[i] = new TypedDatum[1];
                                    result[i][0] = getTypedDatum(element);
                                    i++;
                                }
                                outputValue = typedDatumFactory.createSmallTable(result);
                            }
                            break;
                        default:
                            outputValue = typedDatumFactory.createShortText(o.toString()); // should
                                                                                           // not
                                                                                           // happen
                        }
                        componentContext.writeOutput(outputName, outputValue);
                    }
                }
            }
        }

        for (String outputName : ((PythonScriptEngine) scriptEngine).getNotAValueOutputsList()) {
            componentContext.writeOutput(outputName, typedDatumFactory.createNotAValue());
        }

        stateMap = ((PythonScriptEngine) scriptEngine).getStateOutput();
        for (String outputName : ((PythonScriptEngine) scriptEngine).getCloseOutputChannelsList()) {
            componentContext.closeOutput(outputName);
        }

        ((PythonScriptEngine) scriptEngine).dispose();
        return true;
    }

    private TypedDatum handleFileOrDirectoryOutput(String outputName, TypedDatum outputValue, String type, Object o)
        throws ComponentException {
        try {
            File file = new File(String.valueOf(o));
            if (!file.isAbsolute()) {
                file = new File(((PythonScriptEngine) scriptEngine).getExecutor().getWorkDir(), String.valueOf(o));
            }
            if (file.exists()) {
                if (type.equals("directory")) {
                    outputValue =
                        componentDatamanagementService.createDirectoryReferenceTDFromLocalDirectory(componentContext, file,
                            file.getName());
                } else {
                    outputValue = componentDatamanagementService.createFileReferenceTDFromLocalFile(componentContext, file,
                        file.getName());
                }
                if (file.getAbsolutePath().startsWith(
                    componentContext.getService(ConfigurationService.class)
                        .getParentTempDirectoryRoot().getAbsolutePath())) {
                    tempFiles.add(file);
                }
            } else {
                throw new ComponentException(String.format(
                    "Could not write %s for output \"%s\" because it does not exist: %s", type, outputName,
                    file.getAbsolutePath()));
            }
        } catch (IOException e) {
            throw new ComponentException(String.format(
                "Storing directory in the data management failed. No directory is written to output '%s'",
                outputName), e);
        }
        return outputValue;
    }

    @Override
    public void deleteTempFiles() {
        super.deleteTempFiles();
        if (scriptEngine != null) {
            ((PythonScriptEngine) scriptEngine).dispose();
        }
    }

    @Override
    protected void bindComponentDataManagementService(ComponentDataManagementService compDataManagementService) {
        componentDatamanagementService = compDataManagementService;
    }
}
