/*
 * (C) Copyright 2016-2025 Nuxeo (http://nuxeo.com/) and others.
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

import static org.apache.commons.lang3.ObjectUtils.getIfNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nuxeo.client.methods.UserManagerAPI;
import org.nuxeo.client.objects.ConnectableEntity;
import org.nuxeo.client.objects.EntityTypes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 0.1
 */
public class User extends ConnectableEntity<UserManagerAPI, User> {

    /**
     * User first name property key, this property can be set when creating or updating a user
     *
     * @since 2.4
     */
    public static final String FIRST_NAME_PROPERTY = "firstName";

    /**
     * User last name property key, this property can be set when creating or updating a user
     *
     * @since 2.4
     */
    public static final String LAST_NAME_PROPERTY = "lastName";

    /**
     * User email property key, this property can be set when creating or updating a user
     *
     * @since 2.4
     */
    public static final String EMAIL_PROPERTY = "email";

    /**
     * User groups property key, this property can be set when creating or updating a user
     *
     * @since 2.4
     */
    public static final String GROUPS_PROPERTY = "groups";

    /**
     * User username property key, this property can be set when creating or updating a user
     *
     * @since 2.4
     */
    public static final String USERNAME_PROPERTY = "username";

    /**
     * User company property key, this property can be set when creating or updating a user
     *
     * @since 2.4
     */
    public static final String COMPANY_PROPERTY = "company";

    /**
     * User password property key, this property can be set when creating or updating a user
     *
     * @since 2.4
     */
    public static final String PASSWORD_PROPERTY = "password";

    /**
     * User tenant id property key, this property can be set when creating or updating a user
     *
     * @since 2.4
     */
    public static final String TENANTID_PROPERTY = "tenantId";

    protected String id;

    protected Map<String, Object> properties = new HashMap<>(); // NOSONAR

    protected List<ExtendedGroup> extendedGroups = new ArrayList<>(); // NOSONAR

    @JsonProperty("isAdministrator")
    protected boolean isAdministrator;

    @JsonProperty("isAnonymous")
    protected boolean isAnonymous;

    /**
     * This field is only present when fetching the current user with
     * {@link org.nuxeo.client.methods.UserManagerAPI#fetchCurrentUser() UserManagerAPI.fetchCurrentUser()} API which
     * does not return the exact same object than regular
     * {@link org.nuxeo.client.methods.UserManagerAPI#fetchUser(String) UserManagerAPI.fetchUser(String)} API.
     */
    @JsonProperty("username")
    protected String userName;

    public User() {
        super(EntityTypes.USER, UserManagerAPI.class);
    }

    /**
     * @return the id or username of this user to use in REST paths
     * @since 4.0.4
     */
    public String getIdOrUsername() {
        return getIfNull(getId(), this::getUserName);
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

    public String getUserName() {
        if (userName == null) {
            return (String) this.properties.get(USERNAME_PROPERTY);
        }
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
        this.properties.put(USERNAME_PROPERTY, userName);
    }

    public String getPassword() {
        return (String) this.properties.get(PASSWORD_PROPERTY);
    }

    /**
     * @since 2.4
     */
    @JsonInclude(Include.NON_NULL)
    public void setPassword(String password) {
        this.properties.put(PASSWORD_PROPERTY, password);
    }

    public String getFirstName() {
        return (String) this.properties.get(FIRST_NAME_PROPERTY);
    }

    public void setFirstName(String firstName) {
        this.properties.put(FIRST_NAME_PROPERTY, firstName);
    }

    public String getLastName() {
        return (String) this.properties.get(LAST_NAME_PROPERTY);
    }

    public void setLastName(String lastName) {
        this.properties.put(LAST_NAME_PROPERTY, lastName);
    }

    public String getCompany() {
        return (String) this.properties.get(COMPANY_PROPERTY);
    }

    public void setCompany(String company) {
        this.properties.put(COMPANY_PROPERTY, company);
    }

    public String getEmail() {
        return (String) this.properties.get(EMAIL_PROPERTY);
    }

    public void setEmail(String email) {
        this.properties.put(EMAIL_PROPERTY, email);
    }

    @SuppressWarnings("unchecked")
    public List<String> getGroups() {
        return (List<String>) this.properties.get(GROUPS_PROPERTY);
    }

    public void setGroups(List<String> groups) {
        this.properties.put(GROUPS_PROPERTY, groups);
    }

    /**
     * Adds the current user to the given group.
     * <p>
     * This method will perform a request.
     * 
     * @param group the group to add the user to
     * @return the updated user from remote
     * @since 4.0.4
     */
    public User addUserToGroup(Group group) {
        return fetchResponse(api.addUserToGroup(getIdOrUsername(), group.getIdOrGroupname()));
    }

    /** @since 4.0.4 */
    public String getTenantId() {
        return (String) this.properties.get(TENANTID_PROPERTY);
    }

    /**
     * @since 2.4
     */
    public void setTenantId(String tenantId) {
        this.properties.put(TENANTID_PROPERTY, tenantId);
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    /**
     * @since 2.4
     */
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public List<ExtendedGroup> getExtendedGroups() {
        return extendedGroups;
    }

    public void setExtendedGroups(List<ExtendedGroup> extendedGroups) {
        this.extendedGroups = extendedGroups;
    }
}
