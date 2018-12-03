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

import org.nuxeo.client.objects.Entity;
import org.nuxeo.client.objects.EntityTypes;

/**
 * @since 3.0
 */
public class Facet extends Entity {

    protected String name;

    protected List<Schema> schemas; // NOSONAR

    public Facet() {
        super(EntityTypes.FACET);
    }

    public String getName() {
        return name;
    }

    public List<Schema> getSchemas() {
        return schemas;
    }

    public Schema getSchema(String name) {
        return schemas.stream().filter(schema -> schema.getName().equals(name)).findAny().orElse(null);
    }

}
