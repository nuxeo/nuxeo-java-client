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
package org.nuxeo.client.objects.user;

import org.nuxeo.client.NuxeoClient;
import org.nuxeo.client.methods.UserManagerAPI;
import org.nuxeo.client.objects.NuxeoEntity;

import okhttp3.ResponseBody;
import retrofit2.Callback;

/**
 * @since 0.1
 */
public class UserManager extends NuxeoEntity<UserManagerAPI> {

    public UserManager(NuxeoClient nuxeoClient) {
        super(null, UserManagerAPI.class, nuxeoClient);
    }

    /** Sync **/

    public Group fetchGroup(String groupName) {
        return fetchResponse(api.fetchGroup(groupName));
    }

    public Group updateGroup(String groupName, Group group) {
        return fetchResponse(api.updateGroup(groupName, group));
    }

    public Group updateGroup(Group group) {
        return fetchResponse(api.updateGroup(group.getGroupName(), group));
    }

    public void deleteGroup(String groupName) {
        fetchResponse(api.deleteGroup(groupName));
    }

    public Group createGroup(Group group) {
        return fetchResponse(api.createGroup(group));
    }

    public Groups searchGroup(String query) {
        return fetchResponse(api.searchGroup(query));
    }

    public Groups searchGroup(String query, int currentPageIndex, int pageSize) {
        return (Groups) getResponse(query, currentPageIndex, pageSize);
    }

    public User addUserToGroup(String userName, String groupName) {
        return fetchResponse(api.addUserToGroup(userName, groupName));
    }

    public User fetchUser(String userName) {
        return fetchResponse(api.fetchUser(userName));
    }

    public User updateUser(String userName, User user) {
        return fetchResponse(api.fetchUser(userName));
    }

    public User updateUser(User user) {
        return fetchResponse(api.updateUser(user.getUserName(), user));
    }

    public void deleteUser(String userName) {
        fetchResponse(api.deleteUser(userName));
    }

    public User createUser(User user) {
        return fetchResponse(api.createUser(user));
    }

    public Users searchUser(String query) {
        return fetchResponse(api.searchUser(query));
    }

    public Users searchUser(String query, int currentPageIndex, int pageSize) {
        return (Users) getResponse(query, currentPageIndex, pageSize);
    }

    public User attachGroupToUser(String groupName, String userName) {
        return fetchResponse(api.attachGroupToUser(groupName, userName));
    }

    /** Async **/

    public void fetchGroup(String groupName, Callback<Group> callback) {
        fetchResponse(api.fetchGroup(groupName), callback);
    }

    public void updateGroup(String groupName, Group group, Callback<Group> callback) {
        fetchResponse(api.updateGroup(groupName, group), callback);
    }

    public void updateGroup(Group group, Callback<Group> callback) {
        fetchResponse(api.updateGroup(group.getGroupName(), group), callback);
    }

    public void deleteGroup(String groupName, Callback<ResponseBody> callback) {
        fetchResponse(api.deleteGroup(groupName), callback);
    }

    public void createGroup(Group group, Callback<Group> callback) {
        fetchResponse(api.createGroup(group), callback);
    }

    public void searchGroup(String query, Callback<Groups> callback) {
        fetchResponse(api.searchGroup(query), callback);
    }

    public void addUserToGroup(String userName, String groupName, Callback<User> callback) {
        fetchResponse(api.addUserToGroup(userName, groupName), callback);
    }

    public void fetchUser(String userName, Callback<User> callback) {
        fetchResponse(api.fetchUser(userName), callback);
    }

    public void updateUser(String userName, User user, Callback<User> callback) {
        fetchResponse(api.updateUser(userName, user), callback);
    }

    public void updateUser(User user, Callback<User> callback) {
        fetchResponse(api.updateUser(user.getUserName(), user), callback);
    }

    public void deleteUser(String userName, Callback<ResponseBody> callback) {
        fetchResponse(api.deleteUser(userName), callback);
    }

    public void createUser(User user, Callback<User> callback) {
        fetchResponse(api.createUser(user), callback);
    }

    public void searchUser(String query, Callback<Users> callback) {
        fetchResponse(api.searchUser(query), callback);
    }

    public void attachGroupToUser(String groupName, String userName, Callback<User> callback) {
        fetchResponse(api.attachGroupToUser(groupName, userName), callback);
    }
    
}
