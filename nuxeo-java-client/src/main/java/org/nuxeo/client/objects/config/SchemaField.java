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

import java.io.Serializable;
import java.util.Map;

/**
 * @since 3.0
 */
public class SchemaField implements Serializable {

    protected final String type;

    protected final boolean array;

    protected final Map<String, SchemaField> complexFields;

    public SchemaField(String type) {
        this(type, null);
    }

    public SchemaField(String type, Map<String, SchemaField> complexFields) {
        this.type = type.replace("[]", "");
        this.array = type.endsWith("[]");
        this.complexFields = complexFields;
    }

    public String getNaturalType() {
        return type + (array ? "[]" : "");
    }

    public String getType() {
        return type;
    }

    public boolean isBlob() {
        return "blob".equals(type);
    }

    public boolean isBoolean() {
        return "boolean".equals(type);
    }

    public boolean isComplex() {
        return "complex".equals(type);
    }

    public boolean isDate() {
        return "date".equals(type);
    }

    public boolean isLong() {
        return "long".equals(type);
    }

    public boolean isString() {
        return "string".equals(type);
    }

    public boolean isArray() {
        return array;
    }

    public boolean hasComplex() {
        return complexFields != null;
    }

    public Map<String, SchemaField> getComplexFields() {
        return complexFields;
    }

    public SchemaField getComplexField(String fieldName) {
        return complexFields.get(fieldName);
    }

}
