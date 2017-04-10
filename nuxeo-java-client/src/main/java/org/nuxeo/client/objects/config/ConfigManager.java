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
package org.nuxeo.client.objects.config;

import java.util.List;

import org.nuxeo.client.NuxeoClient;
import org.nuxeo.client.methods.ConfigAPI;
import org.nuxeo.client.objects.AbstractConnectable;

/**
 * @since 3.0
 */
public class ConfigManager extends AbstractConnectable<ConfigAPI> {

    public ConfigManager(NuxeoClient nuxeoClient) {
        super(ConfigAPI.class, nuxeoClient);
    }

    public DocTypes fetchTypes() {
        return fetchResponse(api.types());
    }

    public DocType fetchType(String type) {
        return fetchResponse(api.type(type));
    }

    public List<Schema> fetchSchemas() {
        return fetchResponse(api.schemas());
    }

    public Schema fetchSchema(String schema) {
        return fetchResponse(api.schema(schema));
    }

    public List<Facet> fetchFacets() {
        return fetchResponse(api.facets());
    }

    public Facet fetchFacet(String facet) {
        return fetchResponse(api.facet(facet));
    }

}
