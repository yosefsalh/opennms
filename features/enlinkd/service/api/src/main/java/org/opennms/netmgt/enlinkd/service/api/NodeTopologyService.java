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

package org.opennms.netmgt.enlinkd.service.api;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.opennms.netmgt.enlinkd.model.IpInterfaceTopologyEntity;
import org.opennms.netmgt.enlinkd.model.NodeTopologyEntity;
import org.opennms.netmgt.enlinkd.model.SnmpInterfaceTopologyEntity;

public interface NodeTopologyService extends TopologyService {

    List<Node> findAllSnmpNode();
    Set<SubNetwork> findAllSubNetwork();
    Set<SubNetwork> findAllLegalSubNetwork();
    Set<SubNetwork> findSubNetworkByNetworkPrefixLessThen(int ipv4prefix, int ipv6prefix);
    Set<SubNetwork> findAllPointToPointSubNetwork();
    Set<SubNetwork> findAllLegalPointToPointSubNetwork();
    Set<SubNetwork> findAllLoopbacks();
    Set<SubNetwork> findAllLegalLoopbacks();

    Map<Integer, Integer> getNodeidPriorityMap(ProtocolSupported protocol);

    Node getSnmpNode(String nodeCriteria);
    Node getSnmpNode(int nodeid);
    Set<SubNetwork> getSubNetworks(int nodeid);
    Set<SubNetwork> getLegalSubNetworks(int nodeid);

    List<NodeTopologyEntity> findAllNode();

    List<IpInterfaceTopologyEntity> findAllIp();
    List<SnmpInterfaceTopologyEntity> findAllSnmp();
    NodeTopologyEntity getDefaultFocusPoint();
        
}
