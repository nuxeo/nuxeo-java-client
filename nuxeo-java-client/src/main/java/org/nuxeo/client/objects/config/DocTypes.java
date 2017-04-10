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

import java.util.Map;

import org.nuxeo.client.NuxeoClient;
import org.nuxeo.client.marshaller.DocTypesSchemasDeserializer;
import org.nuxeo.client.objects.Connectable;
import org.nuxeo.client.objects.Entity;
import org.nuxeo.client.objects.EntityTypes;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @since 3.0
 */
@JsonDeserialize(converter = DocTypesConverter.class)
public class DocTypes extends Entity implements Connectable {

    @JsonProperty("doctypes")
    protected Map<String, DocType> docTypes;

    @JsonDeserialize(using = DocTypesSchemasDeserializer.class)
    protected Map<String, Schema> schemas;

    /**
     * For deserialization purpose
     */
    protected DocTypes() {
        super(EntityTypes.DOC_TYPES);
    }

    public Map<String, DocType> getDocTypes() {
        return docTypes;
    }

    public DocType getDocType(String name) {
        return docTypes.get(name);
    }

    public Map<String, Schema> getSchemas() {
        return schemas;
    }

    public Schema getSchema(String name) {
        return schemas.get(name);
    }

    @Override
    public void reconnectWith(NuxeoClient nuxeoClient) {
        docTypes.values().forEach(docType -> docType.reconnectWith(nuxeoClient));
    }

}
