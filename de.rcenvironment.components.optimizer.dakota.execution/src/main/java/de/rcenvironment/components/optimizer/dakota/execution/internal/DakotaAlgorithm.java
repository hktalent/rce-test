/*
 * Copyright (C) 2006-2014 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.components.optimizer.dakota.execution.internal;

import static de.rcenvironment.components.optimizer.common.OptimizerComponentConstants.DATA_TYPE_KEY;
import static de.rcenvironment.components.optimizer.common.OptimizerComponentConstants.DEFAULT_VALUE_KEY;
import static de.rcenvironment.components.optimizer.common.OptimizerComponentConstants.DONT_WRITE_KEY;
import static de.rcenvironment.components.optimizer.common.OptimizerComponentConstants.GRADIENT_DELTA;
import static de.rcenvironment.components.optimizer.common.OptimizerComponentConstants.ID_CONSTRAINT;
import static de.rcenvironment.components.optimizer.common.OptimizerComponentConstants.ID_OBJECTIVE;
import static de.rcenvironment.components.optimizer.common.OptimizerComponentConstants.META_GOAL;
import static de.rcenvironment.components.optimizer.common.OptimizerComponentConstants.META_LOWERBOUND;
import static de.rcenvironment.components.optimizer.common.OptimizerComponentConstants.META_UPPERBOUND;
import static de.rcenvironment.components.optimizer.common.OptimizerComponentConstants.META_WEIGHT;
import static de.rcenvironment.components.optimizer.common.OptimizerComponentConstants.NOKEYWORD_KEY;
import static de.rcenvironment.components.optimizer.common.OptimizerComponentConstants.NO_LINEBREAK_KEY;
import static de.rcenvironment.components.optimizer.common.OptimizerComponentConstants.VALUE_KEY;
import static de.rcenvironment.components.optimizer.dakota.execution.internal.DakotaConstants.BACKSLASH;
import static de.rcenvironment.components.optimizer.dakota.execution.internal.DakotaConstants.BOOL;
import static de.rcenvironment.components.optimizer.dakota.execution.internal.DakotaConstants.COMMA;
import static de.rcenvironment.components.optimizer.dakota.execution.internal.DakotaConstants.DOT;
import static de.rcenvironment.components.optimizer.dakota.execution.internal.DakotaConstants.FD_GRADIENT_STEP_SIZE_KEY;
import static de.rcenvironment.components.optimizer.dakota.execution.internal.DakotaConstants.FD_HESSIAN_STEP_SIZE_KEY;
import static de.rcenvironment.components.optimizer.dakota.execution.internal.DakotaConstants.GRADIANT_INTERVAL_TYPE;
import static de.rcenvironment.components.optimizer.dakota.execution.internal.DakotaConstants.GRADIENTS_KEY;
import static de.rcenvironment.components.optimizer.dakota.execution.internal.DakotaConstants.GRADIENT_STEP_SIZE;
import static de.rcenvironment.components.optimizer.dakota.execution.internal.DakotaConstants.HESSIANS_KEY;
import static de.rcenvironment.components.optimizer.dakota.execution.internal.DakotaConstants.HESSIANS_VALUE;
import static de.rcenvironment.components.optimizer.dakota.execution.internal.DakotaConstants.HESSIAN_INTERVALL;
import static de.rcenvironment.components.optimizer.dakota.execution.internal.DakotaConstants.HESSIAN_STEP_SIZE;
import static de.rcenvironment.components.optimizer.dakota.execution.internal.DakotaConstants.INTERVAL_TYPE_HESSIAN_KEY;
import static de.rcenvironment.components.optimizer.dakota.execution.internal.DakotaConstants.INTERVAL_TYPE_KEY;
import static de.rcenvironment.components.optimizer.dakota.execution.internal.DakotaConstants.NEWLINE;
import static de.rcenvironment.components.optimizer.dakota.execution.internal.DakotaConstants.NORMAL;
import static de.rcenvironment.components.optimizer.dakota.execution.internal.DakotaConstants.NO_GRADIENTS;
import static de.rcenvironment.components.optimizer.dakota.execution.internal.DakotaConstants.NO_HESSIANS;
import static de.rcenvironment.components.optimizer.dakota.execution.internal.DakotaConstants.OUTPUT;
import static de.rcenvironment.components.optimizer.dakota.execution.internal.DakotaConstants.PARAMETER_GRADIENTS;
import static de.rcenvironment.components.optimizer.dakota.execution.internal.DakotaConstants.PARAMETER_HESSIANS;
import static de.rcenvironment.components.optimizer.dakota.execution.internal.DakotaConstants.PLACEHOLDER_CDV_INITIAL_POINT;
import static de.rcenvironment.components.optimizer.dakota.execution.internal.DakotaConstants.PLACEHOLDER_CDV_LOWER_BOUNDS;
import static de.rcenvironment.components.optimizer.dakota.execution.internal.DakotaConstants.PLACEHOLDER_CDV_NAMES;
import static de.rcenvironment.components.optimizer.dakota.execution.internal.DakotaConstants.PLACEHOLDER_CDV_UPPER_BOUNDS;
import static de.rcenvironment.components.optimizer.dakota.execution.internal.DakotaConstants.PLACEHOLDER_CONSTRAINT_COUNT;
import static de.rcenvironment.components.optimizer.dakota.execution.internal.DakotaConstants.PLACEHOLDER_CONSTRAINT_LOWER;
import static de.rcenvironment.components.optimizer.dakota.execution.internal.DakotaConstants.PLACEHOLDER_CONSTRAINT_UPPER;
import static de.rcenvironment.components.optimizer.dakota.execution.internal.DakotaConstants.PLACEHOLDER_CONTINUOUS_DESIGN_COUNT;
import static de.rcenvironment.components.optimizer.dakota.execution.internal.DakotaConstants.PLACEHOLDER_DRIVER_FOR_OS;
import static de.rcenvironment.components.optimizer.dakota.execution.internal.DakotaConstants.PLACEHOLDER_GRADIENT_2_SECTION;
import static de.rcenvironment.components.optimizer.dakota.execution.internal.DakotaConstants.PLACEHOLDER_GRADIENT_SECTION;
import static de.rcenvironment.components.optimizer.dakota.execution.internal.DakotaConstants.PLACEHOLDER_METHOD_2_CODE;
import static de.rcenvironment.components.optimizer.dakota.execution.internal.DakotaConstants.PLACEHOLDER_METHOD_2_PROPERTIES;
import static de.rcenvironment.components.optimizer.dakota.execution.internal.DakotaConstants.PLACEHOLDER_METHOD_3_CODE;
import static de.rcenvironment.components.optimizer.dakota.execution.internal.DakotaConstants.PLACEHOLDER_METHOD_3_PROPERTIES;
import static de.rcenvironment.components.optimizer.dakota.execution.internal.DakotaConstants.PLACEHOLDER_METHOD_CODE;
import static de.rcenvironment.components.optimizer.dakota.execution.internal.DakotaConstants.PLACEHOLDER_METHOD_PROPERTIES;
import static de.rcenvironment.components.optimizer.dakota.execution.internal.DakotaConstants.PLACEHOLDER_OBJECTIVES_WEIGHT;
import static de.rcenvironment.components.optimizer.dakota.execution.internal.DakotaConstants.PLACEHOLDER_OBJECTIVE_FUNCTIONS_COUNT;
import static de.rcenvironment.components.optimizer.dakota.execution.internal.DakotaConstants.PLACEHOLDER_WORKDIR;
import static de.rcenvironment.components.optimizer.dakota.execution.internal.DakotaConstants.RESOURCES_DAKOTA_GRADIENTS_BASE_SAMPLE;
import static de.rcenvironment.components.optimizer.dakota.execution.internal.DakotaConstants.RESOURCES_DAKOTA_GRADIENTS_SAMPLE;
import static de.rcenvironment.components.optimizer.dakota.execution.internal.DakotaConstants.RESOURCES_DAKOTA_HESSIANS_SAMPLE;
import static de.rcenvironment.components.optimizer.dakota.execution.internal.DakotaConstants.RESOURCES_DAKOTA_STANDARD_SAMPLE;
import static de.rcenvironment.components.optimizer.dakota.execution.internal.DakotaConstants.RESOURCES_DAKOTA_SURROGATE_SAMPLE;
import static de.rcenvironment.components.optimizer.dakota.execution.internal.DakotaConstants.STRING;
import static de.rcenvironment.components.optimizer.dakota.execution.internal.DakotaConstants.TABS;
import static de.rcenvironment.components.optimizer.dakota.execution.internal.DakotaConstants.TRUE;
import static de.rcenvironment.components.optimizer.dakota.execution.internal.DakotaConstants.WHITESPACES;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.commons.exec.OS;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import de.rcenvironment.components.optimizer.common.CommonBundleClasspathStub;
import de.rcenvironment.components.optimizer.common.MethodDescription;
import de.rcenvironment.components.optimizer.common.OptimizerComponentConstants;
import de.rcenvironment.components.optimizer.common.OptimizerComponentHistoryDataItem;
import de.rcenvironment.components.optimizer.common.execution.OptimizerAlgorithmExecutor;
import de.rcenvironment.core.component.api.ComponentException;
import de.rcenvironment.core.component.datamanagement.api.ComponentDataManagementService;
import de.rcenvironment.core.component.execution.api.ComponentContext;
import de.rcenvironment.core.datamodel.api.DataType;
import de.rcenvironment.core.datamodel.api.TypedDatum;
import de.rcenvironment.core.datamodel.api.TypedDatumService;
import de.rcenvironment.core.datamodel.types.api.FileReferenceTD;
import de.rcenvironment.core.datamodel.types.api.VectorTD;
import de.rcenvironment.core.utils.common.TempFileServiceAccess;

/**
 * This class provides everything for running the dakota optimizer blackbox.
 * 
 * @author Sascha Zur
 */
public class DakotaAlgorithm extends OptimizerAlgorithmExecutor {

    private static final String APOSTROPHE = "'";

    private static final String INPUT_ARGUMENT = " -input ";

    private static final String RESTART_FILE_ARGUMENT = " -read_restart ";

    private static File dakotaExecutablePath = null;

    private Map<String, TypedDatum> outputValues;

    private Collection<String> input;

    private Map<String, MethodDescription> methodConfigurations;

    private String algorithm;

    private String[] variableOrderForWholeExecution;

    private int currentActiveSetVectorNumber = 0;

    private String[] constraintOrder;

    private Map<String, Double> upperMap;

    private Map<String, Double> lowerMap;

    private File dakotaInputFile;

    private FileReferenceTD dakotaInputFileReference;

    public DakotaAlgorithm() {}

    public DakotaAlgorithm(String algorithm, Map<String, MethodDescription> methodConfigurations, Map<String, TypedDatum> outputValues,
        Collection<String> input2, ComponentContext compContext, Map<String, Double> upperMap,
        Map<String, Double> lowerMap) {
        super(compContext, compContext.getWorkflowExecutionIdentifier() + ".in", "results.out");

        typedDatumFactory = compContext.getService(TypedDatumService.class).getFactory();
        this.algorithm = algorithm;
        this.outputValues = outputValues;
        this.input = input2;
        this.methodConfigurations = methodConfigurations;
        this.upperMap = upperMap;
        this.lowerMap = lowerMap;

        if (Boolean.parseBoolean(compContext.getConfigurationValue(OptimizerComponentConstants.USE_CUSTOM_DAKOTA_PATH))) {
            dakotaExecutablePath = new File(compContext.getConfigurationValue(OptimizerComponentConstants.CUSTOM_DAKOTA_PATH));
            if (!(dakotaExecutablePath.exists() && dakotaExecutablePath.isFile() && dakotaExecutablePath.canExecute())) {
                dakotaExecutablePath = null;
                LOGGER.info("Dakota binary not found at user specified location. Switching to default version.");
            }
        }

        if (dakotaExecutablePath == null) {
            try {
                if (OS.isFamilyWindows()) {
                    dakotaExecutablePath =
                        new File(TempFileServiceAccess.getInstance().createManagedTempDir("DakotaBinary"), "dakota/dakota.exe");

                } else if (OS.isFamilyUnix()) {
                    dakotaExecutablePath =
                        new File(TempFileServiceAccess.getInstance().createManagedTempDir("DakotaBinary"), "dakota/dakota");
                } else {
                    dakotaExecutablePath = null;
                }
            } catch (IOException e) {
                LOGGER.error("Could not create temp file for dakota binary", e);
            }
        }
        workingDir.setExecutable(true);
        if (!dakotaExecutablePath.exists()) {
            InputStream dakotaInput;
            if (OS.isFamilyWindows()) {
                dakotaInput = CommonBundleClasspathStub.class.getResourceAsStream("/resources/binaries/dakota.exe");
            } else if (OS.isFamilyUnix()) {
                dakotaInput = CommonBundleClasspathStub.class.getResourceAsStream("/resources/binaries/dakota");
            } else {
                dakotaInput = null;
            }
            if (dakotaInput != null) {
                try {
                    FileUtils.copyInputStreamToFile(dakotaInput, dakotaExecutablePath);
                    if (OS.isFamilyWindows()) {
                        String[] dllFiles = new String[] { "libifcoremd.dll", "libmmd.dll", "msvcp100.dll", "msvcr100.dll" };
                        for (String dll : dllFiles) {
                            FileUtils.copyInputStreamToFile(CommonBundleClasspathStub.class
                                .getResourceAsStream("/resources/binaries/" + dll), new File(dakotaExecutablePath.getParentFile(), dll));
                        }

                    }
                } catch (IOException e) {
                    initFailed.set(true);
                    startFailedException = new ComponentException("Could not copy dakota binary:", e);
                }
                dakotaExecutablePath.setExecutable(true);
            } else {
                initFailed.set(true);
                startFailedException = new ComponentException("Dakota binaries not found. Maybe Dakota is not installed?");
            }
            dakotaExecutablePath.setExecutable(true);
        }
    }

    @Override
    protected void prepareProblem() throws ComponentException {
        createScript();
        createDakotaInputFile();
    }

    @Override
    protected void writeInputFileforExternalProgram(Map<String, Double> functionVariables,
        Map<String, Double> functionVariablesGradients,
        Map<String, Double> constraintVariables,
        String outputFileName) throws IOException {
        File fo = new File(messageFromClient.getCurrentWorkingDir() + File.separatorChar + outputFileName);
        fo.createNewFile();
        LOGGER.debug(fo.getAbsolutePath());
        FileWriter fw2 = new FileWriter(fo);
        Queue<String> keyQueue = new LinkedList<String>();
        for (String key : functionVariables.keySet()) {
            if ((currentActiveSetVectorNumber & 1) != 0) { // if ASV == 2, no function evaluation is
                // needed
                String variableName = key;
                if (variableName.indexOf(OptimizerComponentConstants.OPTIMIZER_VECTOR_INDEX_SYMBOL) > 0 - 1
                    && compContext.getInputs().contains(variableName.substring(0,
                        variableName.indexOf(OptimizerComponentConstants.OPTIMIZER_VECTOR_INDEX_SYMBOL)))) {
                    variableName =
                        variableName.substring(0, variableName.indexOf(OptimizerComponentConstants.OPTIMIZER_VECTOR_INDEX_SYMBOL));
                }
                fw2.append(WHITESPACES);
                if (compContext.getInputMetaDataValue(variableName, META_GOAL).equals("Maximize")) { // Maximize
                    // Dakota only minimizes functions so for maximizing you need to minimize -f(x)
                    // see Dakota User Manuel Sec. 7.2.4
                    fw2.append("-");
                }
                if (functionVariables.get(key).isInfinite()) {
                    fw2.append("NaN");
                } else {
                    fw2.append(functionVariables.get(key) + " ");
                }
                fw2.append(IOUtils.LINE_SEPARATOR);
            }

            keyQueue.offer(key);
        }
        if (constraintOrder != null) {
            for (int constraintIterator = 0; constraintIterator < constraintOrder.length; constraintIterator++) {
                if ((currentActiveSetVectorNumber & 1) != 0) {
                    if (constraintVariables.get(constraintOrder[constraintIterator]).isInfinite()) {
                        fw2.append(WHITESPACES + "1e99 ");
                    } else {
                        fw2.append(WHITESPACES + constraintVariables.get(constraintOrder[constraintIterator]) + " ");
                    }
                    fw2.append(IOUtils.LINE_SEPARATOR);
                }
                keyQueue.offer(constraintOrder[constraintIterator]);
            }
        }
        while (!keyQueue.isEmpty()) {

            String key = keyQueue.poll();

            if ((currentActiveSetVectorNumber & 2) != 0) {
                fw2.append("[");
                for (int i = 0; i < variableOrderForWholeExecution.length; i++) {
                    String gradientName = GRADIENT_DELTA + key + DOT
                        + GRADIENT_DELTA + variableOrderForWholeExecution[i];
                    if (compContext.getInputDataType(gradientName) == DataType.Vector) {
                        for (int j = 0; j < Integer.valueOf(compContext.getOutputMetaDataValue(
                            gradientName.substring(gradientName.lastIndexOf(OptimizerComponentConstants.GRADIENT_DELTA) + 1),
                            OptimizerComponentConstants.METADATA_VECTOR_SIZE)); j++) {
                            if (functionVariablesGradients.containsKey(gradientName
                                + OptimizerComponentConstants.OPTIMIZER_VECTOR_INDEX_SYMBOL + j)) {
                                if (functionVariablesGradients.get(
                                    gradientName + OptimizerComponentConstants.OPTIMIZER_VECTOR_INDEX_SYMBOL + j).isInfinite()) {
                                    fw2.append(WHITESPACES + "1e99");
                                } else {
                                    fw2.append(WHITESPACES + functionVariablesGradients.get(
                                        gradientName + OptimizerComponentConstants.OPTIMIZER_VECTOR_INDEX_SYMBOL + j));
                                }
                            }
                        }
                    } else {
                        if (functionVariablesGradients.containsKey(gradientName)) {
                            if (functionVariablesGradients.get(gradientName).isInfinite()) {
                                fw2.append(WHITESPACES + "1e99");
                            } else {
                                fw2.append(WHITESPACES + functionVariablesGradients.get(
                                    gradientName));
                            }
                        }
                    }
                }

                fw2.append(" ]" + IOUtils.LINE_SEPARATOR);
            }

        }
        fw2.flush();
        fw2.close();
        // FileUtils.copyFile(fo, new File("c:\\gradiententest " + i++ + ".txt"));
    }

    @Override
    public void readOutputFileFromExternalProgram(Map<String, TypedDatum> outputValueMap) throws IOException {
        File paramsFile = null;
        for (File f : new File(messageFromClient.getCurrentWorkingDir()).listFiles()) {
            if (f.getAbsolutePath().endsWith("params.in")) {
                paramsFile = f;
            }
        }
        BufferedReader fr = new BufferedReader(new FileReader(paramsFile));
        String[] firstLine = fr.readLine().split("\\s+");
        int varCount = 0;
        if (firstLine != null) {
            try {
                varCount = Integer.parseInt(firstLine[1]);
            } catch (NumberFormatException e) {
                LOGGER.error("Could not read variable count", e);
            }
        }
        Map<String, Double> newOutput = new HashMap<String, Double>();
        for (int i = 0; i < varCount; i++) {
            String x = fr.readLine();
            String[] xStrg = x.split(" ");

            // Search for first not empty field
            int j = 0;
            while (xStrg != null && xStrg[j].isEmpty()) {
                j++;
            }
            newOutput.put(xStrg[j + 1], Double.parseDouble(xStrg[j]));

        }
        fr.readLine();
        // read active set number

        String[] asvLine = fr.readLine().split(" ");
        int j = 0;
        while (asvLine != null && asvLine[j].isEmpty()) {
            j++;
        }
        currentActiveSetVectorNumber = Integer.parseInt(asvLine[j]);
        fr.close();
        outputValueMap.clear();
        for (String key : newOutput.keySet()) {
            if (key.contains(OptimizerComponentConstants.OPTIMIZER_VECTOR_INDEX_SYMBOL)) {
                String variableName = key.substring(0, key.lastIndexOf(OptimizerComponentConstants.OPTIMIZER_VECTOR_INDEX_SYMBOL));
                if (compContext.getOutputs().contains(variableName) && compContext.getOutputDataType(variableName) == DataType.Vector) {
                    if (!outputValueMap.containsKey(variableName)) {
                        VectorTD vector =
                            typedDatumFactory.createVector(Integer.parseInt(compContext.getOutputMetaDataValue(variableName,
                                OptimizerComponentConstants.METADATA_VECTOR_SIZE)));
                        for (int i = 0; i < vector.getRowDimension(); i++) {
                            vector.setFloatTDForElement(
                                typedDatumFactory.createFloat(newOutput.get(variableName
                                    + OptimizerComponentConstants.OPTIMIZER_VECTOR_INDEX_SYMBOL + i)), i);
                        }
                        outputValueMap.put(variableName, vector);
                    }
                } else {
                    outputValueMap.put(key, typedDatumFactory.createFloat(newOutput.get(key)));
                }
            }
            outputValueMap.put(key, typedDatumFactory.createFloat(newOutput.get(key)));
        }
    }

    @Override
    public Map<String, Double> getOptimalDesignVariables() {
        File output = new File(workingDir.getAbsolutePath(), "consoleStdOutput.txt");
        Map<String, Double> results = new HashMap<String, Double>();
        try {
            List<String> outputLines = FileUtils.readLines(output);
            boolean readParameters = false;
            for (String s : outputLines) {
                if (s.startsWith(DakotaConstants.BEST_OBJECTIVE_STRING_FROM_DAKOTA)) {
                    readParameters = false;
                }
                if (readParameters) {
                    String[] resultLine = s.split("\\s+");
                    if (resultLine != null && resultLine.length == 3) {
                        results.put(resultLine[2], Double.parseDouble(resultLine[1]));
                    }
                }
                if (s.startsWith(DakotaConstants.BEST_PARAMETERS_STRING_FROM_DAKOTA)) {
                    readParameters = true;
                }

            }

        } catch (IOException e) {
            return null;
        }
        return results;
    }

    @Override
    public void run() {
        String command = dakotaExecutablePath.getAbsolutePath();
        if (compContext.getConfigurationValue(OptimizerComponentConstants.USE_RESTART_FILE) != null
            && Boolean.parseBoolean(compContext.getConfigurationValue(OptimizerComponentConstants.USE_RESTART_FILE))) {
            command +=
                RESTART_FILE_ARGUMENT
                    + "\"" + new File(compContext.getConfigurationValue(OptimizerComponentConstants.RESTART_FILE_PATH)).getAbsolutePath()
                    + "\" ";
        }
        command += INPUT_ARGUMENT;
        try {
            if (!initFailed.get()) {
                startProgram(command);
            }
        } catch (ComponentException e) {
            startFailed.set(true);
            startFailedException = e;
        }
        this.stop();
    }

    private void createDakotaInputFile() {
        try {
            createScript();
            createValueAndConstraintOrders();

            boolean isSurrogate =
                algorithm.split(COMMA).length > 1
                    && algorithm.split(COMMA)[0].equalsIgnoreCase(DakotaConstants.DAKOTA_SURROGATE_BASED_LOCAL_STRING);

            Map<String, String> valuesForSampleFile = new HashMap<String, String>();
            MethodDescription description = methodConfigurations.get(algorithm.split(COMMA)[0]);
            valuesForSampleFile.put(PLACEHOLDER_METHOD_CODE, description.getMethodCode());
            valuesForSampleFile.put(PLACEHOLDER_METHOD_PROPERTIES, createMethodsProperties(description.getCommonSettings())
                + NEWLINE
                + createMethodsProperties(description.getSpecificSettings()));
            if (isSurrogate) {
                MethodDescription description2 = methodConfigurations.get(algorithm.split(COMMA)[1]);
                valuesForSampleFile.put(PLACEHOLDER_METHOD_2_CODE, description2.getMethodCode());
                valuesForSampleFile.put(PLACEHOLDER_METHOD_2_PROPERTIES, createMethodsProperties(description2.getCommonSettings())
                    + NEWLINE
                    + createMethodsProperties(description2.getSpecificSettings()));
                MethodDescription description3 = methodConfigurations.get(algorithm.split(COMMA)[2]);
                valuesForSampleFile.put(PLACEHOLDER_METHOD_3_CODE, description3.getMethodCode());
                valuesForSampleFile.put(PLACEHOLDER_METHOD_3_PROPERTIES, createMethodsProperties(description3.getCommonSettings())
                    + NEWLINE
                    + createMethodsProperties(description3.getSpecificSettings()));
            }

            valuesForSampleFile.put(PLACEHOLDER_CONTINUOUS_DESIGN_COUNT, "" + countOutputs());
            String startValuesString = "";
            for (int i = 0; i < variableOrderForWholeExecution.length; i++) {
                if (outputValues.get(variableOrderForWholeExecution[i]).getDataType() == DataType.Vector) {
                    for (int j = 0; j < Integer.parseInt(compContext.getOutputMetaDataValue(variableOrderForWholeExecution[i],
                        OptimizerComponentConstants.METADATA_VECTOR_SIZE)); j++) {
                        startValuesString +=
                            (TABS + ((VectorTD) outputValues.get(variableOrderForWholeExecution[i])).getFloatTDOfElement(j));
                    }
                } else {
                    startValuesString += (TABS + outputValues.get(variableOrderForWholeExecution[i]));
                }
            }
            valuesForSampleFile.put(PLACEHOLDER_CDV_INITIAL_POINT, startValuesString);
            valuesForSampleFile.put(PLACEHOLDER_CDV_LOWER_BOUNDS, getDVBounds(META_LOWERBOUND));
            valuesForSampleFile.put(PLACEHOLDER_CDV_UPPER_BOUNDS, getDVBounds(META_UPPERBOUND));

            String dvNames = "";
            for (int i = 0; i < variableOrderForWholeExecution.length; i++) {
                if (outputValues.get(variableOrderForWholeExecution[i]).getDataType() == DataType.Vector) {
                    for (int j = 0; j < Integer.parseInt(compContext.getOutputMetaDataValue(variableOrderForWholeExecution[i],
                        OptimizerComponentConstants.METADATA_VECTOR_SIZE)); j++) {
                        dvNames += (TABS + APOSTROPHE + variableOrderForWholeExecution[i]
                            + OptimizerComponentConstants.OPTIMIZER_VECTOR_INDEX_SYMBOL + j) + APOSTROPHE;
                    }
                } else {
                    dvNames += (TABS + APOSTROPHE + variableOrderForWholeExecution[i] + APOSTROPHE);
                }
            }
            valuesForSampleFile.put(PLACEHOLDER_CDV_NAMES, dvNames);

            if (OS.isFamilyWindows()) {
                valuesForSampleFile.put(PLACEHOLDER_DRIVER_FOR_OS, "'dakotaBlackBox.bat'");
            } else if (OS.isFamilyUnix()) {
                valuesForSampleFile.put(PLACEHOLDER_DRIVER_FOR_OS, "'dakotaBlackBox.sh'");
            }

            valuesForSampleFile.put(PLACEHOLDER_WORKDIR, inputFileName + "workdir'");

            valuesForSampleFile.put(PLACEHOLDER_OBJECTIVE_FUNCTIONS_COUNT, "" + countInput(input));
            valuesForSampleFile.put(PLACEHOLDER_OBJECTIVES_WEIGHT, getWeightString());
            valuesForSampleFile.put(PLACEHOLDER_CONSTRAINT_COUNT, "" + countConstraint(input));
            valuesForSampleFile.put(PLACEHOLDER_CONSTRAINT_LOWER,
                getConstraintBoundString(META_LOWERBOUND));
            valuesForSampleFile.put(PLACEHOLDER_CONSTRAINT_UPPER,
                getConstraintBoundString(META_UPPERBOUND));
            valuesForSampleFile.put(PLACEHOLDER_GRADIENT_SECTION,
                getGradientString(methodConfigurations.get(algorithm.split(COMMA)[0])));
            if (isSurrogate) {
                valuesForSampleFile.put(PLACEHOLDER_GRADIENT_2_SECTION,
                    getGradientString(methodConfigurations.get(algorithm.split(COMMA)[2])));
            }
            String filePath = RESOURCES_DAKOTA_STANDARD_SAMPLE;
            if (isSurrogate) {
                filePath = RESOURCES_DAKOTA_SURROGATE_SAMPLE;
            }
            String content = replacePlaceholderInSamplefile(filePath, valuesForSampleFile);
            dakotaInputFile = new File(workingDir.getAbsolutePath() + File.separator + inputFileName);
            dakotaInputFile.createNewFile();
            FileUtils.writeStringToFile(dakotaInputFile, content);

            // FileUtils.copyFile(f, new File("C:/testInputBounds.in"));
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    private int countOutputs() {
        int count = 0;
        for (String e : outputValues.keySet()) {
            if (compContext.getOutputDataType(e) == DataType.Vector) {
                count += Integer.valueOf(compContext.getOutputMetaDataValue(e, OptimizerComponentConstants.METADATA_VECTOR_SIZE));
            } else {
                count++;
            }
        }
        return count;
    }

    private String replacePlaceholderInSamplefile(String filePath, Map<String, String> valuesForSampleFile) throws IOException {
        String content = IOUtils.toString(DakotaAlgorithm.class.getResourceAsStream(filePath));
        String[] splitted = content.split(NEWLINE);
        for (String key : valuesForSampleFile.keySet()) {
            if (valuesForSampleFile.get(key) == null || valuesForSampleFile.get(key).isEmpty()) {
                for (String s : splitted) {
                    if (s.contains(key)) {
                        content = content.replace(s, "");
                    }
                }
            } else {
                content = content.replaceAll(key, valuesForSampleFile.get(key));
            }
        }

        return content;
    }

    private void createValueAndConstraintOrders() {
        int position = 0;
        variableOrderForWholeExecution = new String[outputValues.size()];
        List<String> ordered = new LinkedList<String>(outputValues.keySet());
        Collections.sort(ordered);
        for (String key : ordered) {
            variableOrderForWholeExecution[position++] = key;
        }
        constraintOrder = new String[countConstraint(input)];
        int nextFreePosition = 0;
        List<String> orderedInput = new LinkedList<String>(input);
        Collections.sort(orderedInput);
        for (String e : orderedInput) {
            if (!e.contains(OptimizerComponentConstants.GRADIENT_DELTA)
                && compContext.getDynamicInputIdentifier(e).equals(ID_CONSTRAINT)) {
                constraintOrder[nextFreePosition++] = e;
            }
        }
    }

    private String getDVBounds(String boundType) {
        String result = "";
        Map<String, Double> boundValues = null;
        if (boundType == META_LOWERBOUND) {
            boundValues = lowerMap;
        } else {
            boundValues = upperMap;
        }
        for (int i = 0; i < variableOrderForWholeExecution.length; i++) {
            if (compContext.getOutputDataType(variableOrderForWholeExecution[i]) == DataType.Vector) {
                for (int j = 0; j < 2; j++) {
                    result +=
                        (TABS + boundValues.get(variableOrderForWholeExecution[i]
                            + OptimizerComponentConstants.OPTIMIZER_VECTOR_INDEX_SYMBOL + j));
                }
            } else {
                result += (TABS + boundValues.get(variableOrderForWholeExecution[i]));
            }
        }
        return result;
    }

    private String getWeightString() {
        String weights = "";
        for (String e : input) {
            if (compContext.getDynamicInputIdentifier(e).equals(ID_OBJECTIVE)
                && !e.contains(OptimizerComponentConstants.GRADIENT_DELTA)
                && !compContext.getInputMetaDataValue(e, META_WEIGHT).equals("-")) {
                if (compContext.getInputDataType(e) == DataType.Vector) {
                    int vectorSize =
                        Integer.parseInt(compContext.getInputMetaDataValue(e, OptimizerComponentConstants.METADATA_VECTOR_SIZE));
                    for (int i = 0; i < vectorSize; i++) {
                        weights += ("" + compContext.getInputMetaDataValue(e, META_WEIGHT) + " ");
                    }
                } else {
                    weights += ("" + compContext.getInputMetaDataValue(e, META_WEIGHT) + " ");
                }
            }
        }
        return weights;
    }

    private String getConstraintBoundString(String type) {
        String result = "";
        Map<String, Double> boundValues = null;
        if (type == META_LOWERBOUND) {
            boundValues = lowerMap;
        } else {
            boundValues = upperMap;
        }
        for (int i = 0; i < constraintOrder.length; i++) {
            for (String e : input) {
                if (e.equals(constraintOrder[i])
                    && compContext.getDynamicInputIdentifier(e).equals(ID_CONSTRAINT)) {
                    if (!e.contains(OptimizerComponentConstants.GRADIENT_DELTA) && compContext.getInputDataType(e) == DataType.Vector) {
                        int vectorSize =
                            Integer.parseInt(compContext.getInputMetaDataValue(e, OptimizerComponentConstants.METADATA_VECTOR_SIZE));
                        for (int j = 0; j < vectorSize; j++) {
                            result +=
                                ("" + boundValues.get(e + OptimizerComponentConstants.OPTIMIZER_VECTOR_INDEX_SYMBOL + j) + " ");
                        }
                    } else {
                        result += ("" + boundValues.get(e) + " ");
                    }
                }
            }
        }
        return result;
    }

    private String getGradientString(MethodDescription description) throws IOException {
        Map<String, String> gradientStrings = new HashMap<String, String>();
        String gradients = "";
        if (description.getResponsesSettings() != null
            && description.getResponsesSettings().get(GRADIENTS_KEY) != null) {
            Map<String, Map<String, String>> respSettings = description.getResponsesSettings();
            String gradientValue = respSettings.get(GRADIENTS_KEY).get(VALUE_KEY);
            if (gradientValue == null || gradientValue.equals("")) {
                gradientValue = respSettings.get(GRADIENTS_KEY).get(DEFAULT_VALUE_KEY);
            }
            if (gradientValue.equalsIgnoreCase(DakotaConstants.NUMERICAL_GRADIENTS)) {
                String intervalValue = respSettings.get(INTERVAL_TYPE_KEY).get(VALUE_KEY);
                if (intervalValue == null || intervalValue.equals("")) {
                    intervalValue = respSettings.get(INTERVAL_TYPE_KEY).get(DEFAULT_VALUE_KEY);
                }
                gradientStrings.put(GRADIANT_INTERVAL_TYPE, intervalValue);
                String stepSizeValue = respSettings.get(FD_GRADIENT_STEP_SIZE_KEY).get(VALUE_KEY);
                if (stepSizeValue == null || stepSizeValue.equals("")) {
                    stepSizeValue = respSettings.get(FD_GRADIENT_STEP_SIZE_KEY).get(DEFAULT_VALUE_KEY);
                }
                gradientStrings.put(GRADIENT_STEP_SIZE, stepSizeValue);

                String hessiansValue = respSettings.get(HESSIANS_KEY).get(VALUE_KEY);
                if (hessiansValue == null || hessiansValue.equals("")) {
                    hessiansValue = respSettings.get(HESSIANS_KEY).get(DEFAULT_VALUE_KEY);
                }
                gradientStrings.put(HESSIANS_VALUE, hessiansValue);
                String hessianIntervalValue = respSettings.get(INTERVAL_TYPE_HESSIAN_KEY)
                    .get(VALUE_KEY);
                if (hessianIntervalValue == null || hessianIntervalValue.equals("")) {
                    hessianIntervalValue = respSettings.get(INTERVAL_TYPE_HESSIAN_KEY)
                        .get(DEFAULT_VALUE_KEY);
                }
                gradientStrings.put(HESSIAN_INTERVALL, hessianIntervalValue);
                String hessianStepValue = respSettings.get(FD_HESSIAN_STEP_SIZE_KEY)
                    .get(VALUE_KEY);
                if (hessianStepValue == null || hessianStepValue.equals("")) {
                    hessianStepValue = respSettings.get(FD_HESSIAN_STEP_SIZE_KEY)
                        .get(DEFAULT_VALUE_KEY);
                }
                gradientStrings.put(HESSIAN_STEP_SIZE, hessianStepValue);
                gradientStrings.put(PARAMETER_GRADIENTS, replacePlaceholderInSamplefile(
                    RESOURCES_DAKOTA_GRADIENTS_SAMPLE, gradientStrings));
                if (!gradientStrings.get(HESSIANS_VALUE).equals(NO_HESSIANS)) {
                    gradientStrings.put(PARAMETER_HESSIANS,
                        replacePlaceholderInSamplefile(RESOURCES_DAKOTA_HESSIANS_SAMPLE, gradientStrings));
                } else {
                    gradientStrings.put(PARAMETER_HESSIANS, NO_HESSIANS);
                }
            } else {
                gradientStrings.put(PARAMETER_GRADIENTS, "analytic_gradients");
                gradientStrings.put(PARAMETER_HESSIANS, NO_HESSIANS);
            }
        } else {
            gradientStrings.put(PARAMETER_GRADIENTS, NO_GRADIENTS);
            gradientStrings.put(PARAMETER_HESSIANS, NO_HESSIANS);
        }
        gradients = replacePlaceholderInSamplefile(RESOURCES_DAKOTA_GRADIENTS_BASE_SAMPLE, gradientStrings);
        return gradients;
    }

    private String createMethodsProperties(Map<String, Map<String, String>> settingsType) throws IOException {
        String methodProperties = "";
        for (String attributeKey : settingsType.keySet()) {
            Map<String, Map<String, String>> settings = settingsType;
            if (settings.get(attributeKey).get(DONT_WRITE_KEY) == null
                || !settings.get(attributeKey).get(DONT_WRITE_KEY).equalsIgnoreCase(TRUE)) {
                String value = settings.get(attributeKey).get(VALUE_KEY);
                if (value == null || value.equals("")) {
                    value = settings.get(attributeKey).get(DEFAULT_VALUE_KEY);
                }
                if (settings.get(attributeKey).get(DATA_TYPE_KEY).equalsIgnoreCase(BOOL)) {
                    if (value.equalsIgnoreCase(TRUE)) {
                        methodProperties += (TABS + attributeKey);
                    }

                } else if (!value.equalsIgnoreCase("")) {

                    if ((settings.get(attributeKey).get(NOKEYWORD_KEY) == null
                        || !settings.get(attributeKey).get(NOKEYWORD_KEY)
                        .equalsIgnoreCase(TRUE))
                        && !(attributeKey.equalsIgnoreCase(OUTPUT)
                        && value.equalsIgnoreCase(NORMAL))) {
                        methodProperties += (TABS + attributeKey + " = ");
                    } else {
                        methodProperties += (" ");
                    }
                    if (!(attributeKey.equalsIgnoreCase(OUTPUT) && value.equalsIgnoreCase(NORMAL))) {
                        if (settings.get(attributeKey).get(DATA_TYPE_KEY)
                            .equalsIgnoreCase(STRING)) {
                            methodProperties += (BACKSLASH + value + BACKSLASH);
                        } else {
                            methodProperties += (value);
                        }
                    }
                }
                if (settings.get(attributeKey).get(NO_LINEBREAK_KEY) == null || !settings.get(attributeKey).
                    get(NO_LINEBREAK_KEY).equalsIgnoreCase(TRUE)) {
                    methodProperties += NEWLINE;
                }
            }
        }
        return methodProperties;
    }

    private void createScript() {
        try {
            if (OS.isFamilyWindows()) {
                // create bat file
                File bat = new File(workingDir.getAbsolutePath() + File.separator + "dakotaBlackBox.bat");
                FileWriter fw = new FileWriter(bat);

                fw.append(BACKSLASH + System.getProperty("java.home") + File.separator + "bin"
                    + File.separator + "javaw.exe\" -jar " + workingDir.getAbsolutePath() + File.separator
                    + "de.rcenvironment.components.optimizer.simulator.jar \"%CD%\" \"%1\"");
                fw.flush();
                fw.close();
            } else if (OS.isFamilyUnix()) {
                File sh = new File(workingDir + File.separator + "dakotaBlackBox.sh");
                FileWriter fw = new FileWriter(sh);
                fw.append("#!/bin/bash \n"
                    + System.getProperty("java.home") + File.separator + "bin"
                    + File.separator + "java"
                    + " -jar " + workingDir.getAbsolutePath() + File.separator
                    + "de.rcenvironment.components.optimizer.simulator.jar" + " `pwd` $1 \n");
                fw.append("echo $? \n");
                fw.flush();
                fw.close();
                sh.setExecutable(true);
            }
        } catch (IOException e) {
            LOGGER.error("", e);
        }
    }

    @Override
    public boolean getDerivativedNeeded() {
        return (currentActiveSetVectorNumber & 2) != 0;
    }

    @Override
    public void writeHistoryDataItem(OptimizerComponentHistoryDataItem historyItem) {
        if (dakotaInputFileReference == null) {
            try {
                dakotaInputFileReference =
                    compContext.getService(ComponentDataManagementService.class).createFileReferenceTDFromLocalFile(compContext,
                        dakotaInputFile,
                        "dakotaInput.in");
            } catch (IOException e) {
                LOGGER.error(e.getStackTrace());
            }
        }
        historyItem.setInputFileReference(dakotaInputFileReference.getFileReference());
        String restartFileReference;
        try {
            restartFileReference =
                compContext.getService(ComponentDataManagementService.class).createFileReferenceTDFromLocalFile(compContext,
                    new File(workingDir, "dakota.rst"),
                    "dakota.rst").getFileReference();
            historyItem.setRestartFileReference(restartFileReference);
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }
}
