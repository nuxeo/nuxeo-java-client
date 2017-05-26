/*
 * (C) Copyright 2016-2017 Nuxeo (http://nuxeo.com/) and others.
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
 *         Kevin Leturc <kleturc@nuxeo.com>
 */
package org.nuxeo.client.api.methods;

import org.nuxeo.client.api.objects.user.Group;
import org.nuxeo.client.api.objects.user.Groups;
import org.nuxeo.client.api.objects.user.User;
import org.nuxeo.client.api.objects.user.Users;

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

    @GET("group/{groupName}")
    Call<Group> fetchGroup(@Path("groupName") String groupName);

    @PUT("group/{groupName}")
    Call<Group> updateGroup(@Path("groupName") String groupName, @Body Group group);

    @DELETE("group/{groupName}")
    Call<ResponseBody> deleteGroup(@Path("groupName") String groupName);

    @POST("group")
    Call<Group> createGroup(@Body Group group);

    @GET("group/search")
    Call<Groups> searchGroup(@Query("q") String query);

    @GET("group/search")
    Call<Groups> searchGroup(@Query("q") String query, @Query("currentPageIndex") int currentPageIndex,
            @Query("pageSize") int pageSize);

    @POST("group/{groupName}/user/{userName}")
    Call<User> addUserToGroup(@Path("groupName") String groupName, @Path("userName") String userName);

    @GET("user/{userName}")
    Call<User> fetchUser(@Path("userName") String userName);

    @PUT("user/{userName}")
    Call<User> updateUser(@Path("userName") String userName, @Body User user);

    @DELETE("user/{userName}")
    Call<ResponseBody> deleteUser(@Path("userName") String userName);

    @POST("user")
    Call<User> createUser(@Body User user);

    @GET("user/search")
    Call<Users> searchUser(@Query("q") String query);

    @GET("user/search")
    Call<Users> searchUser(@Query("q") String query, @Query("currentPageIndex") int currentPageIndex,
            @Query("pageSize") int pageSize);

    @POST("user/{userName}/group/{groupName}")
    Call<User> attachGroupToUser(@Path("userName") String userName, @Path("groupName") String groupName);
}
