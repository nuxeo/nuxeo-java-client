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
package org.nuxeo.client.objects;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.nuxeo.client.ConstantsV1;
import org.nuxeo.client.Operations;
import org.nuxeo.client.methods.RepositoryAPI;
import org.nuxeo.client.objects.acl.ACE;
import org.nuxeo.client.objects.acl.ACL;
import org.nuxeo.client.objects.acl.ACP;
import org.nuxeo.client.objects.audit.Audit;
import org.nuxeo.client.objects.blob.Blob;
import org.nuxeo.client.objects.task.Task;
import org.nuxeo.client.objects.workflow.Workflow;
import org.nuxeo.client.objects.workflow.Workflows;
import org.nuxeo.client.spi.NuxeoClientException;

import com.fasterxml.jackson.annotation.JsonIgnore;

import okhttp3.ResponseBody;
import retrofit2.Callback;

/**
 * @since 0.1
 */
public class Document extends RepositoryEntity<RepositoryAPI> {

    public static final String DEFAULT_FILE_CONTENT = "file:content";

    protected String path;

    protected String type;

    protected String state;

    protected String lockOwner;

    protected String lockCreated;

    protected String versionLabel;

    protected String isCheckedOut;

    protected String lastModified;

    protected final Map<String, Object> properties = new HashMap<>();

    @JsonIgnore
    protected final transient Map<String, Object> dirtyProperties = new HashMap<>();

    protected final Map<String, Object> contextParameters = new HashMap<>();

    protected String changeToken;

    protected List<String> facets;

    protected String parentRef;

    protected String uid;

    protected String title;

    protected String name;

    /**
     * @since 2.3
     */
    protected boolean isProxy;

    /**
     * @deprecated since 2.3
     */
    @Deprecated
    public Document(String id, String type, List<String> facets, String changeToken, String path, String state,
            String lockOwner, String lockCreated, String repositoryName, String versionLabel, String isCheckedOut,
            Map<String, Object> properties, Map<String, Object> contextParameters) {
        this(id, type, facets, changeToken, path, state, lockOwner, lockCreated, repositoryName, versionLabel,
                isCheckedOut, false, properties, contextParameters);
    }

    /**
     * @since 2.3
     */
    public Document(String id, String type, List<String> facets, String changeToken, String path, String state,
            String lockOwner, String lockCreated, String repositoryName, String versionLabel, String isCheckedOut,
            boolean isProxy, Map<String, Object> properties, Map<String, Object> contextParameters) {
        super(EntityTypes.DOCUMENT, RepositoryAPI.class);
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
        this.isProxy = isProxy;
        setProperties(properties);
    }

    public Document(String title, String type) {
        super(EntityTypes.DOCUMENT, RepositoryAPI.class);
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
    }

    /**
     * For internal marshalling purpose.
     */
    public Document() {
        super(ConstantsV1.ENTITY_TYPE_DOCUMENT, RepositoryAPI.class);
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
        setContextParameters(contextParameters);
    }

    /**
     * This constructor is providing a way to create 'adapters' of a document. See org.nuxeo.client.test.objects.DataSet
     * in nuxeo-java-client-test.
     *
     * @since 1.0
     * @param document the document to copy from the sub class.
     */
    public Document(Document document) {
        super(EntityTypes.DOCUMENT, RepositoryAPI.class);
        type = ConstantsV1.DEFAULT_DOC_TYPE;
        try {
            Class<?> superclass = this.getClass().getSuperclass();
            while (!superclass.equals(Document.class)) {
                superclass = superclass.getSuperclass();
                if (superclass.equals(Object.class)) {
                    throw new NuxeoClientException(
                            "You should never use this constructor unless you're using a subclass of Document");
                }
            }
            for (Field field : document.getClass().getDeclaredFields()) {
                if (!Modifier.isFinal(field.getModifiers())) {
                    superclass.getDeclaredField(field.getName()).set(this, field.get(document));
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException reason) {
            throw new NuxeoClientException(reason);
        }
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public String getId() {
        return uid;
    }

    public String getInputType() {
        return EntityTypes.DOCUMENT;
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
        rejectIfDateFound(key, value);
        properties.put(key, value);
        dirtyProperties.put(key, value);
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public Object getPropertyValue(String key) {
        return properties.get(key);
    }

    public void setPropertyValue(String key, Object value) {
        rejectIfDateFound(key, value);
        properties.put(key, value);
        dirtyProperties.put(key, value);
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
        this.properties.clear();
        if (properties != null) {
            rejectIfDateFound(null, properties);
            this.properties.putAll(properties);
        }
    }

    public void setDirtyProperties(Map<String, Object> dirtyProperties) {
        this.dirtyProperties.clear();
        if (dirtyProperties != null) {
            rejectIfDateFound(null, dirtyProperties);
            this.dirtyProperties.putAll(dirtyProperties);
        }
    }

    public void setContextParameters(Map<String, Object> contextParameters) {
        this.contextParameters.clear();
        if (contextParameters != null) {
            this.contextParameters.putAll(contextParameters);
        }
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

        if (repositoryName == null) {
            return fetchResponse(api.updateDocument(uid, this));
        }
        return fetchResponse(api.updateDocument(uid, this, repositoryName));

    }

    /* Audit Sync */

    public Audit fetchAudit() {

        if (repositoryName == null) {
            return fetchResponse(api.fetchAuditById(uid));
        }
        return fetchResponse(api.fetchAuditById(uid, repositoryName));

    }

    /* Audit Async */

    public void fetchAudit(Callback<Audit> callback) {

        if (repositoryName == null) {
            fetchResponse(api.fetchAuditById(uid), callback);
        }
        fetchResponse(api.fetchAuditById(uid, repositoryName), callback);

    }

    /* ACP Sync */

    public ACP fetchPermissions() {

        if (repositoryName == null) {
            return fetchResponse(api.fetchPermissionsById(uid));
        }
        return fetchResponse(api.fetchPermissionsById(uid, repositoryName));

    }

    /**
     * Add permission on the current document.
     *
     * @since 1.0
     * @param ace the permission.
     */
    public void addPermission(ACE ace) {
        Map<String, Object> params = toAutomationParameters(ace);
        params.put("user", ace.getUsername());
        nuxeoClient.automation(Operations.DOCUMENT_ADD_PERMISSION).input(this).parameters(params).execute();
    }

    protected Map<String, Object> toAutomationParameters(ACE ace) {
        Map<String, Object> params = new HashMap<>();
        params.put("permission", ace.getPermission());
        params.put("begin", ace.getBeginAsString());
        params.put("end", ace.getEndAsString());
        params.put("creator", ace.getCreator());
        params.put("blockInheritance", ace.isBlockInheritance());
        params.put("comment", ace.getComment());
        params.put("notify", ace.isNotify());
        return params;

    }

    /**
     * Add permission on the current document by passing the related email.
     *
     * @since 1.0
     * @param ace the permission.
     * @param email the invited email.
     */
    public void addInvitation(ACE ace, String email) {
        Map<String, Object> params = toAutomationParameters(ace);
        params.put("email", email);
        nuxeoClient.automation(Operations.DOCUMENT_ADD_PERMISSION).input(this).parameters(params).execute();
    }

    /**
     * Remove all permissions for a given username on the current document.
     *
     * @since 1.0
     * @param username User Name.
     */
    public void removePermission(String username) {
        removePermission(null, username, ACL.LOCAL_ACL);
    }

    /**
     * Remove all permissions for a given username, ace id, acl name on the current document.
     *
     * @since 1.0
     * @param aceId ACE ID.
     * @param username User Name.
     * @param ACLName Name of the ACL (local, inherited...).
     */
    public void removePermission(String aceId, String username, String ACLName) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", aceId);
        params.put("user", username);
        params.put("acl", ACLName);
        nuxeoClient.automation(Operations.DOCUMENT_REMOVE_PERMISSION).input(this).parameters(params).execute();
    }

    /* ACP Async */

    public void fetchPermissions(Callback<ACP> callback) {
        if (repositoryName == null) {
            fetchResponse(api.fetchPermissionsById(uid), callback);
        }
        fetchResponse(api.fetchPermissionsById(uid, repositoryName), callback);
    }

    /**
     * Add permission on the current document.
     *
     * @since 1.0
     * @param ace the permission.
     */
    // TODO check response type
    public void addPermission(ACE ace, Callback<ResponseBody> callback) {
        Map<String, Object> params = toAutomationParameters(ace);
        params.put("user", ace.getUsername());

        nuxeoClient.automation(Operations.DOCUMENT_ADD_PERMISSION).input(this).parameters(params).execute(callback);

    }

    /**
     * Add permission on the current document by passing the related email.
     *
     * @since 1.0
     * @param ace the permission.
     * @param email the invited email.
     */
    // TODO check response type
    public void addInvitation(ACE ace, String email, Callback<ResponseBody> callback) {
        Map<String, Object> params = toAutomationParameters(ace);
        params.put("email", email);

        nuxeoClient.automation(Operations.DOCUMENT_ADD_PERMISSION).input(this).parameters(params).execute(callback);

    }

    /* Children Sync */

    public Documents fetchChildren() {

        if (repositoryName == null) {
            return fetchResponse(api.fetchChildrenById(uid));
        }
        return fetchResponse(api.fetchChildrenById(uid, repositoryName));

    }

    /* Children Async */

    public void fetchChildren(Callback<Documents> callback) {

        if (repositoryName == null) {
            fetchResponse(api.fetchChildrenById(uid), callback);
        }
        fetchResponse(api.fetchChildrenById(uid, repositoryName), callback);

    }

    /* Blobs Sync */

    public Blob fetchBlob() {
        return fetchBlob(DEFAULT_FILE_CONTENT);
    }

    public Blob fetchBlob(String fieldPath) {

        if (repositoryName == null) {
            return fetchResponse(api.fetchBlobById(uid, fieldPath));
        }
        return fetchResponse(api.fetchBlobById(uid, fieldPath, repositoryName));

    }

    /* Blobs Async */

    public void fetchBlob(Callback<Blob> callback) {
        fetchBlob(DEFAULT_FILE_CONTENT, callback);
    }

    public void fetchBlob(String fieldPath, Callback<Blob> callback) {

        if (repositoryName == null) {
            fetchResponse(api.fetchBlobById(uid, fieldPath), callback);
        }
        fetchResponse(api.fetchBlobById(uid, fieldPath, repositoryName), callback);

    }

    /* Workflows Sync */

    public Workflows fetchWorkflowInstances() {

        if (repositoryName == null) {
            return fetchResponse(api.fetchWorkflowInstances(uid));
        }
        return fetchResponse(api.fetchWorkflowInstances(uid, repositoryName));
    }

    public Workflow startWorkflowInstance(Workflow workflow) {
        if (repositoryName == null) {
            return fetchResponse(api.startWorkflowInstanceWithDocId(uid, workflow));
        }
        return fetchResponse(api.startWorkflowInstanceWithDocId(uid, workflow, repositoryName));

    }

    /* Workflows Async */

    public void fetchWorkflowInstances(Callback<Workflows> callback) {
        if (repositoryName == null) {
            fetchResponse(api.fetchWorkflowInstances(uid), callback);
        }
        fetchResponse(api.fetchWorkflowInstances(uid, repositoryName), callback);
    }

    public void startWorkflowInstance(Workflow workflow, Callback<Workflow> callback) {
        if (repositoryName == null) {
            fetchResponse(api.startWorkflowInstanceWithDocId(uid, workflow), callback);
        }
        fetchResponse(api.startWorkflowInstanceWithDocId(uid, workflow, repositoryName), callback);
    }

    /* Task */

    public Task fetchTask() {

        if (repositoryName == null) {
            return fetchResponse(api.fetchTaskById(uid));
        }
        return fetchResponse(api.fetchTaskById(uid, repositoryName));

    }

    /**
     * @since 2.3
     */
    public boolean isProxy() {
        return isProxy;
    }

    /**
     * @since 2.3
     */
    public void setIsProxy(boolean isProxy) {
        this.isProxy = isProxy;
    }

    private void rejectIfDateFound(String key, Object value) {
        if (value instanceof Calendar || value instanceof Date) {
            throw new IllegalArgumentException(String.format(
                    "Property '%s' has value of type '%s'. However, date values are not supported in Nuxeo Java Client."
                            + " Please convert it to String with ISO 8601 format \"yyyy-MM-dd'T'HH:mm:ss.SSSXXX\""
                            + " before setting it as property.",
                    key, value.getClass().getTypeName()));
        } else if (value instanceof Collection) {
            for (Object item : (Collection) value) {
                rejectIfDateFound(key, item);
            }
        } else if (value instanceof Map) {
            for (Entry<String, Object> entry : ((Map<String, Object>) value).entrySet()) {
                rejectIfDateFound(entry.getKey(), entry.getValue());
            }
        } else if (value != null && value.getClass().isArray()) {
            for (int i = 0; i < Array.getLength(value); i++) {
                rejectIfDateFound(key, Array.get(value, i));
            }
        }
    }

}
