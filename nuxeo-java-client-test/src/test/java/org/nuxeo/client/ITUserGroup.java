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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.nuxeo.client.objects.user.Group;
import org.nuxeo.client.objects.user.Groups;
import org.nuxeo.client.objects.user.User;
import org.nuxeo.client.objects.user.UserManager;
import org.nuxeo.client.objects.user.Users;
import org.nuxeo.client.spi.NuxeoClientException;

/**
 * @since 0.1
 */
public class ITUserGroup extends AbstractITBase {

    private User user;

    private Group group;

    @Override
    public void init() {
        super.init();
        UserManager userManager = nuxeoClient.userManager();
        // Create user
        user = userManager.createUser(ITBase.createUser());
        // Create group
        group = userManager.createGroup(ITBase.createGroup());
    }

    @Test
    public void itCanLoginWithANewUser() {
        // Try to log with the login/password to check that the password was correctly set
        try {
            new NuxeoClient.Builder().url(ITBase.BASE_URL)
                                     .authentication(user.getUserName(), ITBase.DEFAULT_USER_PASSWORD)
                                     .connect();
        } catch (NuxeoClientException reason) {
            fail("User should be able to login, the password may have been reset");
        }
    }

    @Test
    public void itCanUpdateAUser() {
        UserManager userManager = nuxeoClient.userManager();
        assertEquals("Nuxeo", user.getCompany());
        user.setCompany("test update");
        user = userManager.updateUser(user);
        assertNotNull(user);
        assertEquals("test update", user.getCompany());
    }

    @Test
    public void itCanSearchUsers() {
        Users users = nuxeoClient.userManager().searchUser("*");
        assertNotNull(users);
        assertEquals(2, users.getResultsCount());
        assertEquals(2, users.getCurrentPageSize());
    }

    /*
     * JAVACLIENT-132
     */
    @Test
    public void itCanPaginateUsers() {
        // First search will retrieve Administrator
        Users users = nuxeoClient.userManager().searchUser("*", 0, 1);
        assertNotNull(users);
        assertEquals(2, users.getResultsCount());
        assertEquals(1, users.getCurrentPageSize());
        assertEquals("Administrator", users.getUsers().get(0).getId());
        assertTrue(users.isNextPageAvailable());
        // Second search will retrieve Guest
        users = nuxeoClient.userManager().searchUser("*", 1, 1);
        assertNotNull(users);
        assertEquals(2, users.getResultsCount());
        assertEquals(1, users.getCurrentPageSize());
        assertEquals("toto", users.getUsers().get(0).getId());
        assertFalse(users.isNextPageAvailable());
    }

    @Test
    public void itCanUpdateAGroup() {
        UserManager userManager = nuxeoClient.userManager();
        assertEquals("Toto Group", group.getGroupLabel());
        group.setGroupLabel("test update");
        group = userManager.updateGroup(group);
        assertNotNull(group);
        assertEquals("test update", group.getGroupLabel());
    }

    @Test
    public void itCanAttachAUserToAGroup() {
        UserManager userManager = nuxeoClient.userManager();
        userManager.addUserToGroup(user.getUserName(), group.getGroupName());
        user = userManager.fetchUser(user.getUserName());
        assertEquals(2, user.getGroups().size());
        assertEquals("members", user.getGroups().get(0));
        assertEquals(group.getGroupName(), user.getGroups().get(1));
    }

    @Test
    public void itCanSearchGroups() {
        Groups groups = nuxeoClient.userManager().searchGroup("*");
        assertNotNull(groups);
        assertEquals(4, groups.getResultsCount());
        assertEquals(4, groups.getCurrentPageSize());
    }

    /*
     * JAVACLIENT-132
     */
    @Test
    public void itCanPaginateGroups() {
        // First search will retrieve administrators
        Groups groups = nuxeoClient.userManager().searchGroup("*", 0, 1);
        assertNotNull(groups);
        assertEquals(4, groups.getResultsCount());
        assertEquals(1, groups.getCurrentPageSize());
        assertEquals("administrators", groups.getGroups().get(0).getGroupName());
        assertTrue(groups.isNextPageAvailable());
        // Second search will retrieve members
        groups = nuxeoClient.userManager().searchGroup("*", 1, 1);
        assertNotNull(groups);
        assertEquals(4, groups.getResultsCount());
        assertEquals(1, groups.getCurrentPageSize());
        assertEquals("members", groups.getGroups().get(0).getGroupName());
        assertTrue(groups.isNextPageAvailable());
        // Third search will retrieve powerusers
        groups = nuxeoClient.userManager().searchGroup("*", 2, 1);
        assertNotNull(groups);
        assertEquals(4, groups.getResultsCount());
        assertEquals(1, groups.getCurrentPageSize());
        assertEquals("powerusers", groups.getGroups().get(0).getGroupName());
        assertTrue(groups.isNextPageAvailable());
        // Fourth search will retrieve default group created for tests
        groups = nuxeoClient.userManager().searchGroup("*", 3, 1);
        assertNotNull(groups);
        assertEquals(4, groups.getResultsCount());
        assertEquals(1, groups.getCurrentPageSize());
        assertEquals(ITBase.DEFAULT_GROUP_NAME, groups.getGroups().get(0).getGroupName());
        assertFalse(groups.isNextPageAvailable());
    }

    @Test
    public void itCanAttachAGroupToAUser() {
        UserManager userManager = nuxeoClient.userManager();
        userManager.attachGroupToUser(group.getGroupName(), user.getUserName());
        user = userManager.fetchUser(user.getUserName());
        assertEquals(2, user.getGroups().size());
        assertEquals("members", user.getGroups().get(0));
        assertEquals(group.getGroupName(), user.getGroups().get(1));
    }

    /**
     * JAVACLIENT-87 : check that updating a user doesn't reset his password
     */
    @Test
    public void itDoesNotResetPassword() {
        UserManager userManager = nuxeoClient.userManager();
        user = userManager.fetchUser(user.getUserName());
        assertNotNull(user);
        assertNull(user.getPassword());
        assertEquals(ITBase.DEFAULT_USER_EMAIL, user.getEmail());

        user.setEmail("tata@nuxeo.com");
        user = userManager.updateUser(user);
        assertEquals("tata@nuxeo.com", user.getEmail());

        // Try to log with the login/password to check that the password was not overridden
        try {
            NuxeoClient client = new NuxeoClient.Builder().url(ITBase.BASE_URL)
                                                          .authentication(user.getUserName(),
                                                                  ITBase.DEFAULT_USER_PASSWORD)
                                                          .connect();
            User totoUser = client.getCurrentUser();
            assertNotNull(totoUser);
            assertEquals(ITBase.DEFAULT_USER_LOGIN, totoUser.getUserName());
        } catch (NuxeoClientException reason) {
            fail("User should be able to login, the password may have been reset");
        }
    }

}
