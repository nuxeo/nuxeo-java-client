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

import org.nuxeo.client.api.objects.task.Task;
import org.nuxeo.client.api.objects.task.TaskCompletionRequest;
import org.nuxeo.client.api.objects.task.Tasks;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * @since 1.0
 */
public interface TaskManagerAPI {

    @GET("task")
    Call<Tasks> fetchTasks(@Query("userId") String userId, @Query("workflowInstanceId") String workflowInstanceId,
            @Query("workflowModelName") String workflowModelName);

    @GET("task/{taskId}")
    Call<Task> fetchTask(@Path("taskId") String taskId);

    @PUT("task/{taskId}/reassign")
    Call<Task> reassign(@Path("taskId") String taskId, @Query("actors") String actors, @Query("comment") String comment);

    @PUT("task/{taskId}/delegate")
    Call<Task> delegate(@Path("taskId") String taskId, @Query("actors") String actors, @Query("comment") String comment);

    @PUT("task/{taskId}/{action}")
    Call<Task> complete(@Path("taskId") String taskId, @Query("action") String action, @Body TaskCompletionRequest taskCompletionRequest);

}
