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

import java.util.List;

import org.nuxeo.client.api.ConstantsV1;
import org.nuxeo.client.api.objects.NuxeoEntity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 0.1
 */
public class Group extends NuxeoEntity {

    public Group() {
        super(ConstantsV1.ENTITY_TYPE_GROUP);
    }

    @JsonProperty("groupname")
    protected String groupName;

    @JsonProperty("grouplabel")
    protected String groupLabel;

    protected List<String> memberUsers;

    protected List<String> memberGroups;

    public String getGroupName() {
        return groupName;
    }

    public String getGroupLabel() {
        return groupLabel;
    }

    public List<String> getMemberUsers() {
        return memberUsers;
    }

    public List<String> getMemberGroups() {
        return memberGroups;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setGroupLabel(String groupLabel) {
        this.groupLabel = groupLabel;
    }

    public void setMemberUsers(List<String> memberUsers) {
        this.memberUsers = memberUsers;
    }

    public void setMemberGroups(List<String> memberGroups) {
        this.memberGroups = memberGroups;
    }
}
