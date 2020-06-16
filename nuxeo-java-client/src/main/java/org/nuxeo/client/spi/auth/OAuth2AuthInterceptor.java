/*
 * (C) Copyright 2019 Nuxeo (http://nuxeo.com/) and others.
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
import java.time.Instant;

import org.nuxeo.client.HttpHeaders;
import org.nuxeo.client.spi.auth.oauth2.OAuth2Client;
import org.nuxeo.client.spi.auth.oauth2.OAuth2Token;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @since 3.3
 */
public class OAuth2AuthInterceptor implements Interceptor {

    protected OAuth2Token token;

    // could be null
    protected OAuth2Client oAuth2Client;

    protected OAuth2AuthInterceptor(OAuth2Token token, OAuth2Client oAuth2Client) {
        this.token = token;
        this.oAuth2Client = oAuth2Client;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (needToRefresh()) {
            synchronized (this) {
                if (needToRefresh()) {
                    token = oAuth2Client.refreshToken(token);
                }
            }
        }
        Request request = chain.request()
                               .newBuilder()
                               .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token.getAccessToken())
                               .build();
        return chain.proceed(request);
    }

    protected boolean needToRefresh() {
        return oAuth2Client != null && Instant.now().plusSeconds(60).isAfter(token.getExpiresAt());
    }

    /**
     * @since 3.6
     */
    public static OAuth2AuthInterceptor createAuthFromToken(String token) {
        OAuth2Token oAuthToken = new OAuth2Token();
        oAuthToken.setAccessToken(token);
        return new OAuth2AuthInterceptor(oAuthToken, null);
    }

    public static OAuth2AuthInterceptor obtainAuthFromAuthorizationCode(String baseUrl, String clientId, String code) {
        return obtainAuthFromAuthorizationCode(baseUrl, clientId, null, code);
    }

    public static OAuth2AuthInterceptor obtainAuthFromAuthorizationCode(String baseUrl, String clientId,
            String clientSecret, String code) {
        OAuth2Client oAuth2Client = new OAuth2Client(baseUrl, clientId, clientSecret);
        OAuth2Token token = oAuth2Client.fetchAccessTokenFromAuthenticationCode(code);
        return new OAuth2AuthInterceptor(token, oAuth2Client);
    }

    public static OAuth2AuthInterceptor obtainAuthFromJWTToken(String baseUrl, String clientId, String jwtToken) {
        return obtainAuthFromJWTToken(baseUrl, clientId, null, jwtToken);
    }

    public static OAuth2AuthInterceptor obtainAuthFromJWTToken(String baseUrl, String clientId, String clientSecret,
            String jwtToken) {
        OAuth2Client oAuth2Client = new OAuth2Client(baseUrl, clientId, clientSecret);
        OAuth2Token token = oAuth2Client.fetchAccessTokenFromJWT(jwtToken);
        return new OAuth2AuthInterceptor(token, oAuth2Client);
    }

}
