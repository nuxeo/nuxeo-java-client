/*
 * (C) Copyright 2024 Nuxeo (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Kevin Leturc <kevin.leturc@hyland.com>
 */
package org.nuxeo.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.nuxeo.client.objects.capabilities.Capabilities;

/**
 * @since 4.0.0
 */
public class ITCapabilities extends AbstractITBase {

    @Test
    public void itCanFetchCapabilities() {
        Capabilities capabilities = nuxeoClient.capabilitiesManager().fetchCapabilities();
        assertNotNull(capabilities);

        var clusterCapability = capabilities.clusterCapability();
        assertNotNull(clusterCapability);
        assertFalse(clusterCapability.capabilityAsBoolean("enabled"));

        var repositoryCapability = capabilities.repositoryCapability();
        assertNotNull(repositoryCapability);
        assertTrue(repositoryCapability.hasCapability("default"));
        var defaultRepositoryCapability = repositoryCapability.capabilityAsCapability("default");
        assertNotNull(defaultRepositoryCapability);
        assertTrue(defaultRepositoryCapability.hasCapability("queryBlobKeys"));

        var serverCapability = capabilities.serverCapability();
        assertNotNull(serverCapability);
        assertEquals("lts", serverCapability.capabilityAsString("distributionName"));
    }
}
