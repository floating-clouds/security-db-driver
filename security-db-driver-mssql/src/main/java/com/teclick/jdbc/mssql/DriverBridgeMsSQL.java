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
package com.teclick.jdbc.mssql;

import com.microsoft.sqlserver.jdbc.SQLServerDriver;
import com.teclick.jdbc.bridge.AbstractDriverBridge;
import com.teclick.jdbc.core.ConnectionProvider;
import com.teclick.jdbc.core.LoginPolicy;
import com.teclick.jdbc.core.LoginPolicyImpl;
import com.teclick.jdbc.driver.SecurityDriver;

import java.sql.SQLException;

/**
 * Created by Lipeng on 2019/04/13.
 *
 * @author lipeng
 * @since 1.0.0
 */
public final class DriverBridgeMsSQL extends AbstractDriverBridge {

    private static final int SQLSERVER_LOGIN_ERROR_RETRY = 18456;

    static {
        driver = new SQLServerDriver();
    }

    @Override
    public String getName() {
        return DriverBridgeMsSQL.class.getName();
    }

    @Override
    public void beforeConnect(ConnectionProvider connectionProvider) throws SQLException {
        String moduleName = connectionProvider.getInfo().getProperty(SecurityDriver.MODULE_KEY_NAME);

        if (null != moduleName) {
            connectionProvider.getInfo().setProperty("application", moduleName);
        }
    }

    @Override
    public LoginPolicy getLoginPolicy() {
        return new LoginPolicyImpl(SQLSERVER_LOGIN_ERROR_RETRY);
    }
}
