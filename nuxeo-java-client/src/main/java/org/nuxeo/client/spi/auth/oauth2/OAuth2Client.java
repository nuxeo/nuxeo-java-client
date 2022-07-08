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
package org.nuxeo.client.spi.auth.oauth2;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static org.nuxeo.client.MediaTypes.APPLICATION_JSON_S;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import org.nuxeo.client.spi.NuxeoClientException;
import org.nuxeo.client.spi.NuxeoClientRemoteException;

import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @since 3.3
 */
public class OAuth2Client {

    public static final String OAUTH_2_TOKEN_ENDPOINT = "oauth2/token";

    public static final String CLIENT_ID = "client_id";

    public static final String CLIENT_SECRET = "client_secret";

    public static final String GRANT_TYPE = "grant_type";

    public static final String GRANT_AUTHORIZATION_CODE = "authorization_code";

    public static final String AUTHENTICATION_CODE = "code";

    public static final String GRAND_JWT_BEARER = "urn:ietf:params:oauth:grant-type:jwt-bearer";

    public static final String ASSERTION = "assertion";

    public static final String GRANT_REFRESH_TOKEN = "refresh_token";

    public static final String REFRESH_TOKEN = "refresh_token";

    protected static final OkHttpClient CLIENT = new OkHttpClient.Builder().build();

    protected static final ObjectMapper MAPPER = new ObjectMapper();

    protected final String baseUrl;

    protected final String clientId;

    protected final String clientSecret;

    public OAuth2Client(String baseUrl, String clientId, String clientSecret) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public OAuth2Token fetchAccessTokenFromAuthenticationCode(String code) {
        // create form body
        FormBody.Builder builder = initFormBodyBuilder(GRANT_AUTHORIZATION_CODE).add(AUTHENTICATION_CODE, code);
        RequestBody formBody = builder.build();
        return executeRequest(formBody);
    }

    public OAuth2Token fetchAccessTokenFromJWT(String jwtToken) {
        // create form body
        FormBody.Builder builder = initFormBodyBuilder(GRAND_JWT_BEARER).add(ASSERTION, jwtToken);
        RequestBody formBody = builder.build();
        return executeRequest(formBody);
    }

    public OAuth2Token refreshToken(OAuth2Token token) {
        // create form body
        FormBody.Builder builder = initFormBodyBuilder(GRANT_REFRESH_TOKEN).add(REFRESH_TOKEN, token.getRefreshToken());
        RequestBody formBody = builder.build();
        return executeRequest(formBody);
    }

    protected FormBody.Builder initFormBodyBuilder(String grantType) {
        FormBody.Builder builder = new FormBody.Builder(UTF_8).add(CLIENT_ID, clientId);
        if (clientSecret != null) {
            builder.add("client_secret", clientSecret);
        }
        return builder.add(GRANT_TYPE, grantType);
    }

    protected OAuth2Token executeRequest(RequestBody body) {
        Request request = new Request.Builder().url(baseUrl + OAUTH_2_TOKEN_ENDPOINT)
                                               .header("Accept", APPLICATION_JSON_S)
                                               .post(body)
                                               .build();
        try (Response response = CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body().string();
                @SuppressWarnings("unchecked")
                Map<String, Serializable> errorMap = MAPPER.readValue(errorBody, Map.class);
                String errorMessage = firstNonNull(errorMap.get("error_description"), errorMap.get("error"),
                        response.message(), "error").toString();
                throw new NuxeoClientRemoteException(response.code(), errorMessage, errorBody, null);
            }
            return MAPPER.readValue(response.body().charStream(), OAuth2Token.class);
        } catch (IOException e) {
            throw new NuxeoClientException("Error during call to Nuxeo server", e);
        }
    }
}
