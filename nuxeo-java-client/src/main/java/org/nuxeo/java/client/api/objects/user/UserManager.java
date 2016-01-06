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
package org.nuxeo.java.client.api.objects.user;

import org.nuxeo.java.client.api.NuxeoClient;
import org.nuxeo.java.client.api.methods.UserManagerAPI;
import org.nuxeo.java.client.api.objects.NuxeoEntity;

/**
 * @since 0.1
 */
public class UserManager extends NuxeoEntity {

    public UserManager(NuxeoClient nuxeoClient) {
        super(null, nuxeoClient, UserManagerAPI.class);
    }

    public Group fetchGroup(String groupName) {
        return (Group) getResponse(groupName);
    }

    public Group updateGroup(String groupName, Group group) {
        return (Group) getResponse(groupName, group);
    }

    public void deleteGroup(String groupName) {
        getResponse(groupName);
    }

    public Group createGroup(Group group) {
        return (Group) getResponse(group);
    }

    public Groups searchGroup(String query) {
        return (Groups) getResponse(query);
    }

    public User addUserToGroup(String groupName, String userName) {
        return (User) getResponse(groupName, userName);
    }

    public User fetchUser(String userName) {
        return (User) getResponse(userName);
    }

    public User updateUser(String userName, User user) {
        return (User) getResponse(userName, user);
    }

    public void deleteUser(String userName) {
        getResponse(userName);
    }

    public User createUser(User user) {
        return (User) getResponse(user);
    }

    public Users searchUser(String query) {
        return (Users) getResponse(query);
    }

    public User attachGroupToUser(String userName, String groupName) {
        return (User) getResponse(userName, groupName);
    }
}
