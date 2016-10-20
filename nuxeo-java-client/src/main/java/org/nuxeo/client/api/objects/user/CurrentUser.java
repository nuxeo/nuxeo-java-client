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
package org.nuxeo.client.api.objects.user;

import org.nuxeo.client.api.ConstantsV1;
import org.nuxeo.client.api.NuxeoClient;
import org.nuxeo.client.api.methods.CurrentUserAPI;
import org.nuxeo.client.api.objects.workflow.Workflow;
import org.nuxeo.client.api.objects.workflow.Workflows;

import retrofit2.Callback;

/**
 * @since 0.1
 */
public class CurrentUser extends User {

    protected String username;

    public CurrentUser() {
        super(ConstantsV1.ENTITY_TYPE_LOGIN);
    }

    public CurrentUser(NuxeoClient nuxeoClient) {
        super(ConstantsV1.ENTITY_TYPE_LOGIN, nuxeoClient, CurrentUserAPI.class);
    }

    public String getUsername() {
        return username;
    }

    /** Sync **/

    public CurrentUser getCurrentUser() {
        return (CurrentUser) getResponse();
    }

    public Workflows fetchWorkflowInstances() {
        return (Workflows) getResponse();
    }

    public Workflow startWorkflowInstance(Workflow workflow) {
        return (Workflow) getResponse(workflow);
    }

    /** Async **/

    public void getCurrentUser(Callback<CurrentUser> callback) {
        execute(callback);
    }

    public void fetchWorkflowInstances(Callback<Workflows> callback) {
        execute(callback);
    }

    public void startWorkflowInstance(Workflow workflow, Callback<Workflow> callback) {
        execute(callback, workflow);
    }

}
