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
import java.util.Set;

import org.nuxeo.client.marshaller.DocTypeSchemaDeserializer;
import org.nuxeo.client.methods.ConfigAPI;
import org.nuxeo.client.objects.ConnectableEntity;
import org.nuxeo.client.objects.EntityTypes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @since 3.0
 */
public class DocType extends ConnectableEntity<ConfigAPI, DocType> {

    protected String name;

    protected String parent;

    protected Set<String> facets;

    @JsonDeserialize(using = DocTypeSchemaDeserializer.class)
    protected List<Schema> schemas;

    /** Services attributes **/

    @JsonIgnore
    protected DocType parentType;

    /**
     * For deserialization purpose
     */
    protected DocType() {
        super(EntityTypes.DOC_TYPE, ConfigAPI.class);
    }

    public String getName() {
        return name;
    }

    public String getParent() {
        return parent;
    }

    public Set<String> getFacets() {
        return facets;
    }

    public boolean hasFacet(String facet) {
        return facets.contains(facet);
    }

    public List<Schema> getSchemas() {
        return schemas;
    }

    public Schema getSchema(String name) {
        return schemas.stream().filter(schema -> schema.getName().equals(name)).findAny().orElse(null);
    }

    // ************
    // * Services *
    // ************

    /**
     * @return the parent doc type if exist, unless fetch it from server and store it in this object
     */
    public DocType fetchParent() {
        if (parentType == null) {
            parentType = fetchResponse(api.type(parent));
        }
        return parentType;
    }

}
