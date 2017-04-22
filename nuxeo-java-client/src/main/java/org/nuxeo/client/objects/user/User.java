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
package org.nuxeo.client.objects.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nuxeo.client.ConstantsV1;
import org.nuxeo.client.NuxeoClient;
import org.nuxeo.client.objects.Entity;
import org.nuxeo.client.objects.EntityTypes;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 0.1
 */
public class User extends Entity {

    protected String id;

    /**
     * @since 2.4 - UserProperties object has been removed for generic purpose
     */
    protected Map<String, Object> properties = new HashMap<>();

    protected List<ExtendedGroup> extendedGroups = new ArrayList<>();

    @JsonProperty("isAdministrator")
    protected boolean isAdministrator;

    @JsonProperty("isAnonymous")
    protected boolean isAnonymous;

    public User() {
        super(EntityTypes.USER);
    }

    public User(String entityType) {
        super(entityType);
    }

    public User(String entityType, NuxeoClient nuxeoClient, Class api) {
        super(entityType);
    }

    public String getId() {
        return id;
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
        return (String) this.properties.get(ConstantsV1.USER_FIRST_NAME);
    }

    public String getLastName() {
        return (String) this.properties.get(ConstantsV1.USER_LAST_NAME);
    }

    public String getCompany() {
        return (String) this.properties.get(ConstantsV1.USER_COMPANY);
    }

    public String getEmail() {
        return (String) this.properties.get(ConstantsV1.USER_EMAIL);
    }

    public List<String> getGroups() {
        return (List<String>) this.properties.get(ConstantsV1.USER_GROUPS);
    }

    public String getUserName() {
        return (String) this.properties.get(ConstantsV1.USER_USERNAME);
    }

    public String getPassword() {
        return (String) this.properties.get(ConstantsV1.USER_PASSWORD);
    }

    public void setExtendedGroups(List<ExtendedGroup> extendedGroups) {
        this.extendedGroups = extendedGroups;
    }

    public void setFirstName(String firstName) {
        this.properties.put(ConstantsV1.USER_FIRST_NAME, firstName);
    }

    public void setLastName(String lastName) {
        this.properties.put(ConstantsV1.USER_LAST_NAME, lastName);
    }

    public void setCompany(String company) {
        this.properties.put(ConstantsV1.USER_COMPANY, company);
    }

    public void setEmail(String email) {
        this.properties.put(ConstantsV1.USER_EMAIL, email);
    }

    public void setGroups(List<String> groups) {
        this.properties.put(ConstantsV1.USER_GROUPS, groups);
    }

    public void setUserName(String userName) {
        this.properties.put(ConstantsV1.USER_USERNAME, userName);
    }

    /**
     * @since 2.4
     */
    public void setPassword(String password) {
        this.properties.put(ConstantsV1.USER_PASSWORD, password);
    }

    /**
     * @since 2.4
     */
    public void setTenantId(String tenantId) {
        this.properties.put(ConstantsV1.USER_TENANTID, tenantId);
    }

    /**
     * @since 2.4
     */
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

}
