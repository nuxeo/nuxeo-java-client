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
package org.nuxeo.java.client.api.objects;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.nuxeo.java.client.api.ConstantsV1;
import org.nuxeo.java.client.api.NuxeoClient;
import org.nuxeo.java.client.internals.spi.NuxeoClientException;

import retrofit.Call;
import retrofit.Response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.ResponseBody;

/**
 * @since 1.0
 */
public abstract class NuxeoEntity {

    private static final Logger logger = LogManager.getLogger(NuxeoEntity.class);

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

    /**
     * For Serialization purpose.
     */
    public NuxeoEntity(String entityType) {
        this.entityType = entityType;
    }

    /**
     * The constructor to use.
     */
    public NuxeoEntity(String entityType, NuxeoClient nuxeoClient, Class api) {
        this.entityType = entityType;
        this.nuxeoClient = nuxeoClient;
        this.api = nuxeoClient.getRetrofit().create(api);
    }

    public String getEntityType() {
        return entityType;
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
            if (!response.isSuccess()) {
                NuxeoClientException nuxeoClientException;
                String errorBody = response.errorBody().string();
                if (errorBody.equals(Strings.EMPTY)) {
                    nuxeoClientException = new NuxeoClientException(response.code(), response.message());
                } else if (!response.raw().body().contentType().equals(ConstantsV1.APPLICATION_JSON)) {
                    nuxeoClientException = new NuxeoClientException(response.code(), errorBody);
                } else {
                    nuxeoClientException = (NuxeoClientException) nuxeoClient.getConverterFactory().readJSON(errorBody,
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
                return response;
            } else {
                return reconnectObject((NuxeoEntity) body, api, nuxeoClient);
            }
        } catch (IOException reason) {
            throw new NuxeoClientException(reason);
        }
    }

    /**
     * Compute the cache key with request
     */
    protected String computeCacheKey(Call<?> methodResult) {
        com.squareup.okhttp.Call rawCall;
        Request originalRequest;
        try {
            // TODO JAVACLIENT-26
            Method rawCallMethod = methodResult.getClass().getDeclaredMethod(ConstantsV1.CREATE_RAW_CALL);
            rawCallMethod.setAccessible(true);
            rawCall = (com.squareup.okhttp.Call) rawCallMethod.invoke(methodResult);
            Field originalRequestField = rawCall.getClass().getDeclaredField(ConstantsV1.ORIGINAL_REQUEST);
            originalRequestField.setAccessible(true);
            originalRequest = (Request) originalRequestField.get(rawCall);
            logger.debug("Request:" + originalRequest.toString());
        } catch (NoSuchFieldException | NoSuchMethodException | IllegalAccessException reason) {
            throw new NuxeoClientException(reason);
        } catch (InvocationTargetException reason) {
            throw new NuxeoClientException(reason.getTargetException().getMessage(), reason);
        }
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
    protected Call<?> getCall(Object api, String methodName, Object... parametersArray) {
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
            return (Call<?>) method.invoke(api, parametersArray);
        } catch (IllegalArgumentException | IllegalAccessException reason) {
            throw new NuxeoClientException(reason);
        } catch(InvocationTargetException reason){
            throw new NuxeoClientException(reason.getTargetException().getMessage(), reason);
        }
    }

    protected String getCurrentMethodName() {
        StackTraceElement stackTraceElements[] = (new Throwable()).getStackTrace();
        return stackTraceElements[2].getMethodName();
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public NuxeoEntity reconnectObject(NuxeoEntity nuxeoEntity, Object api, NuxeoClient nuxeoClient) {
        nuxeoEntity.nuxeoClient = nuxeoClient;
        nuxeoEntity.api = api;
        return nuxeoEntity;
    }
}
