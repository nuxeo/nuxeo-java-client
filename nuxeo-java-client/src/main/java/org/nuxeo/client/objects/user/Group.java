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

import java.util.List;
import java.util.stream.Collectors;

import org.nuxeo.client.methods.UserManagerAPI;
import org.nuxeo.client.objects.ConnectableEntity;
import org.nuxeo.client.objects.EntityTypes;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 0.1
 */
public class Group extends ConnectableEntity<UserManagerAPI, Group> {

    @JsonProperty("groupname")
    protected String groupName;

    @JsonProperty("grouplabel")
    protected String groupLabel;

    protected List<String> memberUsers;

    protected List<String> memberGroups;

    protected List<String> parentGroups;

    public Group() {
        super(EntityTypes.GROUP, UserManagerAPI.class);
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupLabel() {
        return groupLabel;
    }

    public void setGroupLabel(String groupLabel) {
        this.groupLabel = groupLabel;
    }

    /**
     * Returns a list of member users if information is returned by server. This could be done by adding
     * {@code fetch.group=memberUsers} header to request.
     * <p>
     * This could be achieved with client API like this: <blockquote>
     * 
     * <pre>
     * // if you want to apply header on all requests
     * client.fetchPropertiesForGroup("memberUsers").userManager().fetchGroup("...");
     * // if you want to apply header only on requests made by this userManager
     * client.userManager().fetchPropertiesForGroup("memberUsers").fetchGroup("...");
     * </pre>
     * 
     * </blockquote>
     *
     * @return a list of member users
     */
    public List<String> getMemberUsers() {
        return memberUsers;
    }

    /**
     * Returns a list of member users fetched from server. This method will perform a request and store the result on
     * this object.
     * 
     * @return a list of member users fetched from server
     * @since 3.0
     */
    public Users fetchMemberUsers() {
        Users users = fetchResponse(api.fetchGroupMemberUsers(getGroupName()));
        memberUsers = users.streamEntries().map(User::getUserName).collect(Collectors.toList());
        return users;
    }

    /**
     * Sets the memberUsers to this object. This method doesn't perform request to server. It must be used with
     * {@link UserManager#createGroup(Group)} or {@link UserManager#updateGroup(Group)}.
     */
    public void setMemberUsers(List<String> memberUsers) {
        this.memberUsers = memberUsers;
    }

    /**
     * Returns a list of member groups if information is returned by server. This could be done by adding
     * {@code fetch.group=memberGroups} header to request.
     * <p>
     * This could be achieved with client API like this: <blockquote>
     * 
     * <pre>
     * // if you want to apply header on all requests
     * client.fetchPropertiesForGroup("memberGroups").userManager().fetchGroup("...");
     * // if you want to apply header only on requests made by this userManager
     * client.userManager().fetchPropertiesForGroup("memberGroups").fetchGroup("...");
     * </pre>
     * 
     * </blockquote>
     *
     * @return a list of member groups
     */
    public List<String> getMemberGroups() {
        return memberGroups;
    }

    /**
     * Returns a list of member groups fetched from server. This method will perform a request and store the result on
     * this object.
     *
     * @return a list of member groups fetched from server
     * @since 3.0
     */
    public Groups fetchMemberGroups() {
        Groups groups = fetchResponse(api.fetchGroupMemberGroups(getGroupName()));
        memberGroups = groups.streamEntries().map(Group::getGroupName).collect(Collectors.toList());
        return groups;
    }

    /**
     * Sets the memberGroups to this object. This method doesn't perform request to server. It must be used with
     * {@link UserManager#createGroup(Group)} or {@link UserManager#updateGroup(Group)}.
     */
    public void setMemberGroups(List<String> memberGroups) {
        this.memberGroups = memberGroups;
    }

    /**
     * Returns a list of parent groups if information is returned by server. This could be done by adding
     * {@code fetch.group=parentGroups} header to request.
     * <p>
     * This could be achieved with client API like this: <blockquote>
     *
     * <pre>
     * // if you want to apply header on all requests
     * client.fetchPropertiesForGroup("parentGroups").userManager().fetchGroup("...");
     * // if you want to apply header only on requests made by this userManager
     * client.userManager().fetchPropertiesForGroup("parentGroups").fetchGroup("...");
     * </pre>
     *
     * </blockquote>
     * <p>
     * CAUTION: Only available for Nuxeo Server greater than LTS 2016 - 8.10-HF19
     * 
     * @return a list of parent groups
     * @since 3.0
     */
    public List<String> getParentGroups() {
        return parentGroups;
    }

    /**
     * Sets the parentGroups to this object. This method doesn't perform request to server. It must be used with
     * {@link UserManager#createGroup(Group)} or {@link UserManager#updateGroup(Group)}.
     * <p>
     * CAUTION: Only available for Nuxeo Server greater than LTS 2016 - 8.10-HF19
     */
    public void setParentGroups(List<String> parentGroups) {
        this.parentGroups = parentGroups;
    }

}
