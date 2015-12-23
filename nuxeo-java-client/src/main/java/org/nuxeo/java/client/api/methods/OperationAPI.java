/*
 * (C) Copyright 2016 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *          Nuxeo
 */
package org.nuxeo.java.client.api.methods;

import com.squareup.okhttp.ResponseBody;
import org.nuxeo.java.client.api.objects.Operation;
import org.nuxeo.java.client.api.objects.OperationBody;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * @since 1.0
 */
public interface OperationAPI {

    @POST("automation/{operationId}")
    Call<ResponseBody> execute(@Path("operationId") String operationId, @Body OperationBody body);

    @GET("automation/{operationId}")
    Call<Operation> getOperation(@Path("operationId") String operationId);

}
