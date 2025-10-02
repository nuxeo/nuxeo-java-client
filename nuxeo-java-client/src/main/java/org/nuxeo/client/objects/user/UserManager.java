/*
 * (C) Copyright 2016-2025 Nuxeo (http://nuxeo.com/) and others.
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
import org.nuxeo.client.objects.AbstractConnectable;
import org.nuxeo.client.objects.workflow.Workflow;
import org.nuxeo.client.objects.workflow.Workflows;

import okhttp3.ResponseBody;
import retrofit2.Callback;

/**
 * @since 0.1
 */
public class UserManager extends AbstractConnectable<UserManagerAPI, UserManager> {

    public UserManager(NuxeoClient nuxeoClient) {
        super(UserManagerAPI.class, nuxeoClient);
    }

    /** Sync **/

    public Group fetchGroup(String idOrGroupname) {
        return fetchResponse(api.fetchGroup(idOrGroupname));
    }

    public Group updateGroup(String idOrGroupname, Group group) {
        return fetchResponse(api.updateGroup(idOrGroupname, group));
    }

    public Group updateGroup(Group group) {
        return fetchResponse(api.updateGroup(group.getIdOrGroupname(), group));
    }

    public void deleteGroup(String idOrGroupname) {
        try (var ignored = fetchResponse(api.deleteGroup(idOrGroupname))) {
            // just do the try-with-resources
        }
    }

    public Group createGroup(Group group) {
        return fetchResponse(api.createGroup(group));
    }

    public Groups searchGroup(String query) {
        return fetchResponse(api.searchGroup(query));
    }

    public Groups searchGroup(String query, int currentPageIndex, int pageSize) {
        return fetchResponse(api.searchGroup(query, currentPageIndex, pageSize));
    }

    public User addUserToGroup(String idOrUsername, String idOrGroupname) {
        return fetchResponse(api.addUserToGroup(idOrUsername, idOrGroupname));
    }

    public User fetchCurrentUser() {
        return fetchResponse(api.fetchCurrentUser());
    }

    /**
     * Fetch workflow instances for current user.
     */
    public Workflows fetchWorkflowInstances() {
        return fetchResponse(api.fetchWorkflowInstances());
    }

    /**
     * Start workflow instances for current user.
     */
    public Workflow startWorkflowInstance(Workflow workflow) {
        return fetchResponse(api.startWorkflowInstance(workflow));
    }

    public User fetchUser(String idOrUsername) {
        return fetchResponse(api.fetchUser(idOrUsername));
    }

    public User updateUser(String idOrUsername, User user) {
        return fetchResponse(api.updateUser(idOrUsername, user));
    }

    public User updateUser(User user) {
        return fetchResponse(api.updateUser(user.getIdOrUsername(), user));
    }

    public void deleteUser(String idOrUsername) {
        fetchResponse(api.deleteUser(idOrUsername));
    }

    public User createUser(User user) {
        return fetchResponse(api.createUser(user));
    }

    public Users searchUser(String query) {
        return fetchResponse(api.searchUser(query));
    }

    public Users searchUser(String query, int currentPageIndex, int pageSize) {
        return fetchResponse(api.searchUser(query, currentPageIndex, pageSize));
    }

    public Group attachGroupToUser(String idOrGroupname, String idOrUsername) {
        return fetchResponse(api.attachGroupToUser(idOrGroupname, idOrUsername));
    }

    /** Async **/

    public void fetchGroup(String idOrGroupname, Callback<Group> callback) {
        fetchResponse(api.fetchGroup(idOrGroupname), callback);
    }

    public void updateGroup(String idOrGroupname, Group group, Callback<Group> callback) {
        fetchResponse(api.updateGroup(idOrGroupname, group), callback);
    }

    public void updateGroup(Group group, Callback<Group> callback) {
        fetchResponse(api.updateGroup(group.getIdOrGroupname(), group), callback);
    }

    public void deleteGroup(String idOrGroupname, Callback<ResponseBody> callback) {
        fetchResponse(api.deleteGroup(idOrGroupname), callback);
    }

    public void createGroup(Group group, Callback<Group> callback) {
        fetchResponse(api.createGroup(group), callback);
    }

    public void searchGroup(String query, Callback<Groups> callback) {
        fetchResponse(api.searchGroup(query), callback);
    }

    public void addUserToGroup(String idOrUsername, String idOrGroupname, Callback<User> callback) {
        fetchResponse(api.addUserToGroup(idOrUsername, idOrGroupname), callback);
    }

    public void fetchCurrentUser(Callback<User> callback) {
        fetchResponse(api.fetchCurrentUser(), callback);
    }

    /**
     * Fetch workflow instances for current user.
     */
    public void fetchWorkflowInstances(Callback<Workflows> callback) {
        fetchResponse(api.fetchWorkflowInstances(), callback);
    }

    /**
     * Start workflow instances for current user.
     */
    public void startWorkflowInstance(Workflow workflow, Callback<Workflow> callback) {
        fetchResponse(api.startWorkflowInstance(workflow), callback);
    }

    public void fetchUser(String idOrUsername, Callback<User> callback) {
        fetchResponse(api.fetchUser(idOrUsername), callback);
    }

    public void updateUser(String idOrUsername, User user, Callback<User> callback) {
        fetchResponse(api.updateUser(idOrUsername, user), callback);
    }

    public void updateUser(User user, Callback<User> callback) {
        fetchResponse(api.updateUser(user.getIdOrUsername(), user), callback);
    }

    public void deleteUser(String idOrUsername, Callback<Void> callback) {
        fetchResponse(api.deleteUser(idOrUsername), callback);
    }

    public void createUser(User user, Callback<User> callback) {
        fetchResponse(api.createUser(user), callback);
    }

    public void searchUser(String query, Callback<Users> callback) {
        fetchResponse(api.searchUser(query), callback);
    }

    public void attachGroupToUser(String idOrGroupname, String idOrUsername, Callback<Group> callback) {
        fetchResponse(api.attachGroupToUser(idOrGroupname, idOrUsername), callback);
    }

}
