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

package org.opennms.netmgt.monitoring.url.daemon;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

import org.opennms.netmgt.scheduler.ReadyRunnable;

class UrlMonitoringRunnable implements ReadyRunnable {

    private final SiteConfig siteConfig;

    private final ResponseHandler responseHandler;

    public UrlMonitoringRunnable(final SiteConfig siteConfig, final ResponseHandler responseHandler) {
        this.siteConfig = Objects.requireNonNull(siteConfig);
        this.responseHandler = Objects.requireNonNull(responseHandler);
        // TODO MVR verify siteConfig
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void run() {
        try {
            final URL url = new URL(siteConfig.getUrl());
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(siteConfig.getConnectTimeout());
            connection.setReadTimeout(siteConfig.getReadTimeout());
            connection.setRequestMethod("GET"); // TODO MVR do we need to make this configurable as well?
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode >= 200 && responseCode <= 299) {
                responseHandler.onSuccess(siteConfig, responseCode);
            } else {
                responseHandler.onError(siteConfig, responseCode, "TODO MVR");
            }
        } catch (Exception ex) {
            responseHandler.onException(siteConfig, ex);
        }
    }
}
