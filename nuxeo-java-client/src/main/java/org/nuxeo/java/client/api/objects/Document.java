/*
 * (C) Copyright 2015 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *          Nuxeo
 */
package org.nuxeo.java.client.api.objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @since 1.0
 */
public class Document extends NuxeoObject{

    private static final long serialVersionUID = 1L;

    @JsonProperty("repository")
    protected  String repositoryName;

    protected  String path;

    protected  String type;

    protected  String state;

    protected  String lockOwner;

    protected  String lockCreated;

    protected  String versionLabel;

    protected  String isCheckedOut;

    protected String lastModified;

    protected  Map<String, Object> properties;

    protected  Map<String, Object> contextParameters;

    protected  String changeToken;

    protected  List<String> facets;

    protected String parentRef;

    @JsonProperty("uid")
    protected  String ref;

    protected String title;

    protected String name;

    public Document(String id, String type, List<String> facets, String changeToken, String path, String state,
            String lockOwner, String lockCreated, String repositoryName, String versionLabel, String isCheckedOut,
            Map<String, Object> properties, Map<String, Object> contextParameters) {
        super("document");
        ref = id;
        this.changeToken = changeToken;
        this.facets = facets;
        this.path = path;
        this.type = type;
        this.state = state;
        this.lockOwner = lockOwner;
        this.lockCreated = lockCreated;
        this.repositoryName = repositoryName;
        this.versionLabel = versionLabel;
        this.isCheckedOut = isCheckedOut;
        this.properties = properties == null ? new HashMap<>() : properties;
        this.contextParameters = contextParameters == null ? new HashMap<>() : contextParameters;

    }

    public Document(String title, String type) {
        super("document");
        ref = null;
        this.title = title;
        name = title;
        this.type = type;
        changeToken = null;
        facets = null;
        path = null;
        state = null;
        lockOwner = null;
        lockCreated = null;
        repositoryName = null;
        versionLabel = null;
        isCheckedOut = null;
        properties = new HashMap<>();
        contextParameters = new HashMap<>();
    }

    public Document() {
        super("document");
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public String getId() {
        return ref;
    }

    public String getInputType() {
        return "document";
    }

    public String getPath() {
        return path;
    }

    public String getType() {
        return type;
    }

    public String getLock() {
        if (lockOwner != null && lockCreated != null) {
            return lockOwner + ":" + lockCreated;
        }
        return null;
    }

    public String getLockOwner() {
        return lockOwner;
    }

    public String getLockCreated() {
        return lockCreated;
    }

    public boolean isLocked() {
        return lockOwner != null;
    }

    public String getState() {
        return state;
    }

    public String getVersionLabel() {
        return versionLabel;
    }

    public Boolean isCheckedOut() {
        return (isCheckedOut == null) ? null : Boolean.parseBoolean(isCheckedOut);
    }

    public String getTitle() {
        return title;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public String getChangeToken() {
        return changeToken;
    }

    public List<String> getFacets() {
        return facets;
    }

    public Map<String, Object> getContextParameters() {
        return contextParameters;
    }

    public Map<String, Object> getDirties() {
        return properties;
    }

    public String getIsCheckedOut() {
        return isCheckedOut;
    }

    public String getParentRef() {
        return parentRef;
    }

    public String getRef() {
        return ref;
    }

    public String getLastModified() {
        return lastModified;
    }

    public String getName() {
        return name;
    }
}
