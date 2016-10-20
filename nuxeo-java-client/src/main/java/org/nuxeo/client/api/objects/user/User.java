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

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.client.api.ConstantsV1;
import org.nuxeo.client.api.NuxeoClient;
import org.nuxeo.client.api.objects.NuxeoEntity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 0.1
 */
public class User extends NuxeoEntity {

    protected String id;

    protected final UserProperties properties = new UserProperties();

    protected List<ExtendedGroup> extendedGroups = new ArrayList<>();

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

    public String getFirstName() {
        return this.properties.getFirstName();
    }

    public String getLastName() {
        return this.properties.getLastName();
    }

    public String getCompany() {
        return this.properties.getCompany();
    }

    public String getEmail() {
        return this.properties.getEmail();
    }

    public List<String> getGroups() {
        return this.properties.getGroups();
    }

    public String getUserName() {
        return this.properties.getUserName();
    }

    public void setExtendedGroups(List<ExtendedGroup> extendedGroups) {
        this.extendedGroups = extendedGroups;
    }

    public void setFirstName(String firstName) {
        this.properties.setFirstName(firstName);
    }

    public void setLastName(String lastName) {
        this.properties.setLastName(lastName);
    }

    public void setCompany(String company) {
        this.properties.setCompany(company);
    }

    public void setEmail(String email) {
        this.properties.setEmail(email);
    }

    public void setGroups(List<String> groups) {
        this.properties.setGroups(groups);
    }

    public void setUserName(String userName) {
        this.properties.setUserName(userName);
    }
}
