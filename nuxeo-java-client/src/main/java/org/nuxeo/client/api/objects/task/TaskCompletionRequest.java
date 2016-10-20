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

import java.util.Map;

import org.nuxeo.client.api.ConstantsV1;
import org.nuxeo.client.api.objects.NuxeoEntity;

/**
 * @since 1.0
 */
public class TaskCompletionRequest extends NuxeoEntity {

    protected String comment;

    private Map<String, Object> variables;

    public TaskCompletionRequest() {
        super(ConstantsV1.ENTITY_TYPE_TASK);
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

}
