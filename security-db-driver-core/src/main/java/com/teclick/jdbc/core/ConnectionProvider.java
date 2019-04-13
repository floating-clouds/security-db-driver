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

import com.teclick.jdbc.driver.SecurityDriver;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by Lipeng on 2019/04/13.
 *
 * @author lipeng
 * @since 1.0.0
 */
public final class ConnectionProvider {

    private Driver driver;
    private String url;
    private Properties info;

    private String moduleName;
    private String user;
    private String host;
    private String port;
    private String dbName;

    private String password = null;

    public ConnectionProvider(Driver driver) {
        this.driver = driver;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setInfo(Properties info) {
        this.info = info;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getDbName() {
        return dbName;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUrl() {
        return url;
    }

    public Properties getInfo() {
        return info;
    }

    public String getUser() {
        return this.user;
    }

    public String getModuleName() {
        return this.moduleName;
    }

    public String getLastLoginSuccessKey() {
        return String.format("%s|%s|%s", moduleName, user, url);
    }

    public String getPasswordPropertyKey() {
        return String.format("%s|%s|%s|%s|%s", moduleName, user, host, port, dbName);
    }

    public Connection getConnection() throws SQLException {
        return getConnection(this.password);
    }

    public Connection getConnection(String password) throws SQLException {

        Properties props = new Properties();
        props.putAll(info);
        if (null == moduleName) {
            throw new SQLException("moduleName must not empty !!!");
        }

        props.put("moduleName", moduleName);

        if (password == null || "".equals(password)) {
            props.put("password", "");
        } else {
            props.put("password", password);
        }

        return this.driver.connect(url, props);
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isSecureMode() throws SQLException {
        if ((null == password) || ("".equals(password.trim())) || (SecurityDriver.PWD_PLACEHOLDER_NAME.equalsIgnoreCase(password.trim()))) {
            if (null == moduleName) {
                throw new SQLException("Module name not found, you must set the module name property.\n");
            }
        } else {
            return false;
        }

        return true;
    }
}
