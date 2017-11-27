/*
 * (C) Copyright 2016-2017 Nuxeo (http://nuxeo.com/) and others.
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
 *     Vladimir Pasquier <vpasquier@nuxeo.com>
 *     Kevin Leturc <kleturc@nuxeo.com>
 */
package org.nuxeo.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.nuxeo.client.objects.user.User;
import org.nuxeo.client.spi.NuxeoClientRemoteException;

/**
 * @since 0.1
 */
public class ITAuthentication {

    @Test
    public void itCanLoginAndLogout() {
        NuxeoClient.Builder builder = ITBase.createClientBuilder();

        // Login
        NuxeoClient client = builder.connect();
        User currentUser = client.getCurrentUser();
        assertNotNull(currentUser);
        assertEquals("Administrator", currentUser.getUserName());
        assertTrue(currentUser.isAdministrator());
        assertEquals("administrators", currentUser.getGroups().get(0));

        // Logout
        client.disconnect();
        try {
            client.userManager().fetchCurrentUser();
            fail("Should be non authorized");
        } catch (NuxeoClientRemoteException reason) {
            assertEquals(401, reason.getStatus());
        }
    }

    @Test
    public void itCanFailOnLogin() {
        try {
            ITBase.createClientBuilder("wrong", "credentials").connect();
            fail("Should be non authorized");
        } catch (NuxeoClientRemoteException reason) {
            assertEquals(401, reason.getStatus());
        }
    }

    @Test
    public void itCanChangeAuthMethod() {
        NuxeoClient client = ITBase.createClientPortalSSO();
        User currentUser = client.getCurrentUser();
        assertEquals("Administrator", currentUser.getUserName());
    }

}
