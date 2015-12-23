/*
 * (C) Copyright 2016 Nuxeo SA (http://nuxeo.com/) and contributors.
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
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.nuxeo.java.client.internals.spi.NuxeoClientException;

import retrofit.Converter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;

/**
 * @since 1.0
 */
public class NuxeoConverterFactory extends Converter.Factory {

    protected static final Map<Class<?>, NuxeoMarshaller<?>> marshallers = new ConcurrentHashMap<>();

    protected final ObjectMapper mapper;

    public static NuxeoConverterFactory create() {
        // TODO JAVACLIENT-21
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return create(objectMapper);
    }

    public static NuxeoConverterFactory create(ObjectMapper mapper) {
        return new NuxeoConverterFactory(mapper);
    }

    protected NuxeoConverterFactory(ObjectMapper mapper) {
        if (mapper == null)
            throw new NullPointerException("mapper == null");
        this.mapper = mapper;
    }

    @Override
    public Converter<ResponseBody, ?> fromResponseBody(Type type, Annotation[] annotations) {
        JavaType javaType = mapper.getTypeFactory().constructType(type);
        NuxeoMarshaller<?> nuxeoMarshaller = marshallers.get(javaType.getRawClass());
        if (nuxeoMarshaller != null) {
            return new NuxeoResponseConverterFactory<>(nuxeoMarshaller, mapper);
        }
        ObjectReader reader = mapper.readerFor(javaType);
        return new NuxeoResponseConverterFactory<>(reader);
    }

    @Override
    public Converter<?, RequestBody> toRequestBody(Type type, Annotation[] annotations) {
        JavaType javaType = mapper.getTypeFactory().constructType(type);
        NuxeoMarshaller<?> nuxeoMarshaller = marshallers.get(javaType.getRawClass());
        if (nuxeoMarshaller != null) {
            return new NuxeoRequestConverterFactory<>(nuxeoMarshaller, mapper);
        }
        ObjectWriter writer = mapper.writerFor(javaType);
        return new NuxeoRequestConverterFactory<>(writer);
    }

    public void registerMarshaller(NuxeoMarshaller<?> marshaller) {
        marshallers.put(marshaller.getJavaType(), marshaller);
    }

    public Object readJSON(String json, Class javaType) {
        try {
            return mapper.readValue(json, javaType);
        } catch (IOException reason) {
            throw new NuxeoClientException("Converter Read Issue. See NuxeoConverterFactory#readJSON", reason);
        }
    }

    public String writeJSON(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (IOException reason) {
            throw new NuxeoClientException("Converter Write Issue. See NuxeoConverterFactory#writeJSON", reason);
        }
    }
}
