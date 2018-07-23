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
import java.util.function.Predicate;

import org.nuxeo.client.objects.config.SchemaField;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;

/**
 * Deserializer instance for Jackson.
 *
 * @since 3.0
 */
public class SchemaFieldsDeserializer extends StdDeserializer<Map<String, SchemaField>> {

    protected SchemaFieldsDeserializer() {
        super(Map.class);
    }

    @Override
    public Map<String, SchemaField> deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        TreeNode node = jp.readValueAsTree();
        return deserializeFields((ObjectNode) node);
    }

    protected static Map<String, SchemaField> deserializeFields(ObjectNode fields) {
        return deserializeFields(fields, entry -> true);
    }

    protected static Map<String, SchemaField> deserializeFields(ObjectNode fields,
            Predicate<Entry<String, JsonNode>> filter) {
        Map<String, SchemaField> complexFields = new HashMap<>();
        Iterator<Entry<String, JsonNode>> iterator = fields.fields();
        while (iterator.hasNext()) {
            Entry<String, JsonNode> entry = iterator.next();
            if (!filter.test(entry)) {
                continue;
            }
            String key = entry.getKey();
            JsonNode jsonNode = entry.getValue();
            SchemaField complexField;
            if (jsonNode instanceof ValueNode) {
                String type = jsonNode.asText();
                complexField = new SchemaField(type);
            } else {
                ObjectNode objectNode = (ObjectNode) jsonNode;
                // type should be equals to complex or complex[] in this case
                String type = objectNode.get("type").asText();
                Map<String, SchemaField> subComplexFields = deserializeFields((ObjectNode) objectNode.get("fields"));
                complexField = new SchemaField(type, subComplexFields);
            }
            complexFields.put(key, complexField);
        }
        return complexFields;
    }

}
