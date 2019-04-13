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
package com.teclick.jdbc.core;

import com.teclick.jdbc.bridge.DriverBridge;
import com.teclick.jdbc.password.PasswordVendor;
import com.teclick.jdbc.password.PasswordVendorFactory;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Created by Lipeng on 2019/04/13.
 *
 * @author lipeng
 * @since 1.0.0
 */
class DriverBridgeContext {

    private static final Logger logger = Logger.getLogger(DriverBridgeContext.class.getName());

    private DriverBridge driverBridge;

    private PasswordVendorFactory passwordStoreVendorFactory;

    private PasswordVendor passwordStoreVendor;

    private Map<String, Integer> lastSuccessIndex = new HashMap<>();

    private LoginPolicy loginPolicy = null;

    DriverBridgeContext(DriverBridge driverBridge, PasswordVendorFactory passwordStoreVendorFactory) {
        this.driverBridge = driverBridge;
        this.passwordStoreVendorFactory = passwordStoreVendorFactory;
    }

    Driver getDriver() {
        return driverBridge.getDriver();
    }

    Connection connectWithDriver(String url, Properties info) throws SQLException {

        ConnectionProvider connectionProvider = new ConnectionProvider(getDriver());
        driverBridge.rewriteUrlParameters(url, info, connectionProvider);

        Connection conn;
        driverBridge.beforeConnect(connectionProvider);
        if (connectionProvider.isSecureMode()) {
            conn = connectWithDriver(connectionProvider);
        } else {
            conn = connectionProvider.getConnection();
        }
        driverBridge.afterConnect(conn);

        return conn;
    }

    @SuppressWarnings("all")
    private Connection connectWithDriver(ConnectionProvider connectionProvider) throws SQLException {

        if (null == passwordStoreVendor) {
            String vendor = connectionProvider.getInfo().getProperty("passwordVendorClass", "default");
            passwordStoreVendor = passwordStoreVendorFactory.getPasswordStoreVendor(vendor);
        }

        String lastSuccessUrl = connectionProvider.getLastLoginSuccessKey();
        Integer lastSuccess = lastSuccessIndex.get(lastSuccessUrl);

        if (lastSuccess != null) {
            try {
                return getConnectionForPasswordIndex(connectionProvider, lastSuccess, false);
            } catch (SQLException e) {
                // nothing to do
            }
        }

        if (null == this.loginPolicy) {
            loginPolicy = driverBridge.getLoginPolicy();
        }
        //
        // We loginPolicy up to MAX_RETRIES times, unless we get a non-login
        // related SQLException or a SecurityDriver-related exception.
        //
        for (int retryIndex = 0; retryIndex < this.loginPolicy.getMaxRetryTimes(); retryIndex++) {
            try {
                if ((lastSuccess != null) && (lastSuccess == retryIndex)) {
                    continue; // already tried this
                }
                Connection conn = getConnectionForPasswordIndex(connectionProvider, retryIndex, retryIndex == 0);
                lastSuccessIndex.put(lastSuccessUrl, retryIndex);

                return conn;
            } catch (SQLException e) {
                if (!this.loginPolicy.shouldRetry(e)) {
                    throw this.loginPolicy.getWrappedException(e);
                }
            }
        }

        logger.fine("Retried maximum of " + this.loginPolicy.getMaxRetryTimes() + " times, connection unsuccessful.");

        throw loginPolicy.getWrappedException(new SQLException("Connection failure: username / passwords invalid"));
    }

    @SuppressWarnings("all")
    private Connection getConnectionForPasswordIndex(ConnectionProvider connector, int retryIndex, boolean refresh) throws SQLException {

        passwordStoreVendor.getPassword(connector, retryIndex, refresh);

        logger.fine("Get Connection for user [" + connector.getUser() + "] and password index [" + (retryIndex) + "]");

        return connector.getConnection();
    }


}
