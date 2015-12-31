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
package org.nuxeo.java.client.api;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.nuxeo.java.client.api.cache.NuxeoResponseCache;
import org.nuxeo.java.client.api.cache.ResultCacheInMemory;
import org.nuxeo.java.client.api.marshaller.NuxeoConverterFactory;
import org.nuxeo.java.client.api.marshaller.NuxeoMarshaller;
import org.nuxeo.java.client.api.objects.CurrentUser;
import org.nuxeo.java.client.api.objects.DirectoryManager;
import org.nuxeo.java.client.api.objects.Operation;
import org.nuxeo.java.client.api.objects.Repository;
import org.nuxeo.java.client.api.objects.UserManager;
import org.nuxeo.java.client.internals.spi.NuxeoClientException;
import org.nuxeo.java.client.internals.spi.auth.BasicAuthInterceptor;

import retrofit.Retrofit;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

/**
 * @since 1.0
 */
public class NuxeoClient implements Client {

    protected final Retrofit retrofit;

    protected final Repository repository;

    protected final Operation automation;

    protected final CurrentUser currentUser;

    protected final OkHttpClient httpClient;

    protected final Retrofit.Builder builder;

    protected final UserManager userManager;

    protected final DirectoryManager directoryManager;

    protected NuxeoResponseCache nuxeoCache;

    protected final NuxeoConverterFactory converterFactory;

    public NuxeoClient(String url, String username, String password) {
        this(url, username, password, true);
    }

    public NuxeoClient(String url, String username, String password, boolean enableDefaultCache) {
        httpClient = new OkHttpClient();
        converterFactory = NuxeoConverterFactory.create();
        builder = new Retrofit.Builder().baseUrl(url + ConstantsV1.API_PATH).addConverterFactory(converterFactory);
        if (httpClient.interceptors().isEmpty()) {
            if (username != null && password != null) {
                setAuthenticationMethod(new BasicAuthInterceptor(username, password));
            } else {
                throw new NuxeoClientException("Define credentials");
            }
        }
        if (enableDefaultCache) {
            nuxeoCache = new ResultCacheInMemory();
        }
        retrofit = builder.client(httpClient).build();
        currentUser = new CurrentUser(this);
        repository = new Repository(this);
        automation = new Operation(this);
        userManager = new UserManager(this);
        directoryManager = new DirectoryManager(this);
    }

    public NuxeoClient registerMarshaller(NuxeoMarshaller<?> marshaller) {
        converterFactory.registerMarshaller(marshaller);
        return this;
    }

    public Repository getRepository() {
        repository.repositoryName(null);
        return repository;
    }

    public Repository repositoryName(String repositoryName) {
        return getRepository(repositoryName);
    }

    public Repository getRepository(String repositoryName) {
        repository.repositoryName(repositoryName);
        return repository;
    }

    public CurrentUser getCurrentUser() {
        return currentUser.getCurrentUser();
    }

    public void logout() {
        retrofit.client().interceptors().clear();
    }

    @Override
    public NuxeoClient header(String header, String value) {
        retrofit.client().interceptors().add(chain -> {
            Request request = chain.request();
            request = request.newBuilder().addHeader(header, value).build();
            Response response = chain.proceed(request);
            return response;
        });
        return this;
    }

    public NuxeoClient enrichers(String... enrichers) {
        for (String enricher : enrichers) {
            header(ConstantsV1.HEADER_ENRICHERS, enricher);
        }
        return this;
    }

    public NuxeoClient voidOperation(boolean value) {
        header(ConstantsV1.HEADER_VOID_OPERATION, Boolean.toString(value));
        return this;
    }

    public NuxeoClient transactionTimeout(long timeout) {
        header(ConstantsV1.HEADER_TX_TIMEOUT, String.valueOf(timeout));
        return this;
    }

    public NuxeoClient fetch(String... fetchs) {
        for (String fetch : fetchs) {
            header(ConstantsV1.HEADER_FETCH, fetch);
        }
        return this;
    }

    public NuxeoClient depth(String value) {
        header(ConstantsV1.HEADER_DEPTH, value);
        return this;
    }

    public NuxeoClient version(String value) {
        header(ConstantsV1.HEADER_VERSIONING, value);
        return this;
    }

    public NuxeoClient schemas(String... properties) {
        for (String property : properties) {
            header(ConstantsV1.HEADER_PROPERTIES, property);
        }
        return this;
    }

    @Override
    public NuxeoClient setCache(NuxeoResponseCache nuxeoCache) {
        this.nuxeoCache = nuxeoCache;
        return this;
    }

    @Override
    public NuxeoClient setAuthenticationMethod(Interceptor interceptor) {
        httpClient.interceptors().add(interceptor);
        return this;
    }

    @Override
    public NuxeoClient timeout(long timeout) {
        retrofit.client().setConnectTimeout(timeout, TimeUnit.SECONDS);
        return this;
    }

    @Override
    public String getBaseUrl() {
        return retrofit.baseUrl().toString();
    }

    @Override
    public void shutdown() {
        logout();
    }

    @Override
    public Retrofit getRetrofit() {
        return retrofit;
    }

    @Override
    public NuxeoResponseCache getNuxeoCache() {
        return nuxeoCache;
    }

    @Override
    public boolean isCacheEnabled() {
        return nuxeoCache != null;
    }

    @Override
    public Response get(String url) {
        Request request = new Request.Builder().url(url).build();
        try {
            return retrofit.client().newCall(request).execute();
        } catch (IOException e) {
            throw new NuxeoClientException(e);
        }
    }

    @Override
    public Response delete(String url, String json) {
        Request request;
        if (json != null) {
            RequestBody body = RequestBody.create(ConstantsV1.APPLICATION_JSON_CHARSET_UTF_8, json);
            request = new Request.Builder().url(url).delete(body).build();
        } else {
            request = new Request.Builder().url(url).build();
        }
        try {
            return retrofit.client().newCall(request).execute();
        } catch (IOException e) {
            throw new NuxeoClientException(e);
        }
    }

    @Override
    public Response put(String url, String json) {
        Request request;
        if (json != null) {
            RequestBody body = RequestBody.create(ConstantsV1.APPLICATION_JSON_CHARSET_UTF_8, json);
            request = new Request.Builder().url(url).put(body).build();
        } else {
            request = new Request.Builder().url(url).build();
        }
        try {
            return retrofit.client().newCall(request).execute();
        } catch (IOException e) {
            throw new NuxeoClientException(e);
        }
    }

    @Override
    public Response post(String url, String json) {
        Request request;
        if (json != null) {
            RequestBody body = RequestBody.create(ConstantsV1.APPLICATION_JSON_CHARSET_UTF_8, json);
            request = new Request.Builder().url(url).post(body).build();
        } else {
            request = new Request.Builder().url(url).build();
        }
        try {
            return retrofit.client().newCall(request).execute();
        } catch (IOException e) {
            throw new NuxeoClientException(e);
        }
    }

    public Operation automation() {
        return automation;
    }

    public NuxeoConverterFactory getConverterFactory() {
        return converterFactory;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public DirectoryManager getDirectoryManager() {
        return directoryManager;
    }
}
