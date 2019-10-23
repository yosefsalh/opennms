/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2019 The OpenNMS Group, Inc.
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

package org.opennms.smoketest.utils;

import java.util.concurrent.TimeUnit;

import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.model.HostConfig;

public class TestContainerUtils {

    private static final int NUM_CPUS_PER_CONTAINER = 2;

    /**
     * Set memory and CPU limits on the container in oder to help provide
     * more consistent timing across systems and runs.
     *
     * @param cmd reference to the create container command, obtain with withCreateContainerCmdModifier
     */
    public static void setGlobalMemAndCpuLimits(CreateContainerCmd cmd) {
        HostConfig hostConfig = cmd.getHostConfig();
        if (hostConfig == null) {
            hostConfig = new HostConfig();
        }
        hostConfig.withMemory(4 * 1024 * 1024 * 1024L); // Hard limit to 4GB of memory
        // cpu-period denotes the period in which container CPU utilisation is tracked
        hostConfig.withCpuPeriod(TimeUnit.MILLISECONDS.toMicros(100));
        // cpu-quota is the total amount of CPU time that a container can use in each cpu-period
        // say this is equal to the cpu-period set above, then the container can use 1 CPU at 100%
        // if this is 2x the cpu-period. then the container can use 2 CPUs at 100%, and so on...
        hostConfig.withCpuQuota(hostConfig.getCpuPeriod() * NUM_CPUS_PER_CONTAINER);
    }

}
