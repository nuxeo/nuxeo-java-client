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

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

/**
 * @since 1.0
 */
public class TokenAuthInterceptor implements Interceptor {

    protected static final String TOKEN_HEADER = "X-Authentication-Token";

    protected String token;

    public TokenAuthInterceptor(String token) {
        this.token = token;
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request original = chain.request();
        Request request = chain.request()
                               .newBuilder()
                               .method(original.method(), original.body())
                               .addHeader(TOKEN_HEADER, token)
                               .build();
        return chain.proceed(request);
    }
}
