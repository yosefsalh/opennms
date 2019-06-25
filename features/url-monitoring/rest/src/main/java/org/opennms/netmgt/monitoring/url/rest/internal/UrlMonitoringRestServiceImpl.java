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

package org.opennms.netmgt.monitoring.url.rest.internal;

import java.util.List;
import java.util.Objects;

import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;
import org.opennms.netmgt.dao.api.SessionUtils;
import org.opennms.netmgt.monitoring.url.persistence.api.SiteDao;
import org.opennms.netmgt.monitoring.url.persistence.api.SiteEntity;
import org.opennms.netmgt.monitoring.url.rest.UrlMonitoringRestService;

public class UrlMonitoringRestServiceImpl implements UrlMonitoringRestService {

    private final SiteDao siteDao;
    private final SessionUtils sessionUtils;

    public UrlMonitoringRestServiceImpl(final SiteDao siteDao, final SessionUtils sessionUtils) {
        this.siteDao = Objects.requireNonNull(siteDao);
        this.sessionUtils = Objects.requireNonNull(sessionUtils);
    }

    @Override
    public Response listConfigs() {
        final List<SiteEntity> sites = siteDao.findAll();
        if (sites.isEmpty()) {
            return Response.noContent().build();
        }
        final JSONArray jsonSites = new JSONArray(sites);
        return Response.ok().entity(jsonSites.toString()).build();
    }

    @Override
    public Response saveConfig(final SiteEntity entity) {
        try {
            sessionUtils.withTransaction(() -> {
                siteDao.save(entity);
                return null;
            });
        } catch (Exception ex) {
            return Response.serverError().entity(createErrorObject(ex).toString()).build();
        }
        return Response.accepted().build();
    }

    @Override
    public Response updateConfig(final int siteId, final SiteEntity entity) {
        final SiteEntity existingEntity = siteDao.get(siteId);
        if (existingEntity == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        existingEntity.setInterval(entity.getInterval());
        existingEntity.setUrl(entity.getUrl());
        try {
            // TODO MVR shall we reset the results?
            sessionUtils.withTransaction(() -> {
                siteDao.update(existingEntity);
                return null;
            });
            return Response.accepted().build();
        } catch (Exception ex) {
            return Response.serverError().entity(createErrorObject(ex).toString()).build();
        }
    }

    private static JSONObject createErrorObject(Exception ex) {
        return createErrorObject(ex.getMessage(), "entity");
    }

    private static JSONObject createErrorObject(String message, String context) {
        final JSONObject errorObject = new JSONObject()
                .put("message", message)
                .put("context", context);
        return errorObject;
    }
}
