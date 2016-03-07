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

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.client.api.objects.user.Group;
import org.nuxeo.client.api.objects.user.User;
import org.nuxeo.client.api.objects.user.UserManager;
import org.nuxeo.client.internals.spi.NuxeoClientException;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.restapi.test.RestServerFeature;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.Jetty;

/**
 * @since 0.1
 */
@RunWith(FeaturesRunner.class)
@Features({ RestServerFeature.class })
@Jetty(port = 18090)
@RepositoryConfig(cleanup = Granularity.METHOD)
public class TestUserGroup extends TestBase {

    @Before
    public void authentication() {
        login();
    }

    @Test
    public void itCanGetUser() {
        User user = nuxeoClient.getUserManager().fetchUser("Administrator");
        assertNotNull(user);
        assertEquals("Administrator", user.getUserName());
    }

    @Test
    public void itCanGetGroup() {
        Group group = nuxeoClient.getUserManager().fetchGroup("administrators");
        assertNotNull(group);
        assertEquals("administrators", group.getGroupName());
    }

    @Test
    public void itCanCreateAUser() {
        UserManager userManager = nuxeoClient.getUserManager();
        assertNotNull(userManager);
        User newUser = createUser();
        User user = userManager.createUser(newUser);
        assertNotNull(user);
        assertEquals("toto", user.getId());
        assertEquals("to", user.getLastName());
        assertEquals("toto@nuxeo.com", user.getEmail());
        assertEquals("toto@nuxeo.com", user.getEmail());
    }

    protected User createUser() {
        User newUser = new User();
        newUser.setUserName("toto");
        newUser.setCompany("Nuxeo");
        newUser.setEmail("toto@nuxeo.com");
        newUser.setFirstName("to");
        newUser.setLastName("to");
        newUser.setPassword("totopwd");
        List<String> groups = new ArrayList<>();
        groups.add("members");
        groups.add("administrators");
        newUser.setGroups(groups);
        return newUser;
    }

    @Test
    public void itCanUpdateAUser() {
        UserManager userManager = nuxeoClient.getUserManager();
        User user = userManager.fetchUser("Administrator");
        assertNotNull(user);
        assertEquals("Administrator", user.getUserName());
        user.setCompany("Nuxeo");
        User updatedUser = userManager.updateUser(user);
        assertNotNull(updatedUser);
        assertEquals("Nuxeo", updatedUser.getCompany());
    }

    @Test
    public void itCanDeleteAUser() {
        UserManager userManager = nuxeoClient.getUserManager();
        assertNotNull(userManager);
        User newUser = createUser();
        User user = userManager.createUser(newUser);
        assertNotNull(user);
        user = userManager.fetchUser("toto");
        assertNotNull(user);
        userManager.deleteUser("toto");
        try {
            userManager.fetchUser("toto");;
            fail("User should not exist");
        } catch (NuxeoClientException reason) {
            Assert.assertEquals(404, reason.getStatus());
            Assert.assertEquals("user does not exist", reason.getException());
        }
    }
}