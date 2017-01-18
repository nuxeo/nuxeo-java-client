/*
 * (C) Copyright 2016 Nuxeo SA (http://nuxeo.com/) and others.
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
 *         Vladimir Pasquier <vpasquier@nuxeo.com>
 */
package org.nuxeo.client.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.client.api.objects.user.CurrentUser;
import org.nuxeo.client.internals.spi.NuxeoClientException;
import org.nuxeo.client.internals.spi.auth.PortalSSOAuthInterceptor;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.restapi.test.RestServerFeature;
import org.nuxeo.ecm.restapi.test.RestServerInit;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.Jetty;
import org.nuxeo.runtime.test.runner.LocalDeploy;

/**
 * @since 0.1
 */
@RunWith(FeaturesRunner.class)
@Features({ RestServerFeature.class })
@Jetty(port = 18090)
@Deploy({ "org.nuxeo.ecm.platform.login.portal" })
@LocalDeploy("org.nuxeo.java.client.test:test-portal-sso-login-contrib.xml")
@RepositoryConfig(cleanup = Granularity.METHOD, init = RestServerInit.class)
public class TestCurrentUser extends TestBase {

    @Test
    public void itCanLogin() {
        login();
        CurrentUser currentUser = nuxeoClient.fetchCurrentUser();
        assertNotNull(currentUser);
        assertEquals("Administrator", currentUser.getUsername());
        assertEquals(true, currentUser.isAdministrator());
        assertEquals("administrators", currentUser.getGroups().get(1));
        Assert.assertEquals("login", currentUser.getEntityType());
    }

    @Test
    public void itCanLogout() {
        login();
        logout();
        try {
            nuxeoClient.fetchCurrentUser();
            fail("Should be non authorized");
        } catch (NuxeoClientException reason) {
            assertEquals(401, reason.getStatus());
        }
    }

    @Test
    public void itCanFailOnLogin() {
        login("wrong", "credentials");
        try {
            nuxeoClient.fetchCurrentUser();
            fail("Should be non authorized");
        } catch (NuxeoClientException reason) {
            assertEquals(401, reason.getStatus());
        }
    }

    @Test
    public void itCanChangeAuthMethod() {
        login();
        CurrentUser currentUser = nuxeoClient.fetchCurrentUser();
        assertEquals("Administrator", currentUser.getUsername());
        setAuthenticationMethod(new PortalSSOAuthInterceptor("nuxeo5secretkey", "user1"));
        currentUser = nuxeoClient.fetchCurrentUser();
        assertEquals("user1", currentUser.getUsername());
    }

}