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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Random;

import org.nuxeo.java.client.internals.util.Base64;

import com.squareup.okhttp.Headers;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

/**
 * @since 1.0
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
