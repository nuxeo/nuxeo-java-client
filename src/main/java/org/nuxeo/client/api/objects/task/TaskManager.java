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
package org.nuxeo.client.api.objects.task;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.nuxeo.client.api.NuxeoClient;
import org.nuxeo.client.api.methods.TaskManagerAPI;
import org.nuxeo.client.api.objects.NuxeoEntity;

/**
 * @since 1.0
 */
public class TaskManager extends NuxeoEntity {

    public TaskManager(NuxeoClient nuxeoClient) {
        super(null, nuxeoClient, TaskManagerAPI.class);
    }

    public Tasks fetchTasks(String userId, String workflowInstanceId, String workflowModelName) {
        return (Tasks) getResponse(userId, workflowInstanceId, workflowModelName);
    }

    public Task fetchTask(String taskId) {
        return (Task) getResponse(taskId);
    }

    public Task reassign(String taskId, List<String> actors, String comment) {
        String actorsValue = StringUtils.join(actors, ",");
        return reassign(taskId, actorsValue, comment);
    }

    public Task reassign(String taskId, String actors, String comment) {
        return (Task) getResponse(taskId, actors, comment);
    }

    public Task delegate(String taskId, List<String> actors, String comment) {
        String actorsValue = StringUtils.join(actors, ",");
        return reassign(taskId, actorsValue, comment);
    }

    public Task delegate(String taskId, String actors, String comment) {
        return (Task) getResponse(taskId, actors, comment);
    }

    public Task complete(String taskId, String action, TaskCompletionRequest taskCompletionRequest) {
        return (Task) getResponse(taskId, action, taskCompletionRequest);
    }
}
