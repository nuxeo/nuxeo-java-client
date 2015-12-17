/*
 * (C) Copyright 2015 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *          Nuxeo
 */
package org.nuxeo.java.client.api.marshaller;

import java.io.IOException;

import org.nuxeo.java.client.api.ConstantsV1;
import retrofit.Converter;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

/**
 * @since 1.0
 */
public final class NuxeoRequestConverterFactory<T> implements Converter<T, RequestBody> {

    protected ObjectWriter adapter;

    protected ObjectMapper objectMapper;

    protected NuxeoMarshaller<T> nuxeoMarshaller;

    NuxeoRequestConverterFactory(ObjectWriter adapter) {
        this.adapter = adapter;
    }

    NuxeoRequestConverterFactory(NuxeoMarshaller<T> nuxeoMarshaller, ObjectMapper objectMapper) {
        this.nuxeoMarshaller = nuxeoMarshaller;
        this.objectMapper = objectMapper;
    }

    @Override
    public RequestBody convert(T value) throws IOException {
        ByteArrayBuilder bb = new ByteArrayBuilder();
        byte[] bytes;
        if (nuxeoMarshaller != null) {
            JsonGenerator jg = objectMapper.getFactory().createGenerator(bb, JsonEncoding.UTF8);
            nuxeoMarshaller.write(jg, value);
            bytes = bb.toByteArray();
        } else {
            bytes = adapter.writeValueAsBytes(value);
        }
        return RequestBody.create(ConstantsV1.APPLICATION_JSON_CHARSET_UTF_8, bytes);
    }
}
