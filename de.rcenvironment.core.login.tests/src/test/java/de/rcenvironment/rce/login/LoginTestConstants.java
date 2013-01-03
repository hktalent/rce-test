/*
 * Copyright (C) 2006-2010 DLR, Fraunhofer SCAI, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.rce.login;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;

import org.globus.gsi.CertUtil;

import de.rcenvironment.commons.TempFileUtils;

/**
 * Constants for test setups.
 * 
 * @author Doreen Seider
 * @author Robert Mischke (merged key constants; changed to resource loading; javadoc)
 */
public final class LoginTestConstants {

    /**
     * Bundle name.
     */
    public static final String BUNDLE_SYMBOLIC_NAME = "de.rcenvironment.rce.login";

    /**
     * Full path of user 1 cert.
     */
    public static final String USER_1_CERTIFICATE_FILENAME;

    /**
     * Full path of user 1 key.
     */
    public static final String USER_1_KEY_FILENAME;

    /**
     * Full path of user 2 cert.
     */
    public static final String USER_2_CERTIFICATE_FILENAME;

    /**
     * Full path of user 2 key.
     */
    public static final String USER_2_KEY_FILENAME;

    /**
     * User 1 cert object.
     */
    public static final X509Certificate USER_1_CERTIFICATE;

    /**
     * User 2 cert object.
     */
    public static final X509Certificate USER_2_CERTIFICATE;

    private static final String USER_1_CERT_PATH = "/usercert_rainertester.pem";

    private static final String USER_1_KEY_PATH = "/userkey_rainertester.pem";

    private static final String USER_2_CERT_PATH = "/usercert_rainerhacker.pem";

    private static final String USER_2_KEY_PATH = "/userkey_rainerhacker.pem";

    static {
        try {
            USER_1_CERTIFICATE_FILENAME = createTempFilenameFromResource(USER_1_CERT_PATH);
            USER_1_KEY_FILENAME = createTempFilenameFromResource(USER_1_KEY_PATH);
            USER_1_CERTIFICATE = loadCertificateFromFilename(USER_1_CERTIFICATE_FILENAME);
            USER_2_CERTIFICATE_FILENAME = createTempFilenameFromResource(USER_2_CERT_PATH);
            USER_2_KEY_FILENAME = createTempFilenameFromResource(USER_2_KEY_PATH);
            USER_2_CERTIFICATE = loadCertificateFromFilename(USER_2_CERTIFICATE_FILENAME);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException();
        } catch (IOException e) {
            throw new RuntimeException("Error initializting test certificates", e);
        }
    }

    /**
     * Private constructor.
     */
    private LoginTestConstants() {

    }

    private static String createTempFilenameFromResource(String resourcePath) throws IOException {
        File tempFile =
            TempFileUtils.getDefaultInstance().writeInputStreamToTempFile(LoginTestConstants.class.getResourceAsStream(resourcePath));
        return tempFile.getAbsolutePath();
    }

    private static X509Certificate loadCertificateFromFilename(String filename) throws IOException, GeneralSecurityException,
        FileNotFoundException {
        return CertUtil.loadCertificate(filename);
    }

}
