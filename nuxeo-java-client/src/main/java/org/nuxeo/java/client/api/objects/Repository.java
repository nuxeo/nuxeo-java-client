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

import com.squareup.okhttp.ResponseBody;
import org.nuxeo.java.client.api.ConstantsV1;
import org.nuxeo.java.client.api.NuxeoClient;
import org.nuxeo.java.client.api.methods.RepositoryAPI;

import retrofit.Callback;

/**
 * @since 1.0
 */
public class Repository extends NuxeoObject {

    protected final RepositoryAPI repositoryAPI;

    public Repository(NuxeoClient nuxeoClient) {
        super(ConstantsV1.ENTITY_TYPE_DOCUMENT, nuxeoClient);
        repositoryAPI = nuxeoClient.getRetrofit().create(RepositoryAPI.class);
    }

    public Repository repositoryName(String repositoryName) {
        super.repositoryName = repositoryName;
        return this;
    }

    /**
     * Force the cache refresh.
     */
    public Repository refreshCache() {
        this.refreshCache = true;
        return this;
    }

    /* By Id - Sync */

    public Document getDocumentById(String documentId) {
        return (Document) getResponse(repositoryAPI, documentId);
    }

    public Document createDocumentById(String parentId, Document document) {
        return (Document) getResponse(repositoryAPI, parentId, document);
    }

    public Document updateDocument(Document document) {
        document.setProperties(document.getDirtyProperties());
        return (Document) getResponse(repositoryAPI, document.getId(), document);

    }

    /* By Id - Async */

    public void getDocumentById(String documentId, Callback<Document> callback) {
        if (repositoryName == null) {
            repositoryAPI.getDocumentById(documentId).enqueue(callback);
        } else {
            repositoryAPI.getDocumentById(documentId, repositoryName).enqueue(callback);
        }
    }

    public void createDocumentById(String parentId, Document document, Callback<Document> callback) {
        if (repositoryName == null) {
            repositoryAPI.createDocumentById(parentId, document).enqueue(callback);
        } else {
            repositoryAPI.createDocumentById(parentId, document).enqueue(callback);
        }
    }

    public void updateDocument(Document document, Callback<Document> callback) {
        document.setProperties(document.getDirtyProperties());
        if (repositoryName == null) {
            repositoryAPI.updateDocument(document.getId(), document).enqueue(callback);
        } else {
            repositoryAPI.updateDocument(document.getId(), document, repositoryName).enqueue(callback);
        }
    }

    public void deleteDocument(Document document, Callback<ResponseBody> callback) {
        if (repositoryName == null) {
            repositoryAPI.deleteDocument(document.getId()).enqueue(callback);
        } else {
            repositoryAPI.deleteDocument(document.getId(), repositoryName).enqueue(callback);
        }
    }

    /* By Path - Sync */

    public Document getDocumentRoot() {
        return (Document) getResponse(repositoryAPI);
    }

    public Document getDocumentByPath(String documentPath) {
        return (Document) getResponse(repositoryAPI, documentPath);
    }

    public Document createDocumentByPath(String parentPath, Document document) {
        return (Document) getResponse(repositoryAPI, parentPath, document);
    }

    public void deleteDocument(Document document) {
        getResponse(repositoryAPI, document.getId());
    }

    /* By Path - Async */

    public void getDocumentRoot(Callback<Document> callback) {
        // TODO: JAVACLIENT-20
        // executeAsync(getCurrentMethodName(), callback);
        if (repositoryName == null) {
            repositoryAPI.getDocumentRoot().enqueue(callback);
        } else {
            repositoryAPI.getDocumentRoot(repositoryName).enqueue(callback);
        }
    }

    public void getDocumentByPath(String documentPath, Callback<Document> callback) {
        if (repositoryName == null) {
            repositoryAPI.getDocumentByPath(documentPath).enqueue(callback);
        } else {
            repositoryAPI.getDocumentByPath(documentPath, repositoryName).enqueue(callback);
        }
    }

    public void createDocumentByPath(String parentPath, Document document, Callback<Document> callback) {
        if (repositoryName == null) {
            repositoryAPI.createDocumentByPath(parentPath, document).enqueue(callback);
        } else {
            repositoryAPI.createDocumentByPath(parentPath, document).enqueue(callback);
        }
    }

    /* Query - Sync */

    public Documents query(String query) {
        return (Documents) getResponse(repositoryAPI, query);
    }

    public Documents query(String query, String pageSize, String currentPageIndex, String maxResults, String sortBy,
            String sortOrder, String queryParams) {
        return (Documents) getResponse(repositoryAPI, query, pageSize, currentPageIndex,
                maxResults, sortBy, sortOrder, queryParams);
    }

    public Documents queryByProvider(String providerName, String pageSize, String currentPageIndex, String maxResults,
            String sortBy, String sortOrder, String queryParams) {
        return (Documents) getResponse(repositoryAPI, providerName, pageSize, currentPageIndex,
                maxResults, sortBy, sortOrder, queryParams);
    }

    /* Query - Async */

    public void query(String query, Callback<Documents> callback) {
        repositoryAPI.query(query).enqueue(callback);
    }

    public void query(String query, String pageSize, String currentPageIndex, String maxResults, String sortBy,
            String sortOrder, String queryParams, Callback<Documents> callback) {
        repositoryAPI.query(query, pageSize, currentPageIndex, maxResults, sortBy, sortOrder, queryParams).enqueue(
                callback);
    }

    public void queryByProvider(String providerName, String pageSize, String currentPageIndex, String maxResults,
            String sortBy, String sortOrder, String queryParams, Callback<Documents> callback) {
        repositoryAPI.queryByProvider(providerName, pageSize, currentPageIndex, maxResults, sortBy, sortOrder,
                queryParams).enqueue(callback);
    }

    /* Internal */

    // TODO: JAVACLIENT-20
    // protected void executeAsync(String method, Callback<T> callback, Object... parametersArray) {
    // Call<?> methodResult = getCall(method, parametersArray);
    // methodResult.enqueue(callback);
    // }

}
