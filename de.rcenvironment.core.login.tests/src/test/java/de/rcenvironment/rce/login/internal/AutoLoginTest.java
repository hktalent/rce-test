/*
 * Copyright (C) 2006-2010 DLR, Fraunhofer SCAI, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.rce.login.internal;

import org.junit.Before;
import org.junit.Test;

import de.rcenvironment.rce.configuration.testutils.MockConfigurationService;
import de.rcenvironment.rce.login.LoginConfiguration;
import de.rcenvironment.rce.login.LoginTestConstants;
import de.rcenvironment.rce.login.LoginMockFactory;

/**
 * Test cases for {@link CertificateAutoLogin}.
 * 
 * @author Doreen Seider
 */
public class AutoLoginTest {

    /**
     * Set up.
     * 
     * @throws Exception if an error occurs.
     **/
    @Before
    public void setUp() throws Exception {
        ServiceHandler serviceHandler = new ServiceHandler();
        serviceHandler.bindConfigurationService(new DummyConfigurationService());
        serviceHandler.bindAuthenticationService(LoginMockFactory.getInstance().getAuthenticationServiceMock());
        serviceHandler.bindNotificationService(LoginMockFactory.getInstance().getNotificationServiceMock());
        serviceHandler.activate(LoginMockFactory.getInstance().getBundleContextMock());
    }

    /** Test. */
    @Test
    public void testLoginOut() {
        CertificateAutoLogin autoLogin = new CertificateAutoLogin();
        autoLogin.login();
        autoLogin.logout();
    }

    /**
     * Test {@link ConfigurationService} implementation.
     * 
     * @author Doreen Seider
     */
    private class DummyConfigurationService extends MockConfigurationService.ThrowExceptionByDefault {

        @Override
        @SuppressWarnings("unchecked")
        public <T> T getConfiguration(String identifier, Class<T> clazz) {
            if (identifier.equals(LoginMockFactory.BUNDLE_SYMBOLIC_NAME)
                && clazz == LoginConfiguration.class) {
                LoginConfiguration config = new LoginConfiguration();
                config.setAutoLogin(true);
                config.setAutoLoginPassword(LoginMockFactory.PASSWORD);
                config.setCertificateFile(LoginTestConstants.USER_1_CERTIFICATE_FILENAME);
                config.setKeyFile(LoginTestConstants.USER_1_KEY_FILENAME);
                return (T) config;
            }
            return null;
        }

        @Override
        public String getAbsolutePath(String identifier, String path) {
            return path;
        }

    }
}
