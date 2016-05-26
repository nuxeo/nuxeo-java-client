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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Request;
import okhttp3.ResponseBody;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.nuxeo.client.api.ConstantsV1;
import org.nuxeo.client.api.NuxeoClient;
import org.nuxeo.client.api.objects.blob.Blob;
import org.nuxeo.client.api.objects.blob.Blobs;
import org.nuxeo.client.internals.spi.NuxeoClientException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 0.1
 */
public abstract class NuxeoEntity<T> {

    @JsonProperty("entity-type")
    protected final String entityType;

    @JsonProperty("repository")
    protected String repositoryName;

    @JsonIgnore
    protected boolean refreshCache = false;

    @JsonIgnore
    protected NuxeoClient nuxeoClient;

    @JsonIgnore
    protected Object api;

    @JsonIgnore
    protected Class<Object> apiClass;

    /**
     * For Serialization purpose.
     */
    public NuxeoEntity(String entityType) {
        this.entityType = entityType;
    }

    /**
     * The constructor to use.
     */
    public NuxeoEntity(String entityType, NuxeoClient nuxeoClient, Class apiClass) {
        this.entityType = entityType;
        this.nuxeoClient = nuxeoClient;
        this.apiClass = apiClass;
    }

    public String getEntityType() {
        return entityType;
    }

    /**
     * Handle invocation of API Methods Asynchronously. Results will be returned in the given callback.
     */
    public void execute(Callback<T> callback, Object... parametersArray) {
        if (api == null) {
            api = nuxeoClient.getRetrofit().create(apiClass);
        }
        if (nuxeoClient == null) {
            throw new NuxeoClientException("You should pass to your Nuxeo object the client instance");
        }
        String method = getCurrentMethodName();
        Call<T> methodResult = getCall(api, method, parametersArray);
        methodResult.enqueue(callback);
    }

    /**
     * Handle cache and invocation of API methods.
     *
     * @return the response as business objects.
     */
    protected Object getResponse(Object... parametersArray) {
        if (nuxeoClient == null) {
            throw new NuxeoClientException("You should pass to your Nuxeo object the client instance");
        }
        if (api == null) {
            api = nuxeoClient.getRetrofit().create(apiClass);
        }
        String method = getCurrentMethodName();
        Call<?> methodResult = getCall(api, method, parametersArray);
        String cacheKey = Strings.EMPTY;
        if (nuxeoClient.isCacheEnabled()) {
            if (refreshCache) {
                this.refreshCache = false;
                nuxeoClient.getNuxeoCache().invalidateAll();
            } else {
                cacheKey = computeCacheKey(methodResult);
                NuxeoEntity result = (NuxeoEntity) nuxeoClient.getNuxeoCache().getBody(cacheKey);
                if (result != null) {
                    return result;
                }
            }
        }
        try {
            Response<?> response = methodResult.execute();
            // For redirect 308 -> the response should be success
            if (!response.isSuccessful() && response.code() != 308) {
                NuxeoClientException nuxeoClientException;
                String errorBody = response.errorBody().string();
                if (Strings.EMPTY.equals(errorBody)) {
                    nuxeoClientException = new NuxeoClientException(response.code(), response.message());
                } else if (!ConstantsV1.APPLICATION_JSON.equals(response.raw().body().contentType())) {
                    nuxeoClientException = new NuxeoClientException(response.code(), errorBody);
                } else {
                    nuxeoClientException = nuxeoClient.getConverterFactory().readJSON(errorBody,
                            NuxeoClientException.class);
                }
                throw nuxeoClientException;
            }
            if (nuxeoClient.isCacheEnabled()) {
                nuxeoClient.getNuxeoCache().put(cacheKey, response);
            }
            Object body = response.body();
            if (body instanceof ResponseBody) {
                return body;
            } else if (body == null) {
                if (response.code() == 204) {
                    return null;
                }
                return response;
            } else {
                return reconnectObject(body, api, nuxeoClient);
            }
        } catch (IOException reason) {
            throw new NuxeoClientException(reason);
        }
    }

    /**
     * Compute the cache key with request
     */
    protected String computeCacheKey(Call<?> methodResult) {
        Request originalRequest = methodResult.request();
        StringBuffer sb = new StringBuffer();
        sb.append(originalRequest.toString());
        sb.append(originalRequest.headers().toString());
        MessageDigest digest;
        try {
            digest = java.security.MessageDigest.getInstance(ConstantsV1.MD_5);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        digest.update(sb.toString().getBytes());
        byte messageDigest[] = digest.digest();
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < messageDigest.length; i++)
            hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
        return hexString.toString();
    }

    /**
     * Invoking the method of each class "API"
     */
    protected Call<T> getCall(Object api, String methodName, Object... parametersArray) {
        try {
            Method[] methods = api.getClass().getInterfaces()[0].getMethods();
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
            if (method == null) {
                throw new NuxeoClientException(String.format(
                        "No method found for API %s and method name '%s'. Check method name and parameters.", apiClass,
                        methodName));
            }
            return (Call<T>) method.invoke(api, parametersArray);
        } catch (IllegalArgumentException | IllegalAccessException reason) {
            throw new NuxeoClientException(
                    String.format(
                            "An issue has occured in the method found for API %s and method name '%s'. Check method and parameters types.",
                            apiClass, methodName), reason);
        } catch (InvocationTargetException reason) {
            throw new NuxeoClientException(reason.getTargetException().getMessage(), reason);
        }
    }

    protected String getCurrentMethodName() {
        StackTraceElement stackTraceElements[] = (new Throwable()).getStackTrace();
        return stackTraceElements[2].getMethodName();
    }

    protected Object reconnectObject(Object entity, Object api, NuxeoClient nuxeoClient) {
        if (entity instanceof NuxeoEntity) {
            ((NuxeoEntity) entity).nuxeoClient = nuxeoClient;
            ((NuxeoEntity) entity).api = api;
            ((NuxeoEntity) entity).apiClass = apiClass;
            if (entity instanceof Documents) {
                for (Document doc : ((Documents) entity).getDocuments()) {
                    doc.nuxeoClient = nuxeoClient;
                    doc.api = api;
                    doc.apiClass = apiClass;
                }
            } else if (entity instanceof Blobs) {
                for (Blob blob : ((Blobs) entity).getBlobs()) {
                    blob.nuxeoClient = nuxeoClient;
                    blob.api = api;
                    blob.apiClass = apiClass;
                }
            }
            return entity;
        } else if (entity instanceof List<?>) {
            List<NuxeoEntity> entities = new ArrayList<>();
            List<?> l = (List<?>) entity;
            for (Object item : l) {
                if (item instanceof NuxeoEntity) {
                    ((NuxeoEntity) item).nuxeoClient = nuxeoClient;
                    ((NuxeoEntity) item).api = api;
                    ((NuxeoEntity) item).apiClass = apiClass;
                    entities.add((NuxeoEntity) item);
                }
            }
            return entities;
        }
        return entity;
    }
}
