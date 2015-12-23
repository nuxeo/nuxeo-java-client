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
package org.nuxeo.java.client.internals.spi.auth;

import java.io.IOException;

import org.nuxeo.java.client.internals.util.Base64;

import com.google.common.net.HttpHeaders;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

/**
 * @since 1.0
 */
public class BasicAuthInterceptor implements Interceptor {

    protected String token;

    public BasicAuthInterceptor(String username, String password) {
        setAuth(username, password);
    }

    public void setAuth(String username, String password) {
        String info = username + ":" + password;
        token = "Basic " + Base64.encode(info);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request request = chain.request()
                               .newBuilder()
                               .addHeader(HttpHeaders.AUTHORIZATION, token)
                               .addHeader(HttpHeaders.CONTENT_TYPE,
                                       com.google.common.net.MediaType.JSON_UTF_8.toString())
                               .method(original.method(), original.body())
                               .build();
        return chain.proceed(request);
    }
}
