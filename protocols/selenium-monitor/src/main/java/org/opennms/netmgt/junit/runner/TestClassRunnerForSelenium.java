/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2011-2014 The OpenNMS Group, Inc.
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

package org.opennms.netmgt.junit.runner;

import java.util.List;
import java.util.Map;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.opennms.netmgt.poller.MonitoredService;

public class TestClassRunnerForSelenium extends BlockJUnit4ClassRunner{
    
    private int m_timeout;
    private String m_baseUrl;
    private MonitoredService svc;
    private Map<String, Object> parameters;
    
    
    TestClassRunnerForSelenium(Class<?> type, String baseUrl, int timeoutInSeconds, MonitoredService svc, Map<String, Object> parameters) throws InitializationError {
        super(type);
        setBaseUrl(baseUrl);
        setTimeout(timeoutInSeconds);
    	setSvc(svc);
        setParameters(parameters);
    }
    
    
    
    @Override
    public Object createTest() throws Exception{
        return getTestClass().getOnlyConstructor().newInstance(getBaseUrl(), getTimeout(), getSvc(), getParameters());
    }
    
    @Override
    protected void validateConstructor(List<Throwable> errors) {
        validateOnlyOneConstructor(errors);
    }



    public int getTimeout() {
        return m_timeout;
    }



    public void setTimeout(int timeout) {
        m_timeout = timeout;
    }



    public String getBaseUrl() {
        return m_baseUrl;
    }



    public void setBaseUrl(String baseUrl) {
        m_baseUrl = baseUrl;
    }



	public MonitoredService getSvc() {
		return svc;
	}



	public void setSvc(MonitoredService svc) {
		this.svc = svc;
	}



	public Map<String, Object> getParameters() {
		return parameters;
	}



	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}


}