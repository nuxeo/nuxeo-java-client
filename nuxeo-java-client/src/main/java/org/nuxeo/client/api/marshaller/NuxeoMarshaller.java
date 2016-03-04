/*
 * (C) Copyright 2016 Nuxeo SA (http://nuxeo.com/) and others.
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
 *         Vladimir Pasquier <vpasquier@nuxeo.com>
 */
package org.nuxeo.client.api.marshaller;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

/**
 * Plugs in automation client new input/output marshalling logic.
 */
public interface NuxeoMarshaller<T> {

    /**
     * The marshalled java type
     */
    Class<T> getJavaType();

    /**
     * Builds and returns a POJO from the JSON object
     */
    T read(JsonParser jp) throws IOException;

    /**
     * Writes the POJO object to the JsonGenerator
     */
    void write(JsonGenerator jg, Object value) throws IOException;

}
