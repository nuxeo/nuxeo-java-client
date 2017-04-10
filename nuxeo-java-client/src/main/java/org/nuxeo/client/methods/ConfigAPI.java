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
package org.nuxeo.client.methods;

import java.util.List;

import org.nuxeo.client.objects.config.DocType;
import org.nuxeo.client.objects.config.DocTypes;
import org.nuxeo.client.objects.config.Facet;
import org.nuxeo.client.objects.config.Schema;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * @since 3.0
 */
public interface ConfigAPI {

    @GET("config/types")
    Call<DocTypes> types();

    @GET("config/types/{type}")
    Call<DocType> type(@Path("type") String type);

    @GET("config/schemas")
    Call<List<Schema>> schemas();

    @GET("config/schemas/{schema}")
    Call<Schema> schema(@Path("schema") String schema);

    @GET("config/facets")
    Call<List<Facet>> facets();

    @GET("config/facets/{facet}")
    Call<Facet> facet(@Path("facet") String facet);

}
