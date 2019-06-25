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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.opennms.netmgt.events.api.EventIpcManager;
import org.opennms.netmgt.monitoring.url.persistence.api.SiteDao;
import org.opennms.netmgt.monitoring.url.persistence.api.SiteEntity;
import org.opennms.netmgt.scheduler.LegacyScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class UrlMonitoringService implements UrlMonitorScheduler {

    private static final Logger LOG = LoggerFactory.getLogger(UrlMonitoringService.class);

    @Autowired
    private SiteDao siteDao;

    @Autowired
    @Qualifier("eventIpcManager")
    private EventIpcManager eventIpcManager;

    private final LegacyScheduler scheduler;

    public UrlMonitoringService() {
        this.scheduler = new LegacyScheduler("URL Monitor", 30); // TODO MVR make configurable
    }

    @PostConstruct
    public void onInit() {
        siteDao.findAll().forEach(site -> schedule(site));
        scheduler.start();
    }

    @PreDestroy
    public void onDestroy() {
        scheduler.stop();
    }

    @Override
    public void schedule(SiteConfig siteConfig) {
        scheduler.schedule(siteConfig.getInterval(), new UrlMonitoringRunnable(siteConfig, new ResponseHandler(eventIpcManager, this)));
    }

    private void schedule(SiteEntity site) {
        schedule(new SiteConfig(site));
    }
}
