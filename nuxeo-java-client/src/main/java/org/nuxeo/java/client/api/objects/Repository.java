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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.DeserializationFeature;
import org.nuxeo.java.client.api.methods.RepositoryAPI;
import org.nuxeo.java.client.internals.spi.NuxeoClientException;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 1.0
 */
public class Repository<T> extends NuxeoObject {

    protected final RepositoryAPI repositoryAPI;

    protected String repositoryName;

    public Repository(Retrofit retrofit) {
        super("document");
        repositoryAPI = retrofit.create(RepositoryAPI.class);
    }

    public Repository repositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
        return this;
    }

    /* By Id - Sync */

    public Document getDocumentById(String documentId)  {
        return (Document) getResponse(getCurrentMethodName(), documentId);
    }

    public Document createDocumentById(String parentId, Document document)  {
        return (Document) getResponse(getCurrentMethodName(), parentId, document);
    }

    public Document updateDocumentById(String documentId, Document document)  {
        return (Document) getResponse(getCurrentMethodName(), documentId, document);
    }

    public Document deleteDocumentById(String documentId)  {
        return (Document) getResponse(getCurrentMethodName(), documentId);
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

    public void updateDocumentById(String documentId, Document document, Callback<Document> callback) {
        if (repositoryName == null) {
            repositoryAPI.updateDocumentById(documentId, document).enqueue(callback);
        } else {
            repositoryAPI.updateDocumentById(documentId, document, repositoryName).enqueue(callback);
        }
    }

    public void deleteDocumentById(String documentId, Callback<Document> callback) {
        if (repositoryName == null) {
            repositoryAPI.deleteDocumentById(documentId).enqueue(callback);
        } else {
            repositoryAPI.deleteDocumentById(documentId, repositoryName).enqueue(callback);
        }
    }

    /* By Path - Sync */

    public Document getDocumentRoot()  {
        return (Document) getResponse(getCurrentMethodName());
    }

    public Document getDocumentByPath(String documentPath)  {
        return (Document) getResponse(getCurrentMethodName(), documentPath);
    }

    public Document createDocumentByPath(String parentPath, Document document)  {
        return (Document) getResponse(getCurrentMethodName(), parentPath, document);
    }

    public Document updateDocumentByPath(String documentPath, Document document)  {
        return (Document) getResponse(getCurrentMethodName(), documentPath, document);
    }

    public Document deleteDocumentByPath(String documentPath)  {
        return (Document) getResponse(getCurrentMethodName(), documentPath);
    }

    /* By Path - Async */

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

    public void updateDocumentByPath(String documentPath, Document document, Callback<Document> callback) {
        if (repositoryName == null) {
            repositoryAPI.updateDocumentByPath(documentPath, document).enqueue(callback);
        } else {
            repositoryAPI.updateDocumentByPath(documentPath, document, repositoryName).enqueue(callback);
        }
    }

    public void deleteDocumentByPath(String documentPath, Callback<Document> callback) {
        if (repositoryName == null) {
            repositoryAPI.deleteDocumentByPath(documentPath).enqueue(callback);
        } else {
            repositoryAPI.deleteDocumentByPath(documentPath, repositoryName).enqueue(callback);
        }
    }

    /* Query - Sync */

    public List<Document> query(String query, String pageSize, String currentPageIndex, String maxResults,
            String sortBy, String sortOrder, String queryParams)  {
        return (List<Document>) getResponse(getCurrentMethodName(), query, pageSize, currentPageIndex, maxResults, sortBy, sortOrder,
                queryParams);
    }

    public List<Document> queryByProvider(String providerName, String pageSize, String currentPageIndex,
            String maxResults, String sortBy, String sortOrder, String queryParams)  {
        return (List<Document>) getResponse(getCurrentMethodName(), providerName, pageSize, currentPageIndex, maxResults, sortBy, sortOrder, queryParams);
    }

    /* Query - Async */

    public void getDocumentRoot(Callback<Document> callback)  {
        // TODO: JAVACLIENT-20
        //executeAsync(getCurrentMethodName(), callback);
        if (repositoryName == null) {
            repositoryAPI.getDocumentRoot().enqueue(callback);
        } else {
            repositoryAPI.getDocumentRoot(repositoryName).enqueue(callback);
        }
    }

    public void query(String query, String pageSize, String currentPageIndex, String maxResults, String sortBy,
            String sortOrder, String queryParams, Callback<List<Document>> callback) {
        repositoryAPI.queryByProvider(query, pageSize, currentPageIndex, maxResults, sortBy, sortOrder, queryParams)
                     .enqueue(callback);
    }

    public void queryByProvider(String providerName, String pageSize, String currentPageIndex, String maxResults,
            String sortBy, String sortOrder, String queryParams, Callback<List<Document>> callback) {
        repositoryAPI.queryByProvider(providerName, pageSize, currentPageIndex, maxResults, sortBy, sortOrder,
                queryParams).enqueue(callback);
    }


    /* Internal */

    // TODO: JAVACLIENT-20
//    protected void executeAsync(String method, Callback<T> callback, Object... parametersArray) {
//        Call<?> methodResult = getCall(method, parametersArray);
//        methodResult.enqueue(callback);
//    }


    protected Object getResponse(String method, Object... parametersArray) {
        Call<?> methodResult = getCall(method, parametersArray);
        try {
            Response<?> response = methodResult.execute();
            if (!response.isSuccess()) {
                ObjectMapper objectMapper = new ObjectMapper();
                // TODO JAVACLIENT-21
                objectMapper.configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                NuxeoClientException nuxeoClientException = objectMapper.readValue(response.errorBody().string(),NuxeoClientException.class);
                throw nuxeoClientException;
            }
            return response.body();
        } catch (IOException reason) {
            throw new NuxeoClientException(reason);
        }
    }

    protected Call<?> getCall(String methodName, Object... parametersArray) {
        try {
            Method[] methods = RepositoryAPI.class.getMethods();
            List<Object> parameters = new ArrayList<>(Arrays.asList(parametersArray));
            if (repositoryName != null)
                parameters.add(repositoryName);
            parametersArray = parameters.toArray();
            Method method = null;
            for (Method currentMethod : methods) {
                if (currentMethod.getName().equals(methodName)) {
                    if (currentMethod.getParameterTypes().length == parametersArray.length) {
                        method = currentMethod;
                        break;
                    }
                }
            }
            return (Call<?>) method.invoke(repositoryAPI, parametersArray);
        } catch (IllegalAccessException | InvocationTargetException reason) {
            throw new NuxeoClientException(reason);
        }
    }

    protected String getCurrentMethodName() {
        StackTraceElement stackTraceElements[] = (new Throwable()).getStackTrace();
        return stackTraceElements[1].getMethodName();
    }

}
