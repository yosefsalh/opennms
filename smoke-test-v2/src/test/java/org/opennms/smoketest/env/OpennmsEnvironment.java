/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2019-2019 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2019 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.smoketest.env;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.Properties;

import org.opennms.smoketest.utils.RestClient;

public class OpennmsEnvironment {

    private final Properties properties;

    public OpennmsEnvironment(Properties properties) {
        this.properties = Objects.requireNonNull(properties);
    }

    public URL getBaseUrlInternal() {
        try {
            return new URL(String.format("http://%s:%s/", properties.getProperty("opennms.host.internal"), properties.getProperty("opennms.port.web")));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public URL getBaseUrlExternal() {
        try {
            return new URL(String.format("http://%s:%s/", properties.getProperty("opennms.host.external"), properties.getProperty("opennms.port.web")));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public RestClient getRestClient() {
        return new RestClient(getBaseUrlExternal());
    }
}
