/*
 * (C) Copyright 2016-2017 Nuxeo (http://nuxeo.com/) and others.
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
 *     Vladimir Pasquier <vpasquier@nuxeo.com>
 *     Kevin Leturc <kleturc@nuxeo.com>
 */
package org.nuxeo.client.spi.auth;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import org.nuxeo.client.HttpHeaders;
import org.nuxeo.client.MediaTypes;
import org.nuxeo.client.spi.NuxeoClientException;
import org.nuxeo.client.util.Base64;

/**
 * @since 0.1
 */
public class BasicAuthInterceptor implements Interceptor {

    protected String token;

    public BasicAuthInterceptor(String username, String password) {
        if (username == null || password == null) {
            throw new NuxeoClientException("'username' and 'password' must be set");
        }
        String info = username + ":" + password;
        token = "Basic " + Base64.encode(info);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request request = chain.request()
                               .newBuilder()
                               .addHeader(HttpHeaders.AUTHORIZATION, token)
                               .addHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.APPLICATION_JSON_CHARSET_UTF_8_S)
                               .method(original.method(), original.body())
                               .build();
        return chain.proceed(request);
    }

}
