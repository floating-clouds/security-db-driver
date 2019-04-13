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

import com.mysql.jdbc.Connection;
import com.teclick.jdbc.driver.SecurityDriver;

import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by Lipeng on 2019/04/13.
 *
 * @author lipeng
 * @since 1.0.0
 */
public class MySQLClientInfoInjector extends ConnectionLifecycleInterceptorAdaptor {

    @Override
    public void init(Connection conn, Properties props) throws SQLException {

        super.init(conn, props);

        String moduleName = props.getProperty(SecurityDriver.MODULE_KEY_NAME);
        if (null != moduleName) {
            conn.setClientInfo(SecurityDriver.MODULE_KEY_NAME, moduleName);
        }
    }

}
