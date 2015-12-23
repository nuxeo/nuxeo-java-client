/*
 * (C) Copyright 2016 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *          Nuxeo
 */
package org.nuxeo.java.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.restapi.test.RestServerFeature;
import org.nuxeo.java.client.api.objects.CurrentUser;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.Jetty;

/**
 * @since 1.0
 */
@RunWith(FeaturesRunner.class)
@Features({ RestServerFeature.class })
@Jetty(port = 18090)
@RepositoryConfig(cleanup = Granularity.METHOD)
public class CurrentUserTest extends BaseTest {

    @Test
    public void itCanLogin() {
        login();
        CurrentUser currentUser = nuxeoClient.getCurrentUser();
        assertNotNull(currentUser);
        assertEquals("Administrator", currentUser.getUsername());
        assertEquals(true, currentUser.isAdministrator());
        assertEquals("administrators", currentUser.getGroups().get(1));
        assertEquals("login", currentUser.getEntityType());
    }

    @Test
    public void itCanLogout() {
        login();
        logout();
        CurrentUser currentUser = nuxeoClient.getCurrentUser();
        assertNull(currentUser);
    }

    @Test
    public void itCanFailOnLogin() {
        login("wrong", "credentials");
        CurrentUser currentUser = nuxeoClient.getCurrentUser();
        assertNull(currentUser);
    }

}