/*
 * Copyright (C) 2006-2010 DLR, Fraunhofer SCAI, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.rce.communication.service.internal;

import java.io.IOException;
import java.util.List;

import de.rcenvironment.core.utils.common.security.AllowRemoteAccess;
import de.rcenvironment.core.utils.common.security.MethodPermissionCheck;
import de.rcenvironment.rce.communication.NetworkContact;

/**
 * 
 * Test methods for the MethodCallerTest.
 * 
 * @author Doreen Seider
 */
public interface MethodCallerTestMethods {

    /**
     * Method with parameters and return value.
     * 
     * @param a first parameter.
     * @param b second parameter.
     * @return int.
     */
    int add(Integer a, Integer b);

    /**
     * Method with String return value.
     * 
     * @return String.
     */
    String getValue();

    /**
     * Dummy non-static function with return value.
     * 
     * @return Yeah.
     */
    String objectFunction();

    /**
     * Method that throws an exception.
     * 
     * @throws IOException Exception that is thrown.
     */
    void exceptionFunction() throws IOException;

    /**
     * Method that throws a RuntimeException.
     */
    void runtimeExceptionFunction();

    /**
     * A test function for super classes.
     * 
     * @param obj1 First object.
     * @param obj2 Second object.
     * 
     * @return A String.
     */
    String superclass(Object obj1, Object obj2);

    /**
     * Class with a list.
     * 
     * @param list The list.
     * 
     * @return A String.
     */
    String list(List<?> list);

    /**
     * Ambiguous function that overloads with another.
     * 
     * @param obj1 param1.
     * @param string param2.
     */
    void ambiguous(Object obj1, String string);

    /**
     * Ambiguous function that overloads with another.
     * 
     * @param obj1 param1.
     * @param string param2.
     */
    void ambiguous(String string, Object obj1);

    /**
     * Method that has a communication contact as parameter.
     * 
     * @param contact The communication contact.
     * @return The same contact.
     */
    NetworkContact proxyMethod(NetworkContact contact);

    /**
     * Test serializing null.
     * 
     * @param test A null object.
     * @return A null object.
     */
    Object nullTest(Object test);

    /**
     * Test handling of callback object.
     * 
     * @param test A callback object.
     */
    void callbackTest(Object test);

    /**
     * A method for testing {@link MethodPermissionCheck}. The implementation must be annotated with
     * a {@link AllowRemoteAccess} annotation.
     */
    void remoteCallAllowed();

}
