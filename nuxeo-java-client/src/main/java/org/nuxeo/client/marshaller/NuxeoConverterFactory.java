/*
 * (C) Copyright 2016-2017 Nuxeo (http://nuxeo.com/) and others.
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
 *     Vladimir Pasquier <vpasquier@nuxeo.com>
 *     Kevin Leturc <kleturc@nuxeo.com>
 */
package org.nuxeo.client.marshaller;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.nuxeo.client.spi.NuxeoClientException;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * @since 0.1
 */
public class NuxeoConverterFactory extends Converter.Factory {

    protected static final Map<String, Class<?>> entityTypeToClass = new HashMap<>();

    protected final ObjectMapper mapper;

    protected NuxeoConverterFactory(ObjectMapper mapper) {
        this.mapper = Objects.requireNonNull(mapper, "mapper == null");
    }

    public static NuxeoConverterFactory create() {
        // TODO JAVACLIENT-21
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return create(mapper);
    }

    public static NuxeoConverterFactory create(ObjectMapper mapper) {
        return new NuxeoConverterFactory(mapper);
    }

    /**
     * Register an entity pojo to the operation unmarshalling mechanism.
     *
     * @since 3.0
     */
    public static void registerEntity(String entityType, Class<?> clazz) {
        entityTypeToClass.put(entityType, clazz);
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit client) {
        JavaType javaType = mapper.getTypeFactory().constructType(type);
        return new NuxeoResponseConverterFactory<>(mapper, javaType, entityTypeToClass);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations,
            Annotation[] methodAnnotations, Retrofit retrofit) {
        JavaType javaType = mapper.getTypeFactory().constructType(type);
        return new NuxeoRequestConverterFactory<>(mapper, javaType);
    }

    /**
     * @param json the json to unmarshall
     * @param javaType the pojo to use for unmarshalling
     * @return the pojo generated from the json
     */
    public <T> T readJSON(String json, Class<T> javaType) {
        try {
            return mapper.readValue(json, javaType);
        } catch (IOException reason) {
            throw new NuxeoClientException("Converter Read Issue. See NuxeoConverterFactory#readJSON", reason);
        }
    }

    /**
     * @since 2.5
     * @param json the json to unmarshall
     * @param javaTypeContainer the collection
     * @param javaType the pojo to use for unmarshalling
     * @return the collection of pojos generated from the json
     */
    public <T> T readJSON(String json, Class javaTypeContainer, Class javaType) {
        try {
            JavaType type = mapper.getTypeFactory().constructCollectionLikeType(javaTypeContainer, javaType);
            return mapper.readValue(json, type);
        } catch (IOException reason) {
            throw new NuxeoClientException("Converter Read Issue.", reason);
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
