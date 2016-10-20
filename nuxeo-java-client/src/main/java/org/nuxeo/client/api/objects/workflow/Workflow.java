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
package org.nuxeo.client.api.objects.workflow;

import java.util.List;
import java.util.Map;

import org.nuxeo.client.api.ConstantsV1;
import org.nuxeo.client.api.objects.NuxeoEntity;

/**
 * @since 0.1
 */
public class Workflow extends NuxeoEntity {

    protected String id;

    protected String name;

    protected String title;

    protected String state;

    protected String workflowModelName;

    protected String initiator;

    protected List<Map<String, String>> attachedDocumentIds;

    protected Map<String, Object> variables;

    protected String graphResource;

    public Workflow() {
        super(ConstantsV1.ENTITY_TYPE_WORKFLOW);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getState() {
        return state;
    }

    public String getWorkflowModelName() {
        return workflowModelName;
    }

    public String getInitiator() {
        return initiator;
    }

    public List<Map<String, String>> getAttachedDocumentIds() {
        return attachedDocumentIds;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public String getGraphResource() {
        return graphResource;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setWorkflowModelName(String workflowModelName) {
        this.workflowModelName = workflowModelName;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }
}
