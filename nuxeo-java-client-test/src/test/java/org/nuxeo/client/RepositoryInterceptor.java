/*
 * (C) Copyright 2017 Nuxeo (http://nuxeo.com/) and others.
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
package org.nuxeo.client;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Interceptor used to catch document creation/deletion in order to provide a way to clean repository.
 *
 * @since 3.0.0
 */
public class RepositoryInterceptor implements Interceptor {

    private static final String REPOSITORY_PATH_PATH = "/nuxeo/api/v1/(repo/.*/)?path/([^/]*)";

    private static final String REPOSITORY_ID_PATH = "/nuxeo/api/v1/(repo/.*/)?id/([^/]*)";

    private final Map<String, String> documentPathsToDelete = new HashMap<>();

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        if (!response.isSuccessful()) {
            // exist if failed
            return response;
        }

        HttpUrl httpUrl = request.url();
        String method = request.method();
        if ("POST".equals(method)) {
            if (httpUrl.encodedPath().matches(REPOSITORY_PATH_PATH)
                    || httpUrl.encodedPath().matches(REPOSITORY_ID_PATH)) {
                String body = Responses.bodyToString(response);
                String id = body.replaceFirst(".*\"uid\":\"([^\"]*)\".*", "$1");
                String docPath = body.replaceFirst(".*\"path\":\"/([^\"]+)\".*", "$1");
                if (StringUtils.isNotBlank(docPath)) {
                    // do we already have to delete one parent?
                    if (documentPathsToDelete.keySet().stream().noneMatch(docPath::startsWith)) {
                        documentPathsToDelete.put(docPath, id);
                    }
                }
            }
        } else if ("DELETE".equals(method)) {
            if (httpUrl.encodedPath().matches(REPOSITORY_PATH_PATH)) {
                String path = httpUrl.encodedPath().replaceFirst(REPOSITORY_PATH_PATH, "$2");
                documentPathsToDelete.remove(path);
            } else if (httpUrl.encodedPath().matches(REPOSITORY_ID_PATH)) {
                String id = httpUrl.encodedPath().replaceFirst(REPOSITORY_PATH_PATH, "$2");
                documentPathsToDelete.values().remove(id);
            }
        }

        return response;
    }

    public Collection<String> getDocumentIdsToDelete() {
        return documentPathsToDelete.values();
    }

}
