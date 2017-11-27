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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Random;

import org.nuxeo.client.HttpHeaders;
import org.nuxeo.client.util.Base64;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @since 0.1
 */
public class PortalSSOAuthInterceptor implements Interceptor {

    protected final String secret;

    protected final String username;

    public PortalSSOAuthInterceptor(String username, String secret) {
        this.username = username;
        this.secret = secret;
    }

    protected Headers computeHeaders(Headers headers) {
        // compute token
        long ts = new Date().getTime();
        long random = new Random(ts).nextInt();

        String clearToken = String.format("%d:%d:%s:%s", ts, random, secret, username);

        byte[] hashedToken;

        try {
            hashedToken = MessageDigest.getInstance("MD5").digest(clearToken.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Cannot compute token", e);
        }

        String base64HashedToken = Base64.encode(hashedToken);
        return headers.newBuilder()
                      .add(HttpHeaders.NX_TS, String.valueOf(ts))
                      .add(HttpHeaders.NX_RD, String.valueOf(random))
                      .add(HttpHeaders.NX_TOKEN, base64HashedToken)
                      .add(HttpHeaders.NX_USER, username)
                      .build();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request request = chain.request()
                               .newBuilder()
                               .headers(computeHeaders(original.headers()))
                               .method(original.method(), original.body())
                               .build();
        return chain.proceed(request);
    }

}
