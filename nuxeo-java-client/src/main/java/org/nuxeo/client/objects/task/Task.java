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
package org.nuxeo.client.objects.task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.nuxeo.client.objects.Entity;
import org.nuxeo.client.objects.EntityTypes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 1.0
 */
public class Task extends Entity {

    protected String id;

    protected String name;

    protected String workflowInstanceId;

    protected String workflowModelName;

    protected String state;

    protected String directive;

    protected String nodeName;

    protected Calendar created;

    protected Calendar dueDate;

    protected List<String> comments;

    @JsonProperty("targetDocumentIds")
    protected List<Map<String, String>> targetDocumentIds;

    @JsonProperty("actors")
    protected List<Map<String, String>> actors;

    protected TaskVariables variables;

    protected TaskInfo taskInfo;

    public Task() {
        super(EntityTypes.TASK);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWorkflowInstanceId() {
        return workflowInstanceId;
    }

    public void setWorkflowInstanceId(String workflowInstanceId) {
        this.workflowInstanceId = workflowInstanceId;
    }

    public String getWorkflowModelName() {
        return workflowModelName;
    }

    public void setWorkflowModelName(String workflowModelName) {
        this.workflowModelName = workflowModelName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDirective() {
        return directive;
    }

    public void setDirective(String directive) {
        this.directive = directive;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public Calendar getCreated() {
        return created;
    }

    public void setCreated(Calendar created) {
        this.created = created;
    }

    public Calendar getDueDate() {
        return dueDate;
    }

    public void setDueDate(Calendar dueDate) {
        this.dueDate = dueDate;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    @JsonIgnore
    public List<String> getTargetDocumentIds() {
        List<String> ids = new ArrayList<>(targetDocumentIds.size());
        for (Map<String, String> targetDocumentId : targetDocumentIds) {
            ids.add(targetDocumentId.get("id"));
        }
        return ids;
    }

    @JsonIgnore
    public void setTargetDocumentIds(List<String> ids) {
        targetDocumentIds = new ArrayList<>(ids.size());
        for (String id : ids) {
            targetDocumentIds.add(Collections.singletonMap("id", id));
        }
    }

    @JsonIgnore
    public List<String> getActors() {
        List<String> ids = new ArrayList<>(actors.size());
        for (Map<String, String> actor : actors) {
            ids.add(actor.get("id"));
        }
        return ids;
    }

    @JsonIgnore
    public void setActors(List<String> ids) {
        actors = new ArrayList<>(ids.size());
        for (String id : ids) {
            actors.add(Collections.singletonMap("id", id));
        }
    }

    public TaskVariables getVariables() {
        return variables;
    }

    public void setVariables(TaskVariables variables) {
        this.variables = variables;
    }

    public TaskInfo getTaskInfo() {
        return taskInfo;
    }

    public void setTaskInfo(TaskInfo taskInfo) {
        this.taskInfo = taskInfo;
    }

}
