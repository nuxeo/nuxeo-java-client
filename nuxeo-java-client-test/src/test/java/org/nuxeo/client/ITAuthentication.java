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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;
import static org.nuxeo.client.ITBase.JWT;
import static org.nuxeo.client.ITBase.createClient;
import static org.nuxeo.client.ITBase.createClientBuilder;
import static org.nuxeo.client.NuxeoVersion.LTS_2021;

import org.junit.Test;
import org.nuxeo.client.objects.directory.Directory;
import org.nuxeo.client.objects.directory.DirectoryEntry;
import org.nuxeo.client.objects.user.User;
import org.nuxeo.client.objects.user.UserManager;
import org.nuxeo.client.spi.NuxeoClientRemoteException;
import org.nuxeo.client.spi.auth.OAuth2AuthInterceptor;

/**
 * @since 0.1
 */
public class ITAuthentication {

    @Test
    public void itCanLoginAndLogout() {
        // Login
        NuxeoClient client = createClient();
        User currentUser = client.getCurrentUser();
        assertNotNull(currentUser);
        assertEquals("Administrator", currentUser.getUserName());
        assertTrue(currentUser.isAdministrator());
        assertEquals("administrators", currentUser.getGroups().get(0));

        // Logout
        client.disconnect();
        var reason = assertThrows("Should be non authorized", NuxeoClientRemoteException.class,
                () -> client.userManager().fetchCurrentUser());
        assertEquals(401, reason.getStatus());
    }

    @Test
    public void itCanFailOnLogin() {
        var reason = assertThrows("Should be non authorized", NuxeoClientRemoteException.class,
                () -> createClient("wrong", "credentials"));
        assertEquals(401, reason.getStatus());
    }

    @Test
    public void itCanLoginWithPortalSSO() {
        NuxeoClient client = ITBase.createClientPortalSSO();
        User currentUser = client.getCurrentUser();
        assertEquals("Administrator", currentUser.getUserName());
    }

    @Test
    public void itCanLoginWithJWT() {
        NuxeoClient client = ITBase.createClientJWT();
        User currentUser = client.getCurrentUser();
        assertEquals("Administrator", currentUser.getUserName());
    }

    @Test
    public void itCanLoginWithOauth2AndJwtBearer() {
        NuxeoClient adminClient = createClient();
        assumeTrue("itCanLoginWithOauth2AndJwtBearer works only since Nuxeo 11.1",
                adminClient.getServerVersion().isGreaterThan(NuxeoVersion.parse("11.1-SNAPSHOT")));

        Directory oauth2Directory = adminClient.directoryManager().directory("oauth2Clients");
        // create an oauth provider
        DirectoryEntry providerEntry = new DirectoryEntry();
        providerEntry.putProperty("name", "OAuth2 JWT");
        providerEntry.putProperty("clientId", "oauth2Jwt");
        providerEntry.putProperty("clientSecret", "strongSecret");
        providerEntry.putProperty("redirectURIs", "nuxeo://not-used");
        providerEntry.putProperty("autoGrant", true);
        providerEntry.putProperty("enabled", true);
        providerEntry = oauth2Directory.createEntry(providerEntry);
        try {
            OAuth2AuthInterceptor auth = OAuth2AuthInterceptor.obtainAuthFromJWTToken(ITBase.BASE_URL, "oauth2Jwt",
                    "strongSecret", JWT);
            createClientBuilder(auth).connect();
        } finally {
            oauth2Directory.deleteEntry(providerEntry.getId());
        }
    }

    // JAVACLIENT-216
    @Test
    public void itReturnProperExceptionWhenLoginWithOauth2AndJwtBearer() {
        NuxeoClient adminClient = createClient();
        NuxeoVersion serverVersion = adminClient.getServerVersion();
        assumeTrue("itReturnProperExceptionWhenLoginWithOauth2AndJwtBearer works only since Nuxeo 2021.23",
                serverVersion.isGreaterThan(LTS_2021.minor(23)));

        Directory oauth2Directory = adminClient.directoryManager().directory("oauth2Clients");
        // create an oauth provider
        DirectoryEntry providerEntry = new DirectoryEntry();
        providerEntry.putProperty("name", "OAuth2 JWT");
        providerEntry.putProperty("clientId", "ReturnProperExceptionWhenJwt");
        providerEntry.putProperty("clientSecret", "strongSecret");
        providerEntry.putProperty("redirectURIs", "nuxeo://not-used");
        providerEntry.putProperty("autoGrant", true);
        providerEntry.putProperty("enabled", true);
        String providerEntryId1 = oauth2Directory.createEntry(providerEntry).getId();
        // create a second oauth provider to produce an error
        String providerEntryId2 = oauth2Directory.createEntry(providerEntry).getId();
        try {
            NuxeoClientRemoteException e = assertThrows(NuxeoClientRemoteException.class,
                    () -> OAuth2AuthInterceptor.obtainAuthFromJWTToken(ITBase.BASE_URL, "ReturnProperExceptionWhenJwt",
                            "strongSecret", JWT));
            assertEquals("More than one client registered for the 'ReturnProperExceptionWhenJwt' id", e.getMessage());
            assertEquals(500, e.getStatus());
        } finally {
            oauth2Directory.deleteEntry(providerEntryId1);
            oauth2Directory.deleteEntry(providerEntryId2);
        }
    }

    @Test
    public void itCanLoginWithLongCredentials() {
        UserManager userManager = createClient().userManager();

        String email = "verylongmailaddress0123456789@nuxeo.com";
        String password = "verylongpassword0123456789";
        // first create user
        User user = new User();
        user.setUserName(email);
        user.setEmail(email);
        user.setPassword(password);
        userManager.createUser(user);
        // now test
        User currentUser = createClient(email, password).getCurrentUser();
        assertEquals(email, currentUser.getUserName());
        assertFalse(currentUser.isAdministrator());
        // delete it
        userManager.deleteUser(email);
    }

}
