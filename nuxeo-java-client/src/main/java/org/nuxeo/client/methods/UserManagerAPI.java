/*
 * (C) Copyright 2016-2025 Nuxeo (http://nuxeo.com/) and others.
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
 *     Vladimir Pasquier <vpasquier@nuxeo.com>
 *     Kevin Leturc <kleturc@nuxeo.com>
 */
package org.nuxeo.client.methods;

import org.nuxeo.client.objects.user.Group;
import org.nuxeo.client.objects.user.Groups;
import org.nuxeo.client.objects.user.User;
import org.nuxeo.client.objects.user.Users;
import org.nuxeo.client.objects.workflow.Workflow;
import org.nuxeo.client.objects.workflow.Workflows;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * @since 0.1
 */
public interface UserManagerAPI {

    // ----------
    // Group APIs
    // ----------

    @GET("group/{idOrGroupname}")
    Call<Group> fetchGroup(@Path("idOrGroupname") String idOrGroupname);

    @PUT("group/{idOrGroupname}")
    Call<Group> updateGroup(@Path("idOrGroupname") String idOrGroupname, @Body Group group);

    @DELETE("group/{idOrGroupname}")
    Call<ResponseBody> deleteGroup(@Path("idOrGroupname") String idOrGroupname);

    @POST("group")
    Call<Group> createGroup(@Body Group group);

    @GET("group/search")
    Call<Groups> searchGroup(@Query("q") String query);

    @GET("group/search")
    Call<Groups> searchGroup(@Query("q") String query, @Query("currentPageIndex") int currentPageIndex,
            @Query("pageSize") int pageSize);

    @POST("group/{idOrGroupname}/user/{idOrUsername}")
    Call<Group> attachGroupToUser(@Path("idOrGroupname") String idOrGroupname,
            @Path("idOrUsername") String idOrUsername);

    /**
     * @since 3.0
     */
    @GET("group/{idOrGroupname}/@users")
    Call<Users> fetchGroupMemberUsers(@Path("idOrGroupname") String idOrGroupname);

    /**
     * @since 3.11.0
     */
    @GET("group/{idOrGroupname}/@users")
    Call<Users> fetchGroupMemberUsers(@Path("idOrGroupname") String idOrGroupname,
            @Query("currentPageIndex") int currentPageIndex, @Query("pageSize") int pageSize);

    /**
     * @since 3.0
     */
    @GET("group/{idOrGroupname}/@groups")
    Call<Groups> fetchGroupMemberGroups(@Path("idOrGroupname") String idOrGroupname);

    /**
     * @since 3.11.0
     */
    @GET("group/{idOrGroupname}/@groups")
    Call<Groups> fetchGroupMemberGroups(@Path("idOrGroupname") String idOrGroupname,
            @Query("currentPageIndex") int currentPageIndex, @Query("pageSize") int pageSize);

    // ---------
    // User APIs
    // ---------

    @GET("user/{idOrUsername}")
    Call<User> fetchUser(@Path("idOrUsername") String idOrUsername);

    @PUT("user/{idOrUsername}")
    Call<User> updateUser(@Path("idOrUsername") String idOrUsername, @Body User user);

    @DELETE("user/{idOrUsername}")
    Call<Void> deleteUser(@Path("idOrUsername") String idOrUsername);

    @POST("user")
    Call<User> createUser(@Body User user);

    @GET("user/search")
    Call<Users> searchUser(@Query("q") String query);

    @GET("user/search")
    Call<Users> searchUser(@Query("q") String query, @Query("currentPageIndex") int currentPageIndex,
            @Query("pageSize") int pageSize);

    @POST("user/{idOrUsername}/group/{idOrGroupname}")
    Call<User> addUserToGroup(@Path("idOrUsername") String idOrUsername, @Path("idOrGroupname") String idOrGroupname);

    // -----------------
    // Current user APIs
    // -----------------

    @POST("automation/login")
    Call<User> fetchCurrentUser();

    @GET("workflow")
    Call<Workflows> fetchWorkflowInstances();

    @POST("workflow")
    Call<Workflow> startWorkflowInstance(@Body Workflow workflow);

}
