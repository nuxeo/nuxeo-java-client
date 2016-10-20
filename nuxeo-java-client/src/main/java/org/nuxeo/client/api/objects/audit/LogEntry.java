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
package org.nuxeo.client.api.objects.audit;

import java.util.Map;

import org.nuxeo.client.api.ConstantsV1;
import org.nuxeo.client.api.objects.NuxeoEntity;

/**
 * @since 0.1
 */
public class LogEntry extends NuxeoEntity {

    public LogEntry() {
        super(ConstantsV1.ENTITY_TYPE_LOGENTRY);
    }

    protected int id;

    protected String category;

    protected String principalName;

    protected String comment;

    protected String docLifeCycle;

    protected String docPath;

    protected String docType;

    protected String docUUID;

    protected String eventId;

    protected String repositoryId;

    protected String eventDate;

    protected String logDate;

    protected Map<String, Object> extended;

    public int getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public String getPrincipalName() {
        return principalName;
    }

    public String getComment() {
        return comment;
    }

    public String getDocLifeCycle() {
        return docLifeCycle;
    }

    public String getDocPath() {
        return docPath;
    }

    public String getDocType() {
        return docType;
    }

    public String getDocUUID() {
        return docUUID;
    }

    public String getEventId() {
        return eventId;
    }

    public String getRepositoryId() {
        return repositoryId;
    }

    public String getEventDate() {
        return eventDate;
    }

    public String getLogDate() {
        return logDate;
    }

    public Map<String, Object> getExtended() {
        return extended;
    }
}
