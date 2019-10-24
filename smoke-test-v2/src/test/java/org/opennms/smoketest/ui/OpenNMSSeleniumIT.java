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

package org.opennms.smoketest.ui;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.opennms.smoketest.env.MinimalEnvironment;
import org.opennms.smoketest.selenium.AbstractOpenNMSSeleniumHelper;
import org.opennms.smoketest.utils.WebDriverAccessor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 * Base class for Selenium based testing of the OpenNMS web application.
 */
public class OpenNMSSeleniumIT extends AbstractOpenNMSSeleniumHelper {

    @ClassRule
    public static WebDriverAccessor rule = new WebDriverAccessor();

    protected static MinimalEnvironment getEnvironment() {
        return new MinimalEnvironment();
    }

    public static RemoteWebDriver driver;

    public static MinimalEnvironment environment;

    @BeforeClass
    public static void setUpClass() {
        driver = rule.getWebDriver();
        environment = getEnvironment();
    }

    @Override
    public WebDriver getDriver() {
        return driver;
    }

    @Override
    public String getBaseUrlInternal() {
        return environment.opennms().getBaseUrlInternal().toString();
    }

    @Override
    public String getBaseUrlExternal() {
        return environment.opennms().getBaseUrlExternal().toString();
    }

}
