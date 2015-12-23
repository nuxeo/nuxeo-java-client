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
}
