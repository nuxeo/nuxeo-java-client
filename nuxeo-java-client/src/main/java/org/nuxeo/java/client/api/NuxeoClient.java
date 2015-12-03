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
package org.nuxeo.java.client.api;

import java.util.concurrent.TimeUnit;

import org.nuxeo.java.client.api.objects.CurrentUser;
import org.nuxeo.java.client.api.objects.Repository;
import org.nuxeo.java.client.internals.spi.NuxeoClientException;
import org.nuxeo.java.client.internals.spi.auth.BasicAuthInterceptor;

import retrofit.JacksonConverterFactory;
import retrofit.Retrofit;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;

/**
 * @since 1.0
 */
public class NuxeoClient implements Client {

    public static final String VERSION = "v1/";

    public static final String API_PATH = "/api/" + VERSION;

    protected Retrofit retrofit;

    protected final Repository repository;

    protected final CurrentUser currentUser;

    protected final OkHttpClient httpClient;

    protected final Retrofit.Builder builder;

    public NuxeoClient(String url, String username, String password)  {
        httpClient = new OkHttpClient();
        builder = new Retrofit.Builder().baseUrl(url + API_PATH).addConverterFactory(JacksonConverterFactory.create());
        if (httpClient.interceptors().isEmpty()) {
            if (username != null && password != null) {
                setAuthenticationMethod(new BasicAuthInterceptor(username, password));
            } else {
                throw new NuxeoClientException("Define credentials");
            }
        }
        currentUser = new CurrentUser(retrofit);
        repository = new Repository(retrofit);
    }

    public Repository getRepository() {
        repository.repositoryName(null);
        return repository;
    }

    public Repository getRepository(String repositoryName) {
        repository.repositoryName(repositoryName);
        return repository;
    }

    public CurrentUser getCurrentUser()  {
        return currentUser.getCurrentUser();
    }

    public void logout()  {
        httpClient.interceptors().clear();
    }

    @Override
    public NuxeoClient setAuthenticationMethod(Interceptor interceptor) {
        httpClient.interceptors().add(interceptor);
        retrofit = builder.client(httpClient).build();
        return this;
    }

    @Override
    public NuxeoClient setTimeOut(long timeout) {
        httpClient.setConnectTimeout(timeout, TimeUnit.SECONDS);
        retrofit = builder.client(httpClient).build();
        return this;
    }

    @Override
    public String getBaseUrl() {
        return retrofit.baseUrl().toString();
    }

    @Override
    public void shutdown() {

    }
}
