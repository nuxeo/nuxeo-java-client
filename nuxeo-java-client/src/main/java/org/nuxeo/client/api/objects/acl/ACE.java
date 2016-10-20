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
package org.nuxeo.client.api.objects.acl;

import java.util.Calendar;

import org.nuxeo.client.internals.util.DateUtils;

/**
 * @since 0.1
 */
public class ACE {

    protected String id;

    protected String username;

    protected String permission;

    protected String granted;

    protected String creator;

    protected Calendar begin;

    protected Calendar end;

    protected String status;

    protected boolean isBlockInheritance;

    protected String comment;

    protected boolean isNotify;

    public ACE(String username, String permission, String granted, String creator, Calendar begin, Calendar end,
            String status) {
        this.username = username;
        this.permission = permission;
        this.granted = granted;
        this.creator = creator;
        this.begin = begin;
        this.end = end;
        this.status = status;
    }

    public ACE() {

    }

    public boolean isBlockInheritance() {
        return isBlockInheritance;
    }

    public void setBlockInheritance(boolean isBlockInheritance) {
        this.isBlockInheritance = isBlockInheritance;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isNotify() {
        return isNotify;
    }

    public void setNotify(boolean isNotify) {
        this.isNotify = isNotify;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPermission() {
        return permission;
    }

    public String getGranted() {
        return granted;
    }

    public String getCreator() {
        return creator;
    }

    public Calendar getBegin() {
        return begin;
    }

    public Calendar getEnd() {
        return end;
    }

    public String getStatus() {
        return status;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public void setGranted(String granted) {
        this.granted = granted;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void setBegin(Calendar begin) {
        this.begin = begin;
    }

    public void setEnd(Calendar end) {
        this.end = end;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBeginAsString() {
        return DateUtils.formatDate(begin.getTime());
    }

    public String getEndAsString() {
        return DateUtils.formatDate(end.getTime());
    }
}
