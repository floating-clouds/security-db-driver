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
package com.teclick.jdbc.bridge;

import com.teclick.jdbc.core.ConnectionProvider;
import com.teclick.jdbc.core.LoginPolicy;

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
public interface DriverBridge {

    String getName();

    Driver getDriver();

    void rewriteUrlParameters(String url, Properties info, ConnectionProvider connectionProvider) throws SQLException;

    void beforeConnect(ConnectionProvider connectionProvider) throws SQLException;

    void afterConnect(Connection connection) throws SQLException;

    LoginPolicy getLoginPolicy();
}
