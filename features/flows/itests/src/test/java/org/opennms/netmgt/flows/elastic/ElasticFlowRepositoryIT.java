/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2017-2017 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2017 The OpenNMS Group, Inc.
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

package org.opennms.netmgt.flows.elastic;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.opennms.features.jest.client.JestClientWithCircuitBreaker;
import org.opennms.features.jest.client.index.IndexStrategy;
import org.opennms.features.jest.client.template.IndexSettings;
import org.opennms.integration.api.v1.flows.FlowException;
import org.opennms.netmgt.dao.mock.AbstractMockDao;
import org.opennms.netmgt.events.api.EventForwarder;
import org.opennms.netmgt.flows.processing.enrichment.EnrichedFlow;

import com.codahale.metrics.MetricRegistry;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.collect.Lists;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;

public class ElasticFlowRepositoryIT {

    private static final String ERROR_RESPONSE = "{\"took\":97,\"errors\":true,\"items\":[{\"index\":{\"_index\":\"flow-2017-11\",\"_type\":\"flow\",\"_id\":\"AV_8xp7OaWbS_xJQivfD\",\"status\":400,\"error\":{\"type\":\"mapper_parsing_exception\",\"reason\":\"failed to parse [timestamp]\",\"caused_by\":{\"type\":\"number_format_exception\",\"reason\":\"For input string: \\\"XXX\\\"\"}}}}]}";

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(WireMockConfiguration.options().dynamicPort());

    @Test(expected=PersistenceException.class)
    public void verifyThrowsPersistenceException() throws IOException, FlowException {
        // Stub request
        stubFor(post("/_bulk")
                    .willReturn(aResponse()
                            .withStatus(400)
                            .withHeader("Content-Type", "application/json")
                            .withBody(ERROR_RESPONSE)));

        // Verify exception is thrown
        final JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(new HttpClientConfig.Builder("http://localhost:" + wireMockRule.port()).build());
        try (JestClient client = factory.getObject()) {
            final EventForwarder eventForwarder = new AbstractMockDao.NullEventForwarder();
            final JestClientWithCircuitBreaker jestClientWithCircuitBreaker =
                    new JestClientWithCircuitBreaker(client, CircuitBreakerRegistry.of(
                    CircuitBreakerConfig.custom().build()).circuitBreaker(ElasticFlowRepositoryIT.class.getName()));
            jestClientWithCircuitBreaker.setEventForwarder(eventForwarder);
            final ElasticFlowRepository elasticFlowRepository = new ElasticFlowRepository(new MetricRegistry(), jestClientWithCircuitBreaker,
                    IndexStrategy.MONTHLY, new MockIdentity(), new MockTracerRegistry(), new IndexSettings(), 0, 0);

            // It does not matter what we persist here, as the response is fixed.
            // We only have to ensure that the list is not empty
            elasticFlowRepository.persist(Lists.newArrayList(EnrichedFlow.from(FlowDocumentTest.getMockFlow())));
        }
    }
}
