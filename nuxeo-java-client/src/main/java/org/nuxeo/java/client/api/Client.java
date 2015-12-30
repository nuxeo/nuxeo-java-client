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
package org.nuxeo.java.client.api;

import com.squareup.okhttp.Response;
import org.nuxeo.java.client.api.cache.NuxeoResponseCache;

import retrofit.Retrofit;

import com.squareup.okhttp.Interceptor;

/**
 * @since 1.0
 */
public interface Client {

    /**
     * Add header to client requests.
     */
    NuxeoClient header(String header, String value);

    /**
     * Define Custom Cache.
     */
    NuxeoClient setCache(NuxeoResponseCache nuxeoCache);

    /**
     * Set Authentication Method.<br/>
     * Builtin methods:<br/>
     * <ul>
     * <li>{@link org.nuxeo.java.client.internals.spi.auth.BasicAuthInterceptor}</li>
     * <li>{@link org.nuxeo.java.client.internals.spi.auth.PortalSSOAuthInterceptor}</li>
     * <li>{@link org.nuxeo.java.client.internals.spi.auth.TokenAuthInterceptor}</li>
     * </ul>
     */
    NuxeoClient setAuthenticationMethod(Interceptor interceptor);

    /**
     * Define the http call timeout.
     * 
     * @param timeout in milliseconds.
     */
    NuxeoClient timeout(long timeout);

    /**
     * Get base URL.
     */
    String getBaseUrl();

    /**
     * Cleanup any resources held by this client. After a shutdown the client is no more usable.
     */
    void shutdown();

    /**
     * @return retrofit instance.
     */
    Retrofit getRetrofit();

    /**
     * @return Nuxeo cache.
     */
    NuxeoResponseCache getNuxeoCache();

    /**
     * @return Cache Status.
     */
    boolean isCacheEnabled();

    /**
     * GET simple method.
     * @param url should contains parameters if needed.
     * @return okhttp response (response.body().string() -> to get the payload).
     */
    Response get(String url);

    /**
     * DELETE simple method.
     * @param url should contains parameters if needed.
     * @param json - can be null.
     * @return okhttp response.
     */
    Response delete(String url, String json);

    /**
     * PUT simple method.
     * @param url should contains parameters if needed.
     * @param json - can be null.
     * @return okhttp response (response.body().string() -> to get the payload).
     */
    Response put(String url, String json);

    /**
     * POST simple method.
     * @param url should contains parameters if needed.
     * @param json - can be null.
     * @return okhttp response (response.body().string() -> to get the payload).
     */
    Response post(String url, String json);
}
