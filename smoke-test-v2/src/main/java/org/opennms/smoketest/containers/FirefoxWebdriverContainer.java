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

package org.opennms.smoketest.containers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermissions;

import org.opennms.smoketest.selenium.AbstractOpenNMSSeleniumHelper;
import org.opennms.smoketest.utils.TestContainerUtils;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.testcontainers.containers.BrowserWebDriverContainer;
import org.testcontainers.containers.Network;

import com.github.dockerjava.api.command.CreateContainerCmd;

public class FirefoxWebdriverContainer extends BrowserWebDriverContainer {

    public FirefoxWebdriverContainer() {
        this(AbstractOpenNMSSeleniumHelper.DOWNLOADS_FOLDER);
    }

    public FirefoxWebdriverContainer(final File downloads) {
            withCapabilities(getFirefoxOptions());

            // Don't record. This container can fail and cause test instability - for example if retrieving the
            // .flv from the container fails, an exception will be thrown causing the test to fail
            withRecordingMode(BrowserWebDriverContainer.VncRecordingMode.SKIP, new File("target"));

            //.withRecordingMode(BrowserWebDriverContainer.VncRecordingMode.RECORD_ALL, new File("target"))
            withNetwork(Network.SHARED);

            // Increase the containers shared memory to 2GB to help prevent Firefox from crashing
            withSharedMemorySize(2147483648L);

            // Non-blocking entropy
            withEnv("JAVA_OPTS", "-Djava.security.egd=file:/dev/./urandom");
            withEnv("SCREEN_WIDTH", "2048");
            withEnv("SCREEN_HEIGHT", "1400");
            withCreateContainerCmdModifier(cmd -> {
                final CreateContainerCmd createCmd = (CreateContainerCmd) cmd;
                TestContainerUtils.setGlobalMemAndCpuLimits(createCmd);

                // Use this hook to ensure that the downloads directory exists
                downloads.mkdirs();
                try {
                    // Make the folder world readable/writable
                    Files.setPosixFilePermissions(downloads.toPath(), PosixFilePermissions.fromString("rwxrwxrwx"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            withFileSystemBind("target/downloads", "/tmp/firefox-downloads");
    }

    public static FirefoxOptions getFirefoxOptions() {
        final FirefoxOptions options = new FirefoxOptions();
        options.setProfile(new FirefoxProfile());
        // Disable browser notifications
        options.addPreference("dom.webnotifications.enabled", false);
        // Increase the browser resolution on startup
        options.addArguments("--width=2048");
        options.addArguments("--height=1400");
        // Configure FireFox to download PDFs to disk
        options.addPreference("browser.download.folderList", 2); // Use for the default download directory the last folder specified for a download
        options.addPreference("browser.download.dir", "/tmp/firefox-downloads"); // Set the last directory used for saving a file from the "What should (browser) do with this file?" dialog.
        options.addPreference("browser.helperApps.neverAsk.saveToDisk", "application/pdf"); // List of MIME types to save to disk without asking what to use to open the file
        options.addPreference("pdfjs.disabled", true);  // Disable the built-in PDF viewer
        // Debug Selenium <-> Firefox
        //options.setLogLevel(FirefoxDriverLogLevel.TRACE);
        return options;
    }
}
