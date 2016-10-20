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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Random;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import org.nuxeo.client.internals.util.Base64;

/**
 * @since 0.1
 */
public class PortalSSOAuthInterceptor implements Interceptor {

    public static final String NX_USER = "NX_USER";

    public static final String NX_TOKEN = "NX_TOKEN";

    public static final String NX_RD = "NX_RD";

    public static final String NX_TS = "NX_TS";

    protected final String secret;

    protected final String username;

    public PortalSSOAuthInterceptor(String secretKey, String userName) {
        this.secret = secretKey;
        this.username = userName;
    }

    protected Headers computeHeaders() {
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
        Headers headers = new Headers.Builder().add(NX_TS, String.valueOf(ts))
                                               .add(NX_RD, String.valueOf(random))
                                               .add(NX_TOKEN, base64HashedToken)
                                               .add(NX_USER, username)
                                               .build();
        return headers;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request request = chain.request()
                               .newBuilder()
                               .headers(computeHeaders())
                               .method(original.method(), original.body())
                               .build();
        return chain.proceed(request);
    }
}
