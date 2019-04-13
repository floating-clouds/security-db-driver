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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.logging.Logger;

/**
 * Created by Lipeng on 2019/04/13.
 *
 * @author lipeng
 * @since 1.0.0
 */
public class PasswordVendorFactoryImpl implements PasswordVendorFactory {

    private static final Logger logger = Logger.getLogger(PasswordVendorFactoryImpl.class.getName());

    private Map<String, PasswordVendor> passwordStores = new LinkedHashMap<>();

    public static PasswordVendorFactory getInstance() {
        return new PasswordVendorFactoryImpl();
    }

    private PasswordVendorFactoryImpl() {
        initPasswordStore();
    }

    private void initPasswordStore() {
        ServiceLoader<PasswordVendor> passwordStoreVendors = ServiceLoader.load(PasswordVendor.class);
        for (PasswordVendor passwordStoreVendor : passwordStoreVendors) {
            this.passwordStores.put(passwordStoreVendor.getName(), passwordStoreVendor);
        }
    }

    @SuppressWarnings("all")
    @Override
    public PasswordVendor getPasswordStoreVendor(String vendor) {
        PasswordVendor psv = null;
        if ((null == vendor) || (vendor.isEmpty())) {
            psv = passwordStores.get("default");
            logger.fine("passwordStores = " + psv.getClass().getCanonicalName());
            return psv;
        }

        psv = passwordStores.get(vendor);
        if (psv != null) {
            logger.fine("passwordStores = " + psv.getClass().getCanonicalName());
        }
        return psv;
    }

}
