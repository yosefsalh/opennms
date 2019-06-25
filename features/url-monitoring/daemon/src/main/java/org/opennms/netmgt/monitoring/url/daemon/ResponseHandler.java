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

import java.util.Objects;

import org.opennms.netmgt.events.api.EventIpcManager;
import org.opennms.netmgt.model.events.EventBuilder;
import org.opennms.netmgt.monitoring.url.persistence.api.SiteDao;
import org.opennms.netmgt.monitoring.url.persistence.api.SiteEntity;
import org.opennms.netmgt.monitoring.url.persistence.api.SiteResultEntity;
import org.opennms.netmgt.xml.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionOperations;

class ResponseHandler {

    private static final String UEI_INCORRECT_STATUS_CODE = "uei.opennms.org/monitor/url/incorrectStatusCode";
    private static final String UEI_TIMEOUT = "uei.opennms.org/monitor/url/timeout";

    private static final Logger LOG = LoggerFactory.getLogger(ResponseHandler.class);

    private final EventIpcManager eventIpcManager;
    private final UrlMonitorScheduler scheduler;
    private final SiteDao siteDao;
    private final TransactionOperations transactionTemplate;

    public ResponseHandler(final EventIpcManager eventIpcManager, final UrlMonitorScheduler scheduler, final SiteDao siteDao, final TransactionOperations transactionTemplate) {
        this.eventIpcManager = Objects.requireNonNull(eventIpcManager);
        this.scheduler = Objects.requireNonNull(scheduler);
        this.siteDao = Objects.requireNonNull(siteDao);
        this.transactionTemplate = Objects.requireNonNull(transactionTemplate);
    }

    public void onSuccess(final SiteConfig siteConfig, final Response response) {
        LOG.debug("Received statusCode {} for {}", response.getStatusCode(), siteConfig.getUrl());
        try {
            addResultAndPersist(siteConfig.getSiteId(), response);
        } finally {
            reschedule(siteConfig);
        }
    }

    public void onError(final SiteConfig siteConfig, final Response response) {
        LOG.error("Received statusCode {} for {} with error message {}", response.getStatusCode(), siteConfig.getUrl(), response.getErrorMessage() == null ? "undefined" : response.getErrorMessage());
        try {
            addResultAndPersist(siteConfig.getSiteId(), response);

            final Event event = new EventBuilder(UEI_INCORRECT_STATUS_CODE, UrlMonitord.NAME)
                    .addParam("monitorUrlStatusCode", response.getStatusCode())
                    .addParam("monitorUrlSite", siteConfig.getUrl())
                    .addParam("monitorUrlId", siteConfig.getSiteId())
                    .getEvent();
            eventIpcManager.sendNow(event);
        } finally {
            reschedule(siteConfig);
        }
    }

    public void onException(final SiteConfig siteConfig, final Exception ex) {
        LOG.error("An unexpected error occurred for URL {}: {}", siteConfig.getUrl(), ex.getMessage(), ex);
        reschedule(siteConfig);
    }

    public void reschedule(final SiteConfig siteConfig) {
        scheduler.schedule(siteConfig);
    }

    private void addResultAndPersist(final int siteId, final Response response) {
        transactionTemplate.execute(status -> {
            final SiteEntity siteEntity = siteDao.get(siteId);

            final SiteResultEntity resultEntity = new SiteResultEntity();
            resultEntity.setResponseCode(response.getStatusCode());
            resultEntity.setResponseTime(response.getResponseTime());
            if (response.isSuccess()) {
//            resultEntity.setMessage(response.getResponseMessage()); // TODO MVR
            } else {
                resultEntity.setErrorMessage(response.getErrorMessage());
            }
            siteEntity.addResult(resultEntity);
            siteDao.saveOrUpdate(siteEntity);
            return null;
        });
    }
}
