/*
 * (C) Copyright 2018 Nuxeo (http://nuxeo.com/) and others.
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
 *     Kevin Leturc <kleturc@nuxeo.com>
 */
package org.nuxeo.client.spi.auth;

import java.io.IOException;

import org.nuxeo.client.HttpHeaders;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @since 3.2
 */
public class JWTAuthInterceptor implements Interceptor {

    protected String token;

    public JWTAuthInterceptor(String token) {
        this.token = token;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request()
                               .newBuilder()
                               .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                               .build();
        return chain.proceed(request);
    }

}
