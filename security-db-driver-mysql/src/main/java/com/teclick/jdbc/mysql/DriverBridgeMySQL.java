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
package com.teclick.jdbc.mysql;

import com.teclick.jdbc.bridge.AbstractDriverBridge;
import com.teclick.jdbc.core.ConnectionProvider;
import com.teclick.jdbc.core.LoginPolicy;
import com.teclick.jdbc.core.LoginPolicyImpl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Lipeng on 2019/04/13.
 *
 * @author lipeng
 * @since 1.0.0
 */
public class DriverBridgeMySQL extends AbstractDriverBridge {

    private static final Logger logger = Logger.getLogger(DriverBridgeMySQL.class.getName());

    private static final String CONNECTION_LIFECYCLE_INTERCEPTORS = "connectionLifecycleInterceptors";

    private static final String MYSQL_INJECTOR = "com.teclick.jdbc.mysql.MySQLClientInfoInjector";

    private static final int MYSQL_LOGIN_ERROR_RETRY = 1045;

    static {
        try {
            driver = new com.mysql.jdbc.Driver();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public String getName() {
        return DriverBridgeMySQL.class.getName();
    }

    @SuppressWarnings("all")
    @Override
    public void rewriteUrlParameters(String url, Properties info, ConnectionProvider connectionProvider) throws SQLException {
        Properties properties = ((com.mysql.jdbc.Driver) driver).parseURL(url, info);

        for (Map.Entry entry : properties.entrySet()) {
            String name = entry.getKey().toString();
            if (name.equalsIgnoreCase("host")) {
                connectionProvider.setHost(entry.getValue().toString());
            } else if (name.equalsIgnoreCase("port")) {
                connectionProvider.setPort(entry.getValue().toString());
            } else if (name.equalsIgnoreCase("dbname")) {
                connectionProvider.setDbName(entry.getValue().toString());
            } else if (name.equalsIgnoreCase("moduleName")) {
                connectionProvider.setModuleName(entry.getValue().toString());
            } else if (name.equalsIgnoreCase("user")) {
                connectionProvider.setUser(entry.getValue().toString());
            } else if (name.equalsIgnoreCase("password")) {
                connectionProvider.setPassword(entry.getValue().toString());
            }
        }

        /* 以下内容用于修正URL，把module name移除和user、password放入属性 */
        String result;
        StringBuilder fixedUrl = new StringBuilder();

        int index = url.indexOf("?");
        if (index > 0) {
            fixedUrl.append(url.substring(0, index));
            String props = url.substring(index + 1);

            char c = '*';
            StringTokenizer tokenizer = new StringTokenizer(props, "&");
            while (tokenizer.hasMoreTokens()) {
                String keyValuePair = tokenizer.nextToken();
                index = keyValuePair.indexOf("=");
                String name = null;
                String value = null;
                if (index != -1) {
                    name = keyValuePair.substring(0, index);
                    if (index + 1 < keyValuePair.length()) {
                        value = keyValuePair.substring(index + 1);
                    }
                }

                if (value != null && value.length() > 0 && name.length() > 0) {

                    if (name.equalsIgnoreCase("user")) {
                        info.put(name, value);
                        continue;
                    }

                    if (name.equalsIgnoreCase("password")) {
                        info.put(name, value);
                        continue;
                    }

                    if (name.equalsIgnoreCase("moduleName")) {
                        continue;
                    }

                    if (c == '&') {
                        fixedUrl.append(c);
                    } else {
                        fixedUrl.append('?');
                        c = '&';
                    }
                    fixedUrl.append(name).append("=").append(value);
                }
            }

            result = fixedUrl.toString();
        } else {
            result = url;
        }

        connectionProvider.setInfo(info);
        connectionProvider.setUrl(result);
    }

    @Override
    public void beforeConnect(ConnectionProvider connectionProvider) throws SQLException {

        String moduleName = connectionProvider.getModuleName();

        if (null != moduleName) {

            String cli = connectionProvider.getInfo().getProperty(CONNECTION_LIFECYCLE_INTERCEPTORS);
            if (null != cli) {
                connectionProvider.getInfo().setProperty(CONNECTION_LIFECYCLE_INTERCEPTORS, cli + "," + MYSQL_INJECTOR);
            } else {
                connectionProvider.getInfo().setProperty(CONNECTION_LIFECYCLE_INTERCEPTORS, MYSQL_INJECTOR);
            }
        }
    }

    @Override
    public void afterConnect(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            try (ResultSet ignored = stmt.executeQuery("SET SESSION wait_timeout = 28800;")) {
                if (!connection.getAutoCommit()) {
                    connection.commit();
                }
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
        }
    }

    @Override
    public LoginPolicy getLoginPolicy() {
        return new LoginPolicyImpl(MYSQL_LOGIN_ERROR_RETRY);
    }
}
