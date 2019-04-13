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

import com.teclick.jdbc.SecurityDriverRuntimeException;
import com.teclick.jdbc.bridge.DriverBridge;
import com.teclick.jdbc.password.PasswordVendorFactory;
import com.teclick.jdbc.password.PasswordVendorFactoryImpl;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Lipeng on 2019/04/13.
 *
 * @author lipeng
 * @since 1.0.0
 */
public final class DriverDelegate {

    private static final Logger logger = Logger.getLogger(DriverDelegate.class.getName());

    private static final Map<String, DriverBridgeContext> driverContexts = new LinkedHashMap<>();

    public DriverDelegate() {
        init();
    }

    private static void init() {

        try {
            if (logger.isLoggable(Level.FINER)) {
                logger.finer("Load native db driver ...");
            }

            PasswordVendorFactory passwordStoreVendorFactory = PasswordVendorFactoryImpl.getInstance();

            ServiceLoader<DriverBridge> driverBridges = ServiceLoader.load(DriverBridge.class);
            for (DriverBridge driverBridge : driverBridges) {
                DriverBridgeContext driverBridgeContext = new DriverBridgeContext(driverBridge, passwordStoreVendorFactory);
                driverContexts.put(driverBridge.getName(), driverBridgeContext);
            }

            if (logger.isLoggable(Level.FINER)) {
                logger.finer("Load native db driver successfully.");
            }
        } catch (Exception e) {
            throw new SecurityDriverRuntimeException("SecurityDriver: Unable to load native driver, see reason below:" + e.getMessage(), e);
        }
    }

    @SuppressWarnings("all")
    public Connection connectWithDriver(String url, Properties info) throws SQLException {

        DriverBridgeContext driverBridgeContext = null;
        for (Map.Entry<String, DriverBridgeContext> entry : driverContexts.entrySet()) {
            try {
                if (entry.getValue().getDriver().acceptsURL(url)) {
                    driverBridgeContext = entry.getValue();
                    break;
                }
            } catch (SQLException ignored) { /* nothing to do, ignored */ }
        }

        if (null != driverBridgeContext) {
            return driverBridgeContext.connectWithDriver(url, info);
        }

        throw new SQLException("Unsupported, not found any driver bridge.");
    }

    @SuppressWarnings("all")
    public boolean acceptsURL(String url) throws SQLException {

        for (Map.Entry<String, DriverBridgeContext> entry : driverContexts.entrySet()) {
            try {
                if (entry.getValue().getDriver().acceptsURL(url)) {
                    return true;
                }
            } catch (SQLException ignored) { /* nothing to do, ignored */ }
        }

        return false;
    }

    @SuppressWarnings("all")
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {

        for (Map.Entry<String, DriverBridgeContext> entry : driverContexts.entrySet()) {
            try {
                Driver driver = entry.getValue().getDriver();
                if (driver.acceptsURL(url)) {
                    return driver.getPropertyInfo(url, info);
                }
            } catch (SQLException ignored) { /* nothing to do, ignored */ }
        }
        return new DriverPropertyInfo[0];
    }

}
