/*
 * (C) Copyright 2017-2025 Nuxeo (http://nuxeo.com/) and others.
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
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Interceptor used to catch user/group creation/deletion in order to provide a way to clean user manager.
 *
 * @since 3.0.0
 */
public class UserGroupInterceptor implements Interceptor {

    private static final String USER_PATH = "/nuxeo/api/v1/user(?:/([^/]*))?";

    private static final String GROUP_PATH = "/nuxeo/api/v1/group(?:/([^/]*))?";

    private final Set<String> usersToDelete = new HashSet<>();

    private final Set<String> groupsToDelete = new HashSet<>();

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
            if (httpUrl.encodedPath().matches(USER_PATH)) {
                String body = Responses.bodyToString(response);
                String username = body.replaceFirst(".*\"username\":\"([^\"]*)\".*", "$1");
                if (StringUtils.isNotBlank(username)) {
                    usersToDelete.add(username);
                }
            } else if (httpUrl.encodedPath().matches(GROUP_PATH)) {
                String body = Responses.bodyToString(response);
                String groupName = body.replaceFirst(".*\"groupname\":\"([^\"]*)\".*", "$1");
                if (StringUtils.isNotBlank(groupName)) {
                    groupsToDelete.add(groupName);
                }
            }
        } else if ("DELETE".equals(method)) {
            if (httpUrl.encodedPath().matches(USER_PATH)) {
                String username = httpUrl.encodedPath().replaceFirst(USER_PATH, "$1");
                usersToDelete.remove(username);
            } else if (httpUrl.encodedPath().matches(GROUP_PATH)) {
                String groupName = httpUrl.encodedPath().replaceFirst(GROUP_PATH, "$1");
                groupsToDelete.remove(groupName);
            }
        }

        return response;
    }

    public Set<String> getUsersToDelete() {
        return new HashSet<>(usersToDelete);
    }

    public Set<String> getGroupsToDelete() {
        return new HashSet<>(groupsToDelete);
    }

}
