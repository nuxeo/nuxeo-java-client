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
package org.nuxeo.client.internals.spi.auth;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import org.nuxeo.client.internals.util.Base64;

import com.google.common.net.HttpHeaders;

/**
 * @since 0.1
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
