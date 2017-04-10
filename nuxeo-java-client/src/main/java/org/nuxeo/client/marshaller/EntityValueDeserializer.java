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

import org.nuxeo.client.ConstantsV1;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ValueNode;

/**
 * Deserializer instance for Jackson.
 * <p />
 * This is useful to have proper document instances in context parameter when requesting breadcrumb enricher.
 *
 * @since 3.0
 */
public class EntityValueDeserializer extends StdDeserializer<Object> {

    protected EntityValueDeserializer() {
        super(Object.class);
    }

    @Override
    public Object deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        TreeNode node = jp.readValueAsTree();
        Class<?> concreteType = determineConcreteType(node);
        return jp.getCodec().treeToValue(node, concreteType);
    }

    protected Class<?> determineConcreteType(TreeNode node) {
        TreeNode entityNode = node.get(ConstantsV1.ENTITY_TYPE);
        if (entityNode != null && entityNode.isValueNode()) {
            String entityType = ((ValueNode) entityNode).asText();
            Class<?> klazz = NuxeoResponseConverterFactory.entityTypeToClass.get(entityType);
            if (klazz != null) {
                return klazz;
            }
        }
        return Object.class;
    }

}
