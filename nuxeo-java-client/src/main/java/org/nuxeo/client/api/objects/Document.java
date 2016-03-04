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
package org.nuxeo.client.api.objects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nuxeo.client.api.ConstantsV1;
import org.nuxeo.client.api.objects.acl.ACP;
import org.nuxeo.client.api.objects.audit.Audit;
import org.nuxeo.client.api.objects.blob.Blob;
import org.nuxeo.client.api.objects.workflow.Workflow;
import org.nuxeo.client.api.objects.workflow.Workflows;

import retrofit2.Callback;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @since 0.1
 */
public class Document extends NuxeoEntity {

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

    public Document updateDocument() {
        return (Document) getResponse(uid, this);
    }

    /* Audit Sync */

    public Audit fetchAudit() {
        return fetchAuditById(uid);
    }

    public Audit fetchAuditById(String documentId) {
        return (Audit) getResponse(documentId);
    }

    /* Audit Async */

    public void fetchAudit(Callback<Audit> callback) {
        execute(callback, uid);
    }

    public void fetchAuditById(String documentId, Callback<Audit> callback) {
        execute(callback, documentId);
    }

    /* ACP Sync */

    public ACP fetchACP() {
        return fetchACPById(uid);
    }

    public ACP fetchACPById(String documentId) {
        return (ACP) getResponse(documentId);
    }

    /* ACP Async */

    public void fetchACP(Callback<ACP> callback) {
        execute(callback, uid);
    }

    public void fetchACPById(String documentId, Callback<ACP> callback) {
        execute(callback, documentId);
    }

    /* Children Sync */

    public Documents fetchChildren() {
        return fetchChildrenById(uid);
    }

    public Documents fetchChildrenById(String parentId) {
        return (Documents) getResponse(parentId);
    }

    /* Children Async */

    public void fetchChildren(Callback<Documents> callback) {
        execute(callback, uid);
    }

    public void fetchChildrenById(String parentId, Callback<Documents> callback) {
        execute(callback, parentId);
    }

    /* Blobs Sync */

    public Blob fetchBlob() {
        return fetchBlobById(uid, ConstantsV1.DEFAULT_FILE_CONTENT);
    }

    public Blob fetchBlob(String fieldPath) {
        return fetchBlobById(uid, fieldPath);
    }

    public Blob fetchBlobById(String uid, String fieldPath) {
        return (Blob) getResponse(uid, fieldPath);
    }

    /* Blobs Async */

    public void fetchBlob(Callback<Blob> callback) {
        execute(callback, uid, ConstantsV1.DEFAULT_FILE_CONTENT);
    }

    public void fetchBlob(String fieldPath, Callback<Blob> callback) {
        execute(callback, uid, fieldPath);
    }

    public void fetchBlobById(String uid, String fieldPath, Callback<Blob> callback) {
        execute(callback, uid, fieldPath);
    }

    /* Workflows Sync */

    public Workflows fetchWorkflowInstances() {
        return (Workflows) getResponse(uid);
    }

    public Workflow startWorkflowInstance(Workflow workflow) {
        return (Workflow) getResponse(uid, workflow);
    }

    /* Workflows Async */

    public void fetchWorkflowInstances(Callback<Workflows> callback) {
        execute(callback, uid);
    }

    public void startWorkflowInstance(Workflow workflow, Callback<Workflow> callback) {
        execute(callback, uid, workflow);
    }
}
