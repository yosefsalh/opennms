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

package org.opennms.netmgt.monitoring.url.persistence.impl;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennms.core.test.OpenNMSJUnit4ClassRunner;
import org.opennms.core.test.db.annotations.JUnitTemporaryDatabase;
import org.opennms.netmgt.monitoring.url.persistence.api.SiteDao;
import org.opennms.netmgt.monitoring.url.persistence.api.SiteEntity;
import org.opennms.test.JUnitConfigurationEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(OpenNMSJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/META-INF/opennms/applicationContext-commonConfigs.xml",
        "classpath:/META-INF/opennms/applicationContext-minimal-conf.xml",
        "classpath:/META-INF/opennms/applicationContext-soa.xml",
        "classpath:/META-INF/opennms/applicationContext-dao.xml",
        "classpath*:/META-INF/opennms/component-dao.xml",
        "classpath:/META-INF/opennms/mockEventIpcManager.xml"})
@JUnitConfigurationEnvironment
@JUnitTemporaryDatabase
@Transactional
public class SiteDaoImplIT {

    @Autowired
    private SiteDao siteDao;

    @Test
    public void verifyCRUD() {
        // Verify empty
        assertThat(siteDao.findAll(), hasSize(0));

        // Create site
        final SiteEntity site = new SiteEntity();
        site.setInterval(1, TimeUnit.MINUTES);
//        site.setReadTimeout(10);  // TODO MVR do we want to set this settings as well?
//        site.setConnectTimeout(10); // TODO MVR do we want to set this settings as well?
        site.setUrl("http://opennms.org");

        siteDao.save(site);

        // Verify Creation
        assertThat(siteDao.findAll(), hasSize(1));

        final SiteEntity persistedSite = siteDao.get(site.getId());
        assertEquals(site.getId(), persistedSite.getId());
        assertEquals(site.getUrl(), persistedSite.getUrl());
        assertEquals(site.getInterval(), persistedSite.getInterval());

        // Update Site
        persistedSite.setUrl("dummy URL"); // TODO MVR add validation
        persistedSite.setInterval(5, TimeUnit.MINUTES);
//        updatedSite.setReadTimeout(200); // TODO MVR
//        updatedSite.setConnectTimeout(300); // TODO MVR

        siteDao.update(persistedSite);

        // Verify Update
        final SiteEntity updatedSite = siteDao.get(site.getId());
        assertEquals("dummy URL", updatedSite.getUrl());
        assertEquals(5 * 60 * 1000, updatedSite.getInterval());

        // Delete
        siteDao.delete(persistedSite);
        assertThat(siteDao.findAll(), hasSize(0));
    }

}