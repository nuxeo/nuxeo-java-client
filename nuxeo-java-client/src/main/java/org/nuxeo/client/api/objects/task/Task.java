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

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.nuxeo.client.api.ConstantsV1;
import org.nuxeo.client.api.objects.NuxeoEntity;

/**
 * @since 1.0
 */
public class Task extends NuxeoEntity {

    public Task() {
        super(ConstantsV1.ENTITY_TYPE_TASK);
    }

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

    protected List<Map<String, String>> targetDocumentIds;

    protected List<Map<String, String>> actors;

    protected TaskVariables variables;

    protected TaskInfo taskInfo;

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

    public List<Map<String, String>> getTargetDocumentIds() {
        return targetDocumentIds;
    }

    public void setTargetDocumentIds(List<Map<String, String>> targetDocumentIds) {
        this.targetDocumentIds = targetDocumentIds;
    }

    public List<Map<String, String>> getActors() {
        return actors;
    }

    public void setActors(List<Map<String, String>> actors) {
        this.actors = actors;
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
