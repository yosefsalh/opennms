/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2014 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2014 The OpenNMS Group, Inc.
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

package org.opennms.netmgt.enlinkd.model;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Type;
import org.opennms.netmgt.model.FilterManager;
import org.opennms.netmgt.model.OnmsNode;

import static org.opennms.core.utils.InetAddressUtils.str;
@Entity
@Table(name="ospfLink")
@Filter(name=FilterManager.AUTH_FILTER_NAME, condition="exists (select distinct x.nodeid from node x join category_node cn on x.nodeid = cn.nodeid join category_group cg on cn.categoryId = cg.categoryId where x.nodeid = nodeid and cg.groupId in (:userGroups))")
public class OspfLink implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3798160983917807494L;
	private Integer m_id;	
	private OnmsNode m_node;

	private InetAddress m_ospfIpAddr;
	private InetAddress m_ospfIpMask; 
	private Integer m_ospfIfIndex;
	private Integer m_ospfAddressLessIndex;
	
	private InetAddress m_ospfRemRouterId;
	private InetAddress m_ospfRemIpAddr;
	private Integer m_ospfRemAddressLessIndex;

	private Date m_ospfLinkCreateTime = new Date();
    private Date m_ospfLinkLastPollTime;
	private InetAddress m_ospfIfAreaId;

	public OspfLink(){}

    @Id
    @Column(nullable = false)
    @SequenceGenerator(name = "opennmsSequence", sequenceName = "opennmsNxtId")
    @GeneratedValue(generator = "opennmsSequence")
    public Integer getId() {
		return m_id;
	}

	public void setId(Integer id) {
		m_id = id;
	}

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nodeId")
	public OnmsNode getNode() {
		return m_node;
	}

	public void setNode(OnmsNode node) {
		m_node = node;
	}

    @Type(type="org.opennms.netmgt.model.InetAddressUserType")
    @Column(name="ospfIpAddr")
	public InetAddress getOspfIpAddr() {
		return m_ospfIpAddr;
	}
    
    @Type(type="org.opennms.netmgt.model.InetAddressUserType")
    @Column(name="ospfIpMask")
	public InetAddress getOspfIpMask() {
		return m_ospfIpMask;
	}

    @Column(name="ospfAddressLessIndex")
	public Integer getOspfAddressLessIndex() {
		return m_ospfAddressLessIndex;
	}

	@Type(type="org.opennms.netmgt.model.InetAddressUserType")
	@Column(name="ospfIfAreaId")
	public InetAddress getOspfIfAreaId() {
		return m_ospfIfAreaId;
	}

    @Column(name="ospfIfIndex")
	public Integer getOspfIfIndex() {
		return m_ospfIfIndex;
	}

    @Type(type="org.opennms.netmgt.model.InetAddressUserType")
    @Column(name="ospfRemRouterId",nullable=false)
    public InetAddress getOspfRemRouterId() {
		return m_ospfRemRouterId;
	}

    @Type(type="org.opennms.netmgt.model.InetAddressUserType")
    @Column(name="ospfRemIpAddr",nullable=false)
	public InetAddress getOspfRemIpAddr() {
		return m_ospfRemIpAddr;
	}

    @Column(name="ospfRemAddressLessIndex",nullable=false)
	public Integer getOspfRemAddressLessIndex() {
		return m_ospfRemAddressLessIndex;
	}

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="ospfLinkCreateTime", nullable=false)
	public Date getOspfLinkCreateTime() {
		return m_ospfLinkCreateTime;
	}

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="ospfLinkLastPollTime", nullable=false)
	public Date getOspfLinkLastPollTime() {
		return m_ospfLinkLastPollTime;
	}


	public void setOspfIpAddr(InetAddress ospfIpAddr) {
		m_ospfIpAddr = ospfIpAddr;
	}

	public void setOspfIpMask(InetAddress ospfIpMask) {
		m_ospfIpMask = ospfIpMask;
	}
	
	public void setOspfIfIndex(Integer ospfIfIndex) {
		m_ospfIfIndex = ospfIfIndex;
	}

	public void setOspfAddressLessIndex(Integer ospfAddressLessIndex) {
		m_ospfAddressLessIndex = ospfAddressLessIndex;
	}

	public void setOspfIfAreaId(InetAddress ospfIfAreaId) {
		this.m_ospfIfAreaId = ospfIfAreaId;
	}
	
	public void setOspfRemRouterId(InetAddress ospfRemRouterId) {
		m_ospfRemRouterId = ospfRemRouterId;
	}

	public void setOspfRemIpAddr(InetAddress ospfRemIpAddr) {
		m_ospfRemIpAddr = ospfRemIpAddr;
	}

	public void setOspfRemAddressLessIndex(Integer ospfRemAddressLessIndex) {
		m_ospfRemAddressLessIndex = ospfRemAddressLessIndex;
	}

	public void setOspfLinkCreateTime(Date ospfLinkCreateTime) {
		m_ospfLinkCreateTime = ospfLinkCreateTime;
	}

	public void setOspfLinkLastPollTime(Date ospfLinkLastPollTime) {
		m_ospfLinkLastPollTime = ospfLinkLastPollTime;
	}

	/**
	 * <p>toString</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String toString() {
		return "ospflink: nodeid:[" +
				getNode().getId() +
				"]: id/mask/ifindex/addressless/ifAreaId:[" +
				str(getOspfIpAddr()) +
				"/" +
				str(getOspfIpMask()) +
				"/" +
				getOspfIfIndex() +
				"/" +
				getOspfAddressLessIndex() +
				"/" +
				str(getOspfIfAreaId()) +
				"]: rem router id/ip/addressless:[" +
				str(getOspfRemRouterId()) +
				"/" +
				str(getOspfRemIpAddr()) +
				"/" +
				getOspfRemAddressLessIndex() +
				"]";
        }

	
	public void merge(OspfLink link) {
		if (link == null)
			return;
		setOspfIpAddr(link.getOspfIpAddr());
		setOspfIpMask(link.getOspfIpMask());
		setOspfIfIndex(link.getOspfIfIndex());
		setOspfAddressLessIndex(link.getOspfAddressLessIndex());
		setOspfIfAreaId(link.getOspfIfAreaId());
		
		setOspfRemRouterId(link.getOspfRemRouterId());
		setOspfRemIpAddr(link.getOspfRemIpAddr());
		setOspfRemAddressLessIndex(link.getOspfRemAddressLessIndex());
		
		setOspfLinkLastPollTime(link.getOspfLinkCreateTime());
	}


}
