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

import java.util.List;

import org.junit.Before;
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

    @Before
    public void init() {
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
        assertEquals("Label totogroup", group.getGroupLabel());
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

    /**
     * JAVACLIENT-139 : check different ways to fetch member users
     */
    @Test
    public void itCanFetchMemberUsersFromAGroup() {
        UserManager userManager = nuxeoClient.userManager();
        // by default server doesn't return member users
        Group members = userManager.fetchGroup("members");
        assertNull(members.getMemberUsers());
        // add right header
        members = userManager.fetchPropertiesForGroup("memberUsers").fetchGroup(members.getGroupName());
        List<String> membersUsers = members.getMemberUsers();
        assertNotNull(membersUsers);
        assertEquals(1, membersUsers.size());
        assertEquals(user.getUserName(), membersUsers.get(0));
        // re-init user manager headers
        userManager = nuxeoClient.userManager();
        members = userManager.fetchGroup(members.getGroupName());
        assertNull(members.getMemberUsers());
        Users users = members.fetchMemberUsers();
        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(user.getUserName(), users.getEntry(0).getUserName());
        assertEquals(membersUsers, members.getMemberUsers());
    }

    // JAVACLIENT-220
    @Test
    public void itCanFetchMemberUsersFromAGroupPaginable() {
        UserManager userManager = nuxeoClient.userManager();

        // create user in members
        userManager.createUser(ITBase.createUser("user_javaclient"));

        Group members = userManager.fetchGroup("members");

        Users users = members.fetchMemberUsers();
        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals(user.getUserName(), users.getEntry(0).getUserName());
        assertEquals("user_javaclient", users.getEntry(1).getUserName());

        users = members.fetchMemberUsers(0, 1);
        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(user.getUserName(), users.getEntry(0).getUserName());

        users = members.fetchMemberUsers(1, 1);
        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals("user_javaclient", users.getEntry(0).getUserName());
    }

    /**
     * JAVACLIENT-139 : check different ways to fetch member groups
     */
    @Test
    public void itCanFetchMemberGroupsFromAGroup() {
        UserManager userManager = nuxeoClient.userManager();
        // by default server doesn't return member groups
        group = userManager.fetchGroup(group.getGroupName());
        assertNull(group.getMemberGroups());
        // add right header
        group = userManager.fetchPropertiesForGroup("memberGroups").fetchGroup(group.getGroupName());
        List<String> memberGroups = group.getMemberGroups();
        assertNotNull(memberGroups);
        assertEquals(1, memberGroups.size());
        assertEquals("members", memberGroups.get(0));
        // re-init user manager headers
        userManager = nuxeoClient.userManager();
        group = userManager.fetchGroup(group.getGroupName());
        assertNull(group.getMemberGroups());
        Groups groups = group.fetchMemberGroups();
        assertNotNull(groups);
        assertEquals(1, groups.size());
        assertEquals("members", groups.getEntry(0).getGroupName());
        assertEquals(memberGroups, group.getMemberGroups());
    }

    /**
     * JAVACLIENT-139 : check different ways to fetch parent groups
     */
    @Test
    public void itCanFetchParentGroupsFromAGroup() {
        UserManager userManager = nuxeoClient.userManager();
        // by default server doesn't return parent groups
        Group members = userManager.fetchGroup("members");
        assertNull(members.getParentGroups());
        // add right header
        members = userManager.fetchPropertiesForGroup("parentGroups").fetchGroup(members.getGroupName());
        List<String> parentGroups = members.getParentGroups();
        assertNotNull(parentGroups);
        assertEquals(1, parentGroups.size());
        assertEquals(group.getGroupName(), parentGroups.get(0));
    }

    /**
     * JAVACLIENT-139 : check different ways to fetch parent groups
     */
    @Test
    public void itCanSetParentGroups() {
        UserManager userManager = nuxeoClient.userManager();
        // create group
        Group subGroup = new Group();
        subGroup.setGroupName("sub" + group.getGroupName());
        subGroup.setGroupLabel("Sub " + group.getGroupLabel());
        subGroup.setParentGroups(List.of(group.getGroupName()));
        subGroup = userManager.createGroup(subGroup);
        // by default server doesn't return parent groups
        assertNull(subGroup.getParentGroups());
        // add right header
        subGroup = userManager.fetchPropertiesForGroup("parentGroups").fetchGroup(subGroup.getGroupName());
        List<String> subParentGroups = subGroup.getParentGroups();
        assertNotNull(subParentGroups);
        assertEquals(1, subParentGroups.size());
        assertEquals(group.getGroupName(), subParentGroups.get(0));
    }

}
