/*
 * (C) Copyright 2016-2018 Nuxeo (http://nuxeo.com/) and others.
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
import java.util.function.BiConsumer;

import org.nuxeo.client.ConstantsV1;
import org.nuxeo.client.NuxeoClient;
import org.nuxeo.client.Operations;
import org.nuxeo.client.marshaller.EntityValueDeserializer;
import org.nuxeo.client.methods.RepositoryAPI;
import org.nuxeo.client.objects.acl.ACE;
import org.nuxeo.client.objects.acl.ACL;
import org.nuxeo.client.objects.acl.ACP;
import org.nuxeo.client.objects.annotation.Annotation;
import org.nuxeo.client.objects.annotation.Annotations;
import org.nuxeo.client.objects.audit.Audit;
import org.nuxeo.client.objects.blob.FileBlob;
import org.nuxeo.client.objects.blob.StreamBlob;
import org.nuxeo.client.objects.task.Task;
import org.nuxeo.client.objects.workflow.Workflow;
import org.nuxeo.client.objects.workflow.Workflows;
import org.nuxeo.client.spi.NuxeoClientException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import retrofit2.Callback;

/**
 * @since 0.1
 */
public class Document extends RepositoryEntity<RepositoryAPI, Document> {

    public static final String DEFAULT_FILE_CONTENT = "file:content";

    protected static final String DELETED_STATE = "deleted";

    protected String path;

    protected String type;

    protected String state;

    protected String lockOwner;

    protected String lockCreated;

    protected String versionLabel;

    protected String isCheckedOut;

    protected String lastModified;

    // We need this @JsonProperty because setProperties has some business logic whose Jackson doesn't have to use
    @JsonProperty("properties")
    @JsonDeserialize(contentUsing = EntityValueDeserializer.class)
    protected Map<String, Object> properties = new HashMap<>();

    @JsonIgnore
    protected Map<String, Object> dirtyProperties = new HashMap<>();

    @JsonDeserialize(contentUsing = EntityValueDeserializer.class)
    protected Map<String, Object> contextParameters = new HashMap<>();

    protected String changeToken;

    protected List<String> facets;

    protected String parentRef;

    protected String uid;

    protected String title;

    protected String name;

    /**
     * @since 2.3
     */
    @JsonProperty("isProxy")
    protected boolean isProxy;

    @JsonProperty("isTrashed")
    protected Boolean isTrashed;

    /**
     * For internal marshalling purpose.
     */
    protected Document() {
        this(ConstantsV1.DEFAULT_DOC_TYPE, ConstantsV1.DEFAULT_DOC_TYPE);
    }

    /**
     * Protected constructor for adapters implementation.
     *
     * @since 3.0 this constructor has changed its meaning, it is used for adapters, see
     *        org.nuxeo.client.objects.DataSet.
     */
    protected Document(String uid, String type) {
        super(EntityTypes.DOCUMENT, RepositoryAPI.class);
        this.uid = uid;
        this.type = type;
    }

    /**
     * This constructor is providing a way to create 'adapters' of a document. See org.nuxeo.client.objects.DataSet in
     * nuxeo-java-client-test.
     *
     * @since 1.0
     * @param document the document to copy from the sub class.
     */
    protected Document(Document document) {
        super(EntityTypes.DOCUMENT, RepositoryAPI.class);
        type = ConstantsV1.DEFAULT_DOC_TYPE;
        try {
            Class<?> superclass = getClass().getSuperclass();
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
        } catch (ReflectiveOperationException reason) {
            throw new NuxeoClientException("Error during construction of document adapter", reason);
        }
    }

    /**
     * Regular way to instantiate a {@link Document} in order to update it.
     *
     * @since 3.0
     */
    public static Document createWithId(String uid, String type) {
        return new Document(uid, type);
    }

    /**
     * Regular way to instantiate a {@link Document} in order to create it.
     *
     * @since 3.0
     */
    public static Document createWithName(String name, String type) {
        Document document = new Document(null, type);
        document.name = name;
        return document;
    }

    public String getId() {
        return uid;
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
        return (isCheckedOut == null) ? null : Boolean.valueOf(isCheckedOut);
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

    public Map<String, Object> getProperties() {
        return properties;
    }

    @SuppressWarnings("unchecked")
    public <T> T getPropertyValue(String key) {
        return (T) properties.get(key);
    }

    /**
     * Sets one property value, property's value will also be put to dirty properties.
     */
    public void setPropertyValue(String key, Object value) {
        rejectIfDateFound(key, value);
        properties.put(key, value);
        dirtyProperties.put(key, value);
    }

    /**
     * Sets several properties in one call, this method will also put input properties to dirty properties.
     */
    @JsonIgnore
    public void setProperties(Map<String, Object> properties) {
        this.properties.clear();
        if (properties != null) {
            rejectIfDateFound(null, properties);
            this.properties.putAll(properties);
        }
        // do the logic as it because we can call setProperties(getDirtyProperties()) -> the expected result is to have
        // dirty properties in properties and an empty dirty properties
        this.dirtyProperties.clear();
        if (properties != null) {
            this.dirtyProperties.putAll(properties);
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

    /**
     * Get the dirty properties.
     */
    public Map<String, Object> getDirtyProperties() {
        return dirtyProperties;
    }

    /**
     * @return The value associates to the input key from the context parameters.
     * @since 3.0
     */
    @SuppressWarnings("unchecked")
    public <T> T getContextParameter(String key) {
        return (T) contextParameters.get(key);
    }

    public Map<String, Object> getContextParameters() {
        return contextParameters;
    }

    /*------------*
     *  Services  *
     *------------*/

    public Document updateDocument() {
        setProperties(getDirtyProperties());
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
    public Document addPermission(ACE ace) {
        Map<String, Object> params = toAutomationParameters(ace);
        params.put("user", ace.getUsername());
        return nuxeoClient.operation(Operations.DOCUMENT_ADD_PERMISSION).input(this).parameters(params).execute();
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
    public Document addInvitation(ACE ace, String email) {
        Map<String, Object> params = toAutomationParameters(ace);
        params.put("email", email);
        return nuxeoClient.operation(Operations.DOCUMENT_ADD_PERMISSION).input(this).parameters(params).execute();
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
     * @param aclName Name of the ACL (local, inherited...).
     */
    public void removePermission(String aceId, String username, String aclName) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", aceId);
        params.put("user", username);
        params.put("acl", aclName);
        nuxeoClient.operation(Operations.DOCUMENT_REMOVE_PERMISSION).input(this).parameters(params).execute();
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
    public void addPermission(ACE ace, Callback<Document> callback) {
        Map<String, Object> params = toAutomationParameters(ace);
        params.put("user", ace.getUsername());
        nuxeoClient.operation(Operations.DOCUMENT_ADD_PERMISSION).input(this).parameters(params).execute(callback);
    }

    /**
     * Add permission on the current document by passing the related email.
     *
     * @since 1.0
     * @param ace the permission.
     * @param email the invited email.
     */
    public void addInvitation(ACE ace, String email, Callback<Document> callback) {
        Map<String, Object> params = toAutomationParameters(ace);
        params.put("email", email);
        nuxeoClient.operation(Operations.DOCUMENT_ADD_PERMISSION).input(this).parameters(params).execute(callback);
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

    /**
     * @deprecated since 3.1, use {@link #streamBlob()} instead
     */
    @Deprecated
    public FileBlob fetchBlob() {
        return fetchBlob(DEFAULT_FILE_CONTENT);
    }

    /**
     * @deprecated since 3.1, use {@link #streamBlob(String)} instead
     */
    @Deprecated
    public FileBlob fetchBlob(String fieldPath) {
        if (repositoryName == null) {
            return fetchResponse(api.fetchBlobById(uid, fieldPath));
        }
        return fetchResponse(api.fetchBlobById(uid, fieldPath, repositoryName));
    }

    public StreamBlob streamBlob() {
        return streamBlob(DEFAULT_FILE_CONTENT);
    }

    public StreamBlob streamBlob(String fieldPath) {
        if (repositoryName == null) {
            return fetchResponse(api.streamBlobById(uid, fieldPath));
        }
        return fetchResponse(api.streamBlobById(uid, fieldPath, repositoryName));
    }

    /* Blobs Async */

    /**
     * @deprecated since 3.1, use {@link #streamBlob(Callback)} instead
     */
    @Deprecated
    public void fetchBlob(Callback<FileBlob> callback) {
        fetchBlob(DEFAULT_FILE_CONTENT, callback);
    }

    /**
     * @deprecated since 3.1, use {@link #streamBlob(String, Callback)} instead
     */
    @Deprecated
    public void fetchBlob(String fieldPath, Callback<FileBlob> callback) {
        if (repositoryName == null) {
            fetchResponse(api.fetchBlobById(uid, fieldPath), callback);
        }
        fetchResponse(api.fetchBlobById(uid, fieldPath, repositoryName), callback);
    }

    public void streamBlob(Callback<StreamBlob> callback) {
        streamBlob(DEFAULT_FILE_CONTENT, callback);
    }

    public void streamBlob(String fieldPath, Callback<StreamBlob> callback) {
        if (repositoryName == null) {
            fetchResponse(api.streamBlobById(uid, fieldPath), callback);
        }
        fetchResponse(api.streamBlobById(uid, fieldPath, repositoryName), callback);
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
     * CAUTION: Only available for Nuxeo Server greater than LTS 2016 - 8.10
     *
     * @since 2.3
     */
    public boolean isProxy() {
        return isProxy;
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

    /* Annotations */

    /**
     * This API is available since Nuxeo Server 10.2.
     *
     * @since 3.1
     */
    public Annotation createAnnotation(String annotationId, String entity) {
        return createAnnotation(annotationId, DEFAULT_FILE_CONTENT, entity);
    }


    /**
     * This API is available since Nuxeo Server 10.2.
     *
     * @since 3.1
     */
    public Annotation createAnnotation(String annotationId, String xpath, String entity) {
        Annotation annotation = new Annotation(annotationId, uid, xpath);
        annotation.setEntity(entity);
        return createAnnotation(annotation);
    }

    /**
     * This API is available since Nuxeo Server 10.2.
     *
     * @since 3.1
     */
    public Annotation createAnnotation(Annotation annotation) {
        if (repositoryName == null) {
            return fetchResponse(api.createAnnotation(uid, annotation));
        }
        return fetchResponse(api.createAnnotation(uid, annotation, repositoryName));
    }

    /**
     * Fetches annotations for {@link #DEFAULT_FILE_CONTENT} blob.
     * <p />
     * This API is available since Nuxeo Server 10.2.
     *
     * @since 3.1
     */
    public Annotations fetchAnnotations() {
        return fetchAnnotations(DEFAULT_FILE_CONTENT);
    }

    /**
     * This API is available since Nuxeo Server 10.2.
     *
     * @since 3.1
     */
    public Annotations fetchAnnotations(String xpath) {
        if (repositoryName == null) {
            return fetchResponse(api.fetchAnnotationsByXPath(uid, xpath));
        }
        return fetchResponse(api.fetchAnnotationsByXPath(uid, repositoryName, xpath));
    }

    /**
     * This API is available since Nuxeo Server 10.2.
     *
     * @since 3.1
     */
    public Annotation fetchAnnotationById(String annotationId) {
        if (repositoryName == null) {
            return fetchResponse(api.fetchAnnotationById(uid, annotationId));
        }
        return fetchResponse(api.fetchAnnotationById(uid, annotationId, repositoryName));
    }

    /**
     * This API is available since Nuxeo Server 10.2.
     *
     * @since 3.1
     */
    public Annotation updateAnnotation(Annotation annotation) {
        if (repositoryName == null) {
            return fetchResponse(api.updateAnnotation(uid, annotation));
        }
        return fetchResponse(api.updateAnnotation(uid, annotation, repositoryName));
    }

    public void deleteAnnotation(String annotationId) {
        if (repositoryName == null) {
            fetchResponse(api.deleteAnnotation(uid, annotationId));
        } else {
            fetchResponse(api.deleteAnnotation(uid, annotationId, repositoryName));
        }
    }

    /* Trash management */

    /**
     * @since 3.1
     */
    public boolean isTrashed() {
        return isTrashed != null ? isTrashed : DELETED_STATE.equals(state);
    }

    /**
     * This API is available since Nuxeo Server 10.2.
     *
     * @since 3.1
     */
    public Document trash() {
        return nuxeoClient.operation(Operations.DOCUMENT_TRASH).input(this).execute();
    }

    /**
     * This API is available since Nuxeo Server 10.2.
     *
     * @since 3.1
     */
    public Document untrash() {
        return nuxeoClient.operation(Operations.DOCUMENT_UNTRASH).input(this).execute();
    }

    @Override
    public void reconnectWith(NuxeoClient nuxeoClient) {
        super.reconnectWith(nuxeoClient);
        BiConsumer<String, Object> reconnect = (key, value) -> {
            if (value instanceof Connectable) {
                ((Connectable) value).reconnectWith(nuxeoClient);
            }
        };
        // Re-connect possible objects
        properties.forEach(reconnect);
        contextParameters.forEach(reconnect);
    }

}
