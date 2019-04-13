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

import java.sql.SQLException;

/**
 * Created by Lipeng on 2019/04/13.
 *
 * @author lipeng
 * @since 1.0.0
 */
public class LoginPolicyImpl implements LoginPolicy {

    @SuppressWarnings("FieldCanBeLocal")
    private static final int MAX_RETRY_TIMES = 3;

    private int errorCode;

    public LoginPolicyImpl(int errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public SQLException getWrappedException(SQLException e) {
        return new SQLException("Security driver: " + e.getMessage());
    }

    @Override
    public boolean shouldRetry(SQLException e) {
        return e.getErrorCode() == errorCode;
    }

    @Override
    public int getMaxRetryTimes() {
        return MAX_RETRY_TIMES;
    }
}
