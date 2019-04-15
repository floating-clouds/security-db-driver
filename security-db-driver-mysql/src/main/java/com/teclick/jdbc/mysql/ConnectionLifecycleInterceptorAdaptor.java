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

import com.mysql.cj.MysqlConnection;
import com.mysql.cj.jdbc.interceptors.ConnectionLifecycleInterceptor;
import com.mysql.cj.log.Log;

import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.Properties;

/**
 * Created by Lipeng on 2019/04/13.
 *
 * @author lipeng
 * @since 1.0.0
 */
public class ConnectionLifecycleInterceptorAdaptor implements ConnectionLifecycleInterceptor {

    @Override
    public ConnectionLifecycleInterceptor init(MysqlConnection conn, Properties props, Log log) {
        return null;
    }

    @SuppressWarnings("all")
    @Override
    public void destroy() {
    }

    @SuppressWarnings("all")
    @Override
    public void close() throws SQLException {
    }

    @Override
    public boolean commit() throws SQLException {
        return true;
    }

    @Override
    public boolean rollback() throws SQLException {
        return true;
    }

    @Override
    public boolean rollback(Savepoint arg0) throws SQLException {
        return true;
    }

    @Override
    public boolean setAutoCommit(boolean arg0) throws SQLException {
        return true;
    }

    @Override
    public boolean setCatalog(String arg0) throws SQLException {
        return true;
    }

    @Override
    public boolean transactionBegun() {
        return true;
    }

    @Override
    public boolean transactionCompleted() {
        return true;
    }

}
