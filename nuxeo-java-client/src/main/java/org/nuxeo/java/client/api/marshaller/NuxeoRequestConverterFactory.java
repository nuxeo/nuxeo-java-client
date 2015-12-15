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

import retrofit.Converter;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

/**
 * @since 1.0
 */
public final class NuxeoRequestConverterFactory<T> implements Converter<T, RequestBody> {

    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");

    protected ObjectWriter adapter;

    protected JsonFactory jsonFactory;

    protected NuxeoMarshaller<T> nuxeoMarshaller;

    NuxeoRequestConverterFactory(ObjectWriter adapter) {
        this.adapter = adapter;
    }

    NuxeoRequestConverterFactory(NuxeoMarshaller<T> nuxeoMarshaller, JsonFactory jsonFactory) {
        this.nuxeoMarshaller = nuxeoMarshaller;
        this.jsonFactory = jsonFactory;
    }

    @Override
    public RequestBody convert(T value) throws IOException {
        ByteArrayBuilder bb = new ByteArrayBuilder();
        byte[] bytes;
        if (nuxeoMarshaller != null) {
            JsonGenerator jg = jsonFactory.createGenerator(bb, JsonEncoding.UTF8);
            nuxeoMarshaller.write(jg, value);
            bytes = bb.toByteArray();
        } else {
            bytes = adapter.writeValueAsBytes(value);
        }
        return RequestBody.create(MEDIA_TYPE, bytes);
    }
}
