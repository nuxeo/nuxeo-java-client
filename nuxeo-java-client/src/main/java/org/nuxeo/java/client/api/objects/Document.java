/*
 * (C) Copyright 2016 Nuxeo SA (http://nuxeo.com/) and contributors.
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nuxeo.java.client.api.ConstantsV1;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @since 1.0
 */
public class Document extends NuxeoObject {

    protected String path;

    protected final String type;

    protected String state;

    protected String lockOwner;

    protected String lockCreated;

    protected String versionLabel;

    protected String isCheckedOut;

    protected String lastModified;

    protected Map<String, Object> properties;

    @JsonIgnore
    protected transient Map<String, Object> dirtyProperties;

    protected Map<String, Object> contextParameters;

    protected String changeToken;

    protected List<String> facets;

    protected String parentRef;

    protected String uid;

    protected String title;

    protected String name;

    public Document(String id, String type, List<String> facets, String changeToken, String path, String state,
            String lockOwner, String lockCreated, String repositoryName, String versionLabel, String isCheckedOut,
            Map<String, Object> properties, Map<String, Object> contextParameters) {
        super(ConstantsV1.ENTITY_TYPE_DOCUMENT);
        uid = id;
        this.changeToken = changeToken;
        this.facets = facets;
        this.path = path;
        this.type = type;
        this.state = state;
        this.lockOwner = lockOwner;
        this.lockCreated = lockCreated;
        super.repositoryName = repositoryName;
        this.versionLabel = versionLabel;
        this.isCheckedOut = isCheckedOut;
        this.contextParameters = contextParameters == null ? new HashMap<>() : contextParameters;
        this.properties = properties == null ? new HashMap<>() : properties;
        this.dirtyProperties = new HashMap<>();
    }

    public Document(String title, String type) {
        super(ConstantsV1.ENTITY_TYPE_DOCUMENT);
        uid = null;
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
        dirtyProperties = new HashMap<>();
        contextParameters = new HashMap<>();

    }

    /**
     * For internal marshalling purpose.
     */
    public Document() {
        super(ConstantsV1.ENTITY_TYPE_DOCUMENT);
        uid = null;
        title = ConstantsV1.DEFAULT_DOC_TYPE;
        name = ConstantsV1.DEFAULT_DOC_TYPE;
        type = ConstantsV1.DEFAULT_DOC_TYPE;
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
        dirtyProperties = new HashMap<>();
        contextParameters = new HashMap<>();
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public String getId() {
        return uid;
    }

    public String getInputType() {
        return ConstantsV1.ENTITY_TYPE_DOCUMENT;
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

    public String getChangeToken() {
        return changeToken;
    }

    public List<String> getFacets() {
        return facets;
    }

    public String getIsCheckedOut() {
        return isCheckedOut;
    }

    public String getParentRef() {
        return parentRef;
    }

    public String getUid() {
        return uid;
    }

    public String getLastModified() {
        return lastModified;
    }

    public String getName() {
        return name;
    }

    public void set(String key, Object value) {
        properties.put(key, value);
        dirtyProperties.put(key, value);
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void followLifeCycle(String state) {
        this.state = state;
    }

    public void setLockOwner(String lockOwner) {
        this.lockOwner = lockOwner;
    }

    public void setLockCreated(String lockCreated) {
        this.lockCreated = lockCreated;
    }

    public void setVersionLabel(String versionLabel) {
        this.versionLabel = versionLabel;
    }

    public void setIsCheckedOut(String isCheckedOut) {
        this.isCheckedOut = isCheckedOut;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public void setDirtyProperties(Map<String, Object> dirtyProperties) {
        this.dirtyProperties = dirtyProperties;
    }

    public void setContextParameters(Map<String, Object> contextParameters) {
        this.contextParameters = contextParameters;
    }

    public void setChangeToken(String changeToken) {
        this.changeToken = changeToken;
    }

    public void setFacets(List<String> facets) {
        this.facets = facets;
    }

    public void setParentRef(String parentRef) {
        this.parentRef = parentRef;
    }

    public void setId(String uid) {
        this.uid = uid;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object get(String key) {
        return properties.get(key);
    }

    /**
     * Get the dirty properties.
     */
    public Map<String, Object> getDirtyProperties() {
        return dirtyProperties;
    }

    public Map<String, Object> getContextParameters() {
        return contextParameters;
    }

}
