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
 *         Vladimir Pasquier <vpasquier@nuxeo.com>
 *         Kevin Leturc <kleturc@nuxeo.com>
 */
package org.nuxeo.client.api.objects.user;

import org.nuxeo.client.api.NuxeoClient;
import org.nuxeo.client.api.methods.UserManagerAPI;
import org.nuxeo.client.api.objects.NuxeoEntity;

import okhttp3.ResponseBody;
import retrofit2.Callback;

/**
 * @since 0.1
 */
public class UserManager extends NuxeoEntity {

    public UserManager(NuxeoClient nuxeoClient) {
        super(null, nuxeoClient, UserManagerAPI.class);
    }

    /** Sync **/

    public Group fetchGroup(String groupName) {
        return (Group) getResponse(groupName);
    }

    public Group updateGroup(String groupName, Group group) {
        return (Group) getResponse(groupName, group);
    }

    public Group updateGroup(Group group) {
        return (Group) getResponse(group.getGroupName(), group);
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

    public Groups searchGroup(String query, int currentPageIndex, int pageSize) {
        return (Groups) getResponse(query, currentPageIndex, pageSize);
    }

    public User addUserToGroup(String userName, String groupName) {
        return (User) getResponse(groupName, userName);
    }

    public User fetchUser(String userName) {
        return (User) getResponse(userName);
    }

    public User updateUser(String userName, User user) {
        return (User) getResponse(userName, user);
    }

    public User updateUser(User user) {
        return (User) getResponse(user.getUserName(), user);
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

    public Users searchUser(String query, int currentPageIndex, int pageSize) {
        return (Users) getResponse(query, currentPageIndex, pageSize);
    }

    public User attachGroupToUser(String groupName, String userName) {
        return (User) getResponse(userName, groupName);
    }

    /** Async **/

    public void fetchGroup(String groupName, Callback<Group> callback) {
        execute(callback, groupName);
    }

    public void updateGroup(String groupName, Group group, Callback<Group> callback) {
        execute(callback, groupName, group);
    }

    public void deleteGroup(String groupName, Callback<ResponseBody> callback) {
        execute(callback, groupName);
    }

    public void createGroup(Group group, Callback<Group> callback) {
        execute(callback, group);
    }

    public void searchGroup(String query, Callback<Groups> callback) {
        execute(callback, query);
    }

    public void addUserToGroup(String userName, String groupName, Callback<User> callback) {
        execute(callback, groupName, userName);
    }

    public void fetchUser(String userName, Callback<User> callback) {
        execute(callback, userName);
    }

    public void updateUser(String userName, User user, Callback<User> callback) {
        execute(callback, userName, user);
    }

    public void deleteUser(String userName, Callback<ResponseBody> callback) {
        getResponse(userName);
    }

    public void createUser(User user, Callback<User> callback) {
        execute(callback, user);
    }

    public void searchUser(String query, Callback<Users> callback) {
        execute(callback, query);
    }

    public void attachGroupToUser(String groupName, String userName, Callback<User> callback) {
        execute(callback, userName, groupName);
    }
}
