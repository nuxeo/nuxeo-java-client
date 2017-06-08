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
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Interceptor used to catch document creation/deletion in order to provide a way to clean repository.
 *
 * @since 3.0.0
 */
public class WorkflowInterceptor implements Interceptor {

    private static final String WORKFLOW_ID_DELETION_PATH = "/nuxeo/api/v1/workflow/(.*)";

    private static final String WORKFLOW_PATH_CREATION_PATH = "/nuxeo/api/v1/(repo/.*/)?path/(.*)/@workflow";

    private static final String WORKFLOW_ID_CREATION_PATH = "/nuxeo/api/v1/(repo/.*/)?id/(.*)/@workflow";

    private final Set<String> workflowIdsToDelete = new HashSet<>();

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
            if (httpUrl.encodedPath().matches(WORKFLOW_PATH_CREATION_PATH)
                    || httpUrl.encodedPath().matches(WORKFLOW_ID_CREATION_PATH)) {
                String body = Responses.bodyToString(response);
                String id = body.replaceFirst(".*\"entity-type\":\"workflow\",\"id\":\"([^\"]*)\".*", "$1");
                if (StringUtils.isNotBlank(id)) {
                    workflowIdsToDelete.add(id);
                }
            }
        } else if ("DELETE".equals(method)) {
            if (httpUrl.encodedPath().matches(WORKFLOW_ID_DELETION_PATH)) {
                String id = httpUrl.encodedPath().replaceFirst(WORKFLOW_ID_DELETION_PATH, "$1");
                workflowIdsToDelete.remove(id);
            }
        }

        return response;
    }

    public Set<String> getWorkflowIdsToDelete() {
        return workflowIdsToDelete;
    }

}
