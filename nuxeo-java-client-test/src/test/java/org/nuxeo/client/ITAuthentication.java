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
import org.nuxeo.client.spi.NuxeoClientException;

/**
 * @since 0.1
 */
public class ITAuthentication {

    @Test
    public void itCanLoginAndLogout() {
        NuxeoClient client = ITBase.createClient();

        // Login
        User currentUser = client.userManager().fetchCurrentUser();
        assertNotNull(currentUser);
        assertEquals("Administrator", currentUser.getUserName());
        assertTrue(currentUser.isAdministrator());
        assertEquals("administrators", currentUser.getGroups().get(0));

        // Logout
        // TODO provide a real way to logout
//        client.logout();
//        try {
//            client.userManager().fetchCurrentUser();
//            fail("Should be non authorized");
//        } catch (NuxeoClientException reason) {
//            assertEquals(401, reason.getStatus());
//        }
    }

    @Test
    public void itCanFailOnLogin() {
        NuxeoClient client = ITBase.createClient("wrong", "credentials");
        try {
            client.userManager().fetchCurrentUser();
            fail("Should be non authorized");
        } catch (NuxeoClientException reason) {
            assertEquals(401, reason.getStatus());
        }
    }

    @Test
    public void itCanChangeAuthMethod() {
        // TODO re-implement this
        // login();
        // CurrentUser currentUser = client.fetchCurrentUser();
        // assertEquals("Administrator", currentUser.getUsername());
        // setAuthenticationMethod(new PortalSSOAuthInterceptor("user1", "nuxeo5secretkey"));
        // currentUser = client.fetchCurrentUser();
        // assertEquals("user1", currentUser.getUsername());
    }

}
