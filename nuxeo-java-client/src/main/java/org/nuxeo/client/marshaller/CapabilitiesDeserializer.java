/*
 * (C) Copyright 2024 Nuxeo (http://nuxeo.com/) and others.
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
 *     Kevin Leturc <kevin.leturc@hyland.com>
 */
package org.nuxeo.client.marshaller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.nuxeo.client.objects.capabilities.Capabilities;
import org.nuxeo.client.objects.capabilities.Capability;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @since 4.0.0
 */
public class CapabilitiesDeserializer extends StdDeserializer<Capabilities> {

    protected CapabilitiesDeserializer() {
        super(Capabilities.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Capabilities deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        Map<String, Capability> capabilities = new HashMap<>();
        ObjectNode entity = p.readValueAsTree();
        for (Iterator<Map.Entry<String, JsonNode>> it = entity.fields(); it.hasNext();) {
            Map.Entry<String, JsonNode> fieldEntry = it.next();
            if (!"entity-type".equals(fieldEntry.getKey())) {
                Map<String, Object> subCapabilities = ctxt.readTreeAsValue(fieldEntry.getValue(), Map.class);
                capabilities.put(fieldEntry.getKey(), new Capability(subCapabilities));
            }
        }
        return new Capabilities(capabilities);
    }
}
