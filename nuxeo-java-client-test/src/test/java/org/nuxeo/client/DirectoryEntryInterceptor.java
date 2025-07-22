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
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Interceptor used to catch directory entry creation/deletion in order to provide a way to clean directories.
 *
 * @since 4.0.2
 */
public class DirectoryEntryInterceptor implements Interceptor {

    private static final String DIRECTORY_ENTRY_PATH = "/nuxeo/api/v1/directory/([^/]+)(?:/(.*)(?:/@.+)*)?$";

    private final Set<Pair<String, String>> directoryEntryIdsToDelete = new HashSet<>();

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        if (!response.isSuccessful()) {
            // exit if failed
            return response;
        }

        HttpUrl httpUrl = request.url();
        String method = request.method();
        if ("POST".equals(method)) {
            if (httpUrl.encodedPath().matches(DIRECTORY_ENTRY_PATH)) {
                String body = Responses.bodyToString(response);
                String directoryName = body.replaceFirst(".*\"directoryName\":\"([^\"]*)\".*", "$1");
                String id = body.replaceFirst(".*\"id\":\"([^\"]*)\".*", "$1");
                if (StringUtils.isNotBlank(directoryName) && StringUtils.isNotBlank(id)) {
                    directoryEntryIdsToDelete.add(Pair.of(directoryName, id));
                }
            }
        } else if ("DELETE".equals(method)) {
            if (httpUrl.encodedPath().matches(DIRECTORY_ENTRY_PATH)) {
                String path = "/" + String.join("/", httpUrl.pathSegments());
                String directoryName = path.replaceFirst(DIRECTORY_ENTRY_PATH, "$1");
                String entryId = path.replaceFirst(DIRECTORY_ENTRY_PATH, "$2");
                directoryEntryIdsToDelete.remove(Pair.of(directoryName, entryId));
            }
        }

        return response;
    }

    public Collection<Pair<String, String>> getDirectoryEntryIdsToDelete() {
        return new HashSet<>(directoryEntryIdsToDelete);
    }
}
