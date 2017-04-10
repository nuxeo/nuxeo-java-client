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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.nuxeo.client.objects.config.Schema;
import org.nuxeo.client.objects.config.SchemaField;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Deserializer instance for Jackson.
 *
 * @since 3.0
 */
public class DocTypesSchemasDeserializer extends StdDeserializer<Map<String, Schema>> {

    protected DocTypesSchemasDeserializer() {
        super(Map.class);
    }

    @Override
    public Map<String, Schema> deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        TreeNode node = jp.readValueAsTree();
        return deserializeSchemas((ObjectNode) node);
    }

    protected static Map<String, Schema> deserializeSchemas(ObjectNode fields) {
        Map<String, Schema> complexFields = new HashMap<>();
        Iterator<Entry<String, JsonNode>> iterator = fields.fields();
        while (iterator.hasNext()) {
            Entry<String, JsonNode> entry = iterator.next();
            String name = entry.getKey();
            JsonNode jsonNode = entry.getValue();
            // Get prefix
            JsonNode prefixNode = jsonNode.get("@prefix");
            String prefix = prefixNode != null ? prefixNode.asText() : "";
            // Read fields
            Map<String, SchemaField> schemaFields = SchemaFieldsDeserializer.deserializeFields((ObjectNode) jsonNode,
                    e -> !"@prefix".equals(e.getKey()));
            Schema schema = new Schema(name, prefix, schemaFields);
            complexFields.put(name, schema);
        }
        return complexFields;
    }

}
