/*
 * Copyright 2016-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.teclick.jdbc.driver;

import com.teclick.jdbc.SecurityDriverRuntimeException;
import com.teclick.jdbc.core.DriverDelegate;

import java.sql.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Lipeng on 2019/04/13.
 *
 * @author lipeng
 * @since 1.0.0
 */
public final class SecurityDriver implements Driver {

    public static final String MODULE_KEY_NAME = "moduleName";

    public static final String PWD_PLACEHOLDER_NAME = "{SECURITY-DB-PASSWORD}";

    private static final String JDBC_URL_PREFIX = "security:";

    private static final int DRIVER_MAJOR_VERSION = 1;

    private static final int DRIVER_MINOR_VERSION = 0;

    private static DriverDelegate driverDelegate = new DriverDelegate();

    private static final Logger logger = Logger.getLogger(SecurityDriver.class.getName());

    static {

        try {
            if (logger.isLoggable(Level.FINER)) {
                logger.finer("Registering SecurityDriver to java.sql.DriverManager...");
            }

            DriverManager.registerDriver(new SecurityDriver());

            if (logger.isLoggable(Level.FINER)) {
                logger.finer("SecurityDriver registered successfully.");
            }
        } catch (Exception e) {
            throw new SecurityDriverRuntimeException("SecurityDriver: Unable to register driver, see reason below: " + e.getMessage(), e);
        }
    }

    public SecurityDriver() throws SQLException {
        // nothing
    }

    @Override
    public int getMajorVersion() {
        return DRIVER_MAJOR_VERSION;
    }

    @Override
    public int getMinorVersion() {
        return DRIVER_MINOR_VERSION;
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        return driverDelegate.acceptsURL(getRealURL(url));
    }

    @Override
    public synchronized Connection connect(String url, Properties info) throws SQLException {

        if (logger.isLoggable(Level.FINER)) {
            logger.finer("SecurityDriver: connect to url [" + url + "]");
        }

        return driverDelegate.connectWithDriver(getRealURL(url), info);
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return logger;
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return driverDelegate.getPropertyInfo(getRealURL(url), info);
    }

    @Override
    public boolean jdbcCompliant() {
        return true;
    }

    private static String getRealURL(String url) throws SQLException {

        if (null == url || url.trim().isEmpty()) {
            throw new SQLException("Url is empty");
        }

        if (url.startsWith(JDBC_URL_PREFIX)) {
            return url.substring(JDBC_URL_PREFIX.length());
        }

        return url;
    }

}
