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

/**
 * @since 1.0
 */
public class TaskInfo {

    protected TaskInfoItem taskActions;

    protected TaskInfoItem layoutResource;

    protected TaskInfoItem schemas;

    public TaskInfoItem getTaskActions() {
        return taskActions;
    }

    public void setTaskActions(TaskInfoItem taskActions) {
        this.taskActions = taskActions;
    }

    public TaskInfoItem getLayoutResource() {
        return layoutResource;
    }

    public void setLayoutResource(TaskInfoItem layoutResource) {
        this.layoutResource = layoutResource;
    }

    public TaskInfoItem getSchemas() {
        return schemas;
    }

    public void setSchemas(TaskInfoItem schemas) {
        this.schemas = schemas;
    }

    public class TaskInfoItem {
        protected String name;

        protected String url;

        protected String label;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
