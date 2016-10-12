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
package org.nuxeo.client.api.methods;

import org.nuxeo.client.api.objects.user.CurrentUser;
import org.nuxeo.client.api.objects.workflow.Workflow;
import org.nuxeo.client.api.objects.workflow.Workflows;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * @since 0.1
 */
public interface CurrentUserAPI {

    @POST("automation/login")
    Call<CurrentUser> getCurrentUser();

    @GET("workflow")
    Call<Workflows> fetchWorkflowInstances();

    @POST("workflow")
    Call<Workflow> startWorkflowInstance(@Body Workflow workflow);

}
