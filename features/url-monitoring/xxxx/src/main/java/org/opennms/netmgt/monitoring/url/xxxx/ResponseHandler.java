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

package org.opennms.netmgt.monitoring.url.xxxx;

import java.util.Date;
import java.util.Objects;

import org.opennms.netmgt.events.api.EventIpcManager;
import org.opennms.netmgt.model.events.EventBuilder;
import org.opennms.netmgt.xml.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ResponseHandler {

    private static final String UEI_INCORRECT_STATUS_CODE = "uei.opennms.org/monitor/url/incorrectStatusCode";
    private static final String UEI_TIMEOUT = "uei.opennms.org/monitor/url/timeout";

    private static final Logger LOG = LoggerFactory.getLogger(ResponseHandler.class);

    private final EventIpcManager eventIpcManager;
    private final UrlMonitorScheduler scheduler;

    public ResponseHandler(final EventIpcManager eventIpcManager, final UrlMonitorScheduler scheduler) {
        this.eventIpcManager = Objects.requireNonNull(eventIpcManager);
        this.scheduler = Objects.requireNonNull(scheduler);
    }

    public void onSuccess(final SiteConfig siteConfig, final int statusCode) {
        LOG.debug("Received statusCode {} for {}", statusCode, siteConfig.getUrl());
        reschedule(siteConfig);
    }

    public void onError(final SiteConfig siteConfig, final int statusCode, final String errorMessage) {
        LOG.error("Received statusCode {} for {} with error message {}", statusCode, siteConfig.getUrl(), errorMessage == null ? "undefined" : errorMessage);
        final Event event = new EventBuilder(UEI_INCORRECT_STATUS_CODE, UrlMonitoringService.class.getSimpleName(), new Date())
                .addParam("monitorUrlStatusCode", statusCode)
                .addParam("monitorUrlSite", siteConfig.getUrl())
                .addParam("monitorUrlId", siteConfig.getId())
                .getEvent();
        eventIpcManager.sendNow(event);
        reschedule(siteConfig);
    }

    public void onException(final SiteConfig siteConfig, final Exception ex) {
        LOG.error("An unexpected error occurred for URL {}: {}", siteConfig.getUrl(), ex.getMessage(), ex);
        reschedule(siteConfig);
    }

    public void reschedule(final SiteConfig siteConfig) {
        scheduler.schedule(siteConfig);
    }
}
