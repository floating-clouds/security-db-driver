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
package com.teclick.jdbc.password;

import com.teclick.jdbc.core.ConnectionProvider;

/**
 * Created by Lipeng on 2019/04/13.
 *
 * @author lipeng
 * @since 1.0.0
 */
public interface PasswordVendor {

    String getVersion();

    String getName();

    void getPassword(ConnectionProvider provider, int retryIndex, boolean refresh);
}
