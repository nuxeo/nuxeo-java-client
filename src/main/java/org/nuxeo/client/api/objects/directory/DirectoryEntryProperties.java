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
package org.nuxeo.client.api.objects.directory;

/**
 * @since 0.1
 */
public class DirectoryEntryProperties {

    protected Integer ordering;

    protected Integer obsolete;

    protected String id;

    protected String label;

    public Integer getOrdering() {
        return ordering;
    }

    public Integer getObsolete() {
        return obsolete;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public void setOrdering(Integer ordering) {
        this.ordering = ordering;
    }

    public void setObsolete(Integer obsolete) {
        this.obsolete = obsolete;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
