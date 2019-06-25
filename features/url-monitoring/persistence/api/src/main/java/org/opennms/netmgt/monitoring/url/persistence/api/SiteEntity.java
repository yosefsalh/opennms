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

package org.opennms.netmgt.monitoring.url.persistence.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.BatchSize;

@Entity
@Table(name="sites")
public class SiteEntity {

    @Id
    @SequenceGenerator(name = "sitesSequence", sequenceName = "sitenxtid")
    @GeneratedValue(generator = "sitesSequence")
    @Column(name="id", nullable = false)
    private Integer id; // TODO MVR should probably be a long

    // TODO MVR should this be an URL ?
    @Column(name="url", unique = true, nullable = false)
    private String url;

    // TODO MVR this should be long
    // TODO MVR this should be configurable
    @Column(name="interval", nullable=false)
    private int interval = 5 * 60 * 1000; // Default is 5 Minutes

    @OneToMany(mappedBy="site", orphanRemoval=true)
    @org.hibernate.annotations.Cascade(org.hibernate.annotations.CascadeType.ALL)
    @BatchSize(size = 10)
    private List<SiteResultEntity> results = new ArrayList<>();

    public SiteEntity() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public void setInterval(int interval, TimeUnit unit) {
        setInterval((int) TimeUnit.MILLISECONDS.convert(interval, unit));
    }

    public void addResult(SiteResultEntity result) {
        Objects.requireNonNull(result);
        result.setSite(this);
        results.add(result);
    }

    public List<SiteResultEntity> getResults() {
        return results;
    }

    public void setResults(List<SiteResultEntity> results) {
        this.results = results;
    }
}
