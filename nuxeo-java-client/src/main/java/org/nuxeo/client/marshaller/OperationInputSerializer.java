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

import org.nuxeo.client.objects.blob.Blob;
import org.nuxeo.client.objects.blob.Blobs;
import org.nuxeo.client.objects.operation.DocRef;
import org.nuxeo.client.objects.operation.DocRefs;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * @since 3.0
 */
public class OperationInputSerializer extends StdSerializer<Object> {

    protected OperationInputSerializer() {
        super(Object.class);
    }

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (value instanceof Blob || value instanceof Blobs) {
            // Don't serialize blob(s)
        } else if (value instanceof DocRef) {
            gen.writeString("doc:" + ((DocRef) value).getDoc());
        } else if (value instanceof DocRefs) {
            gen.writeString("docs:" + String.join(",", (DocRefs) value));
        } else {
            gen.writeObject(value);
        }
    }

}
