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
package org.nuxeo.java.client.api.objects.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.nuxeo.java.client.api.ConstantsV1;
import org.nuxeo.java.client.api.NuxeoClient;
import org.nuxeo.java.client.api.objects.NuxeoEntity;

import java.util.List;

/**
 * @since 1.0
 */
public class User extends NuxeoEntity {

    protected String id;

    protected UserProperties properties;

    protected List<ExtendedGroup> extendedGroups;

    @JsonProperty("isAdministrator")
    protected boolean isAdministrator;

    @JsonProperty("isAnonymous")
    protected boolean isAnonymous;

    public User() {
        super(ConstantsV1.ENTITY_TYPE_USER);
    }

    public User(String entityType) {
        super(entityType);
    }

    public User(String entityType, NuxeoClient nuxeoClient, Class api) {
        super(entityType, nuxeoClient, api);
    }

    public String getId() {
        return id;
    }

    public UserProperties getProperties() {
        return properties;
    }

    public boolean isAdministrator() {
        return isAdministrator;
    }

    public boolean isAnonymous() {
        return isAnonymous;
    }

    public List<ExtendedGroup> getExtendedGroups() {
        return extendedGroups;
    }
}
