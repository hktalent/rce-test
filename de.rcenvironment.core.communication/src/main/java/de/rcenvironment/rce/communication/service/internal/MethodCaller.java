/*
 * Copyright (C) 2006-2010 DLR, Fraunhofer SCAI, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.rce.communication.service.internal;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.LogFactory;

import de.rcenvironment.core.utils.common.security.MethodPermissionCheck;
import de.rcenvironment.rce.communication.CommunicationException;

/**
 * Class that calls a specified method.
 * 
 * This class does: - Detect interfaces and super classes of parameters. - Caching of method
 * detection for faster access.
 * 
 * This class has some restrictions: - Primitives are not supported. - It only detects public
 * functions. - It can not handle null arguments.
 * 
 * For a more detailed overview see: - http://www.ddj.com/dept/java/184403978
 * 
 * @author Heinrich Wendel
 * @author Doreen Seider
 * @author Robert Mischke (added MethodPermissionCheck)
 */
public final class MethodCaller {

    private static final String ERROR_METHOD_CALL_FAILED = "Could not call method: ";

    private static Map<String, Method> cache = new HashMap<String, Method>();

    private MethodCaller() {}

    /**
     * Calls the method of the service.
     * 
     * @param service The service with the method to call.
     * @param methodName The name of method to call.
     * @param parameters The parameters of the method.
     * @return the return object of the call.
     * @throws CommunicationException if calling the method failed.
     */
    public static Object callMethod(Object service, String methodName, List<? extends Serializable> parameters)
        throws CommunicationException {
        // delegate without using a MethodPermissionCheck; maintains old behavior
        return callMethod(service, methodName, parameters, null);
    }

    /**
     * Calls the method of the service. An optional {@link MethodPermissionCheck} is performed
     * before execution to see if access to this method is allowed.
     * 
     * @param service The service with the method to call.
     * @param methodName The name of method to call.
     * @param parameters The parameters of the method.
     * @param permissionCheck an optional permission check, or null to disable
     * @return the return object of the call.
     * @throws CommunicationException if calling the method failed.
     */
    @SuppressWarnings("unchecked")
    public static Object callMethod(Object service, String methodName, List<? extends Serializable> parameters,
        MethodPermissionCheck permissionCheck)
        throws CommunicationException {

        // Extract parameters
        Class<? extends Serializable>[] parameterTypes = null;
        Object[] parameterList = null;
        if (parameters != null) {
            parameterTypes = new Class[parameters.size()];
            parameterList = new Object[parameters.size()];
            for (int i = 0; i < parameters.size(); i++) {
                if (parameters.get(i) == null) {
                    parameterTypes[i] = Serializable.class;
                } else {
                    parameterTypes[i] = parameters.get(i).getClass();
                }
                parameterList[i] = parameters.get(i);
            }
        } else {
            parameterTypes = new Class[0];
            parameterList = new Class[0];
        }

        String uid = createUniqueIdentifier(service.getClass(), methodName, parameterTypes);
        Method method = null;

        // Check in cache
        if (cache.containsKey(uid)) {
            method = cache.get(uid);

            // Otherwise do a new lookup
        } else {

            method = lookupMethod(service.getClass(), methodName, parameterTypes);
            if (method == null) {
                throw new CommunicationException(ERROR_METHOD_CALL_FAILED + uid + " - it could not be not found.");
            }

            // Add to cache
            cache.put(uid, method);
        }

        if (permissionCheck != null) {
            if (!permissionCheck.checkPermission(method)) {
                // NOTE: security is not enforced yet; only log a warning for now
                LogFactory.getLog(MethodCaller.class).warn("RPC permission check failed for " + method);
                // throw new CommunicationException(ERROR_METHOD_CALL_FAILED + uid +
                // " - permission denied");
            }
        }

        try {
            return method.invoke(service, parameterList);
        } catch (IllegalArgumentException e) {
            throw new CommunicationException(ERROR_METHOD_CALL_FAILED + uid + " - invalid arguments.");
        } catch (IllegalAccessException e) {
            throw new CommunicationException(ERROR_METHOD_CALL_FAILED + uid + " - it could not be not accessed.");
        } catch (InvocationTargetException e) {
            return e.getCause();
        }
    }

    /**
     * Searches for a matching method.
     * 
     * See JLS 15.11.2 for the definition.
     * 
     * @param clazz The class to call.
     * @param javaMethodName The name of the method.
     * @param parameterTypes The array of parameter types.
     * 
     * @return A matching method or null.
     */
    private static Method lookupMethod(Class<?> clazz, String javaMethodName, Class<? extends Serializable>[] parameterTypes) {

        Method method = null;

        try {
            method = clazz.getMethod(javaMethodName, parameterTypes);

        } catch (NoSuchMethodException e) {

            if (parameterTypes.length != 0) {

                List<Method> methods = new ArrayList<Method>();
                // otherwise lookup
                for (Method candidate : clazz.getMethods()) {
                    if (javaMethodName.equals(candidate.getName()) && parameterTypesMatch(candidate.getParameterTypes(), parameterTypes)) {
                        methods.add(candidate);
                    }
                }

                if (methods.size() > 0) {
                    method = mostSpecificMethod(methods);
                }
            }
        }

        return method;
    }

    /**
     * Checks whether the parameterTypes are matching.
     * 
     * @param parameterTypesOne The first list of types.
     * @param parameterTypesTwo The second list of types.
     * 
     * @return true or false.
     */
    private static boolean parameterTypesMatch(Class<?>[] parameterTypesOne, Class<? extends Serializable>[] parameterTypesTwo) {
        if (parameterTypesOne.length == parameterTypesTwo.length) {
            for (int i = 0; i < parameterTypesOne.length; i++) {
                if (!parameterTypesOne[i].isAssignableFrom(parameterTypesTwo[i])) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Checks for the best candidate in the list.
     * 
     * See JLS 15.11.2.2 for the definition.
     * 
     * @param methods The list of methods.
     * 
     * @return The best candidate or null if there is no best candidate.
     */
    private static Method mostSpecificMethod(List<Method> methods) {

        List<Method> methodsToRemove = new ArrayList<Method>();

        for (int i = 0; i < methods.size(); i++) {
            for (int j = 0; j < methods.size(); j++) {
                if (i != j && moreSpecific(methods.get(i), methods.get(j))) {
                    methodsToRemove.add(methods.get(j));
                }
            }
        }

        methods.removeAll(methodsToRemove);

        if (methods.size() == 1) {
            return methods.get(0);
        } else {
            return null;
        }
    }

    /**
     * Checks if methodOne is a better candidate than methodTwo.
     * 
     * @param methodOne The first method.
     * @param methodTwo The second method.
     * 
     * @return true or false.
     */
    private static boolean moreSpecific(Method methodOne, Method methodTwo) {

        Class<?>[] parameterTypesOne = methodOne.getParameterTypes();
        Class<?>[] parameterTypesTwo = methodTwo.getParameterTypes();

        for (int i = 0; i < parameterTypesOne.length; i++) {
            if (!parameterTypesTwo[i].isAssignableFrom(parameterTypesOne[i])) {
                return false;
            }
        }

        return true;
    }

    /**
     * Creates an unique identifier for cashing.
     * 
     * @param clazz The class to call.
     * @param javaMethodName The name of the method.
     * @param parameterTypes The array of parameter types.
     * 
     * @return A hash String.
     */
    private static String createUniqueIdentifier(Class<?> clazz, String javaMethodName, Class<? extends Serializable>[] parameterTypes) {
        String hash = "";
        hash += clazz.getCanonicalName();
        hash += "." + javaMethodName;
        hash += "(";
        for (int i = 0; i < parameterTypes.length; i++) {
            hash += parameterTypes[i].getCanonicalName();
            if (i < parameterTypes.length - 1) {
                hash += ", ";
            }
        }
        hash += ")";
        return hash;
    }
}
