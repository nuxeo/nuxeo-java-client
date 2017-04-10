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

import org.nuxeo.client.marshaller.SchemaFieldsDeserializer;
import org.nuxeo.client.objects.Entity;
import org.nuxeo.client.objects.EntityTypes;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @since 3.0
 */
public class Schema extends Entity {

    protected String name;

    @JsonProperty("@prefix")
    protected String prefix;

    @JsonDeserialize(using = SchemaFieldsDeserializer.class)
    protected Map<String, SchemaField> fields;

    /**
     * For deserialization purpose
     */
    protected Schema() {
        super(EntityTypes.SCHEMA);
    }

    public Schema(String name, String prefix, Map<String, SchemaField> fields) {
        this();
        this.name = name;
        this.prefix = prefix;
        this.fields = fields;
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public Map<String, SchemaField> getFields() {
        return fields;
    }

    public SchemaField getField(String fieldName) {
        return fields.get(fieldName);
    }

}
