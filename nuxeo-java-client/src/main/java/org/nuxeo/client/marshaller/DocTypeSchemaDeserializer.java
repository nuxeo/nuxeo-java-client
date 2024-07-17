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
package org.nuxeo.client.marshaller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.nuxeo.client.objects.config.Schema;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * Deserializer instance for Jackson.
 * <p />
 * We need this deserializer because /types endpoint return array of string for schemas whereas /types/{types} endpoint
 * returns the full schema definition.
 *
 * @since 3.0
 */
public class DocTypeSchemaDeserializer extends StdDeserializer<List<Schema>> {

    protected DocTypeSchemaDeserializer() {
        super(List.class);
    }

    @Override
    public List<Schema> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ArrayNode schemas = p.readValueAsTree();
        if (schemas.isEmpty()) {
            return List.of();
        } else if (schemas.get(0).isTextual()) {
            List<Schema> objSchemas = new ArrayList<>(schemas.size());
            Iterator<JsonNode> schemasIt = schemas.elements();
            while (schemasIt.hasNext()) {
                JsonNode schema = schemasIt.next();
                objSchemas.add(new Schema(schema.textValue(), null, null));
            }
            return objSchemas;
        } else {
            List<Schema> objSchemas = new ArrayList<>(schemas.size());
            ObjectCodec codec = p.getCodec();
            Iterator<JsonNode> schemasIt = schemas.elements();
            while (schemasIt.hasNext()) {
                JsonNode schema = schemasIt.next();
                objSchemas.add(codec.treeToValue(schema, Schema.class));
            }
            return objSchemas;
        }
    }

}
