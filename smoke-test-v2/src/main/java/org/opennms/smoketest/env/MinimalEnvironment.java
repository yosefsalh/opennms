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

import java.io.IOException;
import java.util.Properties;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

public class MinimalEnvironment {

    private final Properties properties;

    public MinimalEnvironment() {
        this(new ClassPathResource("/ui-tests/test.properties"));
    }

    public MinimalEnvironment(final Resource resource) {
        try {
            this.properties = PropertiesLoaderUtils.loadProperties(resource);
            properties.keySet().stream()
                .map(key -> (String) key)
                .forEach(key -> {
                    // Overwrite value if it exists
                    final String systemProperty = System.getProperty(key);
                    if (systemProperty != null && !"".equals(systemProperty)) {
                        properties.put(key,systemProperty);
                    }
                });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getDockerBridgeName() {
        return properties.getProperty("docker.bridge.name");
    }

    public OpennmsEnvironment opennms() {
        return new OpennmsEnvironment(properties);
    }

    public PostgresEnvironment postgres() {
        return new PostgresEnvironment() {};
    }
}
