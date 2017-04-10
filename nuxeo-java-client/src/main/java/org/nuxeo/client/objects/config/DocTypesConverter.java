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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.util.StdConverter;

/**
 * This converter is useful to bind schemas in each docTypes and fix their names. As the server doesn't use regular
 * marshaller for the /types endpoint.
 *
 * @since 3.0
 */
public class DocTypesConverter extends StdConverter<DocTypes, DocTypes> {

    @Override
    public DocTypes convert(DocTypes value) {
        Map<String, Schema> schemas = value.getSchemas();
        for (Entry<String, DocType> entry : value.getDocTypes().entrySet()) {
            String name = entry.getKey();
            DocType docType = entry.getValue();
            // Fix name
            docType.name = name;
            // Bind schemas
            List<Schema> docTypeSchemas = new ArrayList<>(docType.schemas.size());
            for (Schema schema : docType.schemas) {
                docTypeSchemas.add(schemas.get(schema.getName()));
            }
            // Replace schema
            docType.schemas = docTypeSchemas;
        }
        return value;
    }

}
