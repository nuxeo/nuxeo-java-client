/*
 * (C) Copyright 2016-2018 Nuxeo (http://nuxeo.com/) and others.
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

import static org.nuxeo.client.ConstantsV1.ENTITY_TYPE;
import static org.nuxeo.client.marshaller.NuxeoConverterFactory.JACKSON_ATTRIBUTE_KEY;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.nuxeo.client.MediaType;
import org.nuxeo.client.MediaTypes;
import org.nuxeo.client.objects.blob.Blob;
import org.nuxeo.client.objects.blob.Blobs;
import org.nuxeo.client.objects.blob.FileBlob;
import org.nuxeo.client.objects.blob.StreamBlob;
import org.nuxeo.client.spi.NuxeoClientException;
import org.nuxeo.client.util.IOUtils;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.Headers;
import okhttp3.MultipartReader;
import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * @since 0.1
 */
public final class NuxeoResponseConverter<T> implements Converter<ResponseBody, T> {

    protected final JavaType javaType;

    protected final ObjectMapper objectMapper;

    protected final Map<String, Class<?>> entityTypeToClass;

    protected NuxeoResponseConverter(ObjectMapper objectMapper, JavaType javaType,
            Map<String, Class<?>> entityTypeToClass) {
        this.objectMapper = objectMapper;
        this.javaType = javaType;
        this.entityTypeToClass = entityTypeToClass;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T convert(ResponseBody body) throws IOException {
        MediaType mediaType = MediaType.fromOkHttpMediaType(body.contentType());
        // there's post treatment in NuxeoClient on blob
        if (javaType.getRawClass().equals(StreamBlob.class)) {
            return (T) new StreamBlob(body.byteStream(), null, mediaType.toString());
        }
        // Checking if multipart outputs.
        if (!MediaTypes.APPLICATION_JSON.equalsTypeSubTypeWithoutSuffix(mediaType)) {
            if (mediaType.type().equals(MediaTypes.MULTIPART_S)) {
                List<Blob> blobs = new ArrayList<>();
                try (MultipartReader reader = new MultipartReader(body)) {
                    MultipartReader.Part part;
                    while ((part = reader.nextPart()) != null) {
                        Headers headers = part.headers();
                        String contentDisposition = headers.get("Content-Disposition");
                        String contentType = headers.get("Content-Type");
                        String filename = Stream.of(contentDisposition.split("; ?"))
                                                .filter(s -> s.startsWith("filename"))
                                                .map(s -> s.replaceFirst("filename=\"?([^\"]*)\"?", "$1"))
                                                .findFirst()
                                                .orElse(null);
                        // IOUtils.copyToTempFile close the input stream for us
                        blobs.add(
                                new FileBlob(IOUtils.copyToTempFile(part.body().inputStream()), filename, contentType));
                    }
                    return (T) new Blobs(blobs);
                }
            }
            // automation case
            else {
                return (T) new StreamBlob(body.byteStream(), null, mediaType.toString());
            }
        }
        try (Reader reader = body.charStream()) {
            // Checking the type of the method client side
            if (javaType.getRawClass().equals(Object.class)) {
                // When it's Object we look for entity-type in header and then in payload to deduce java type, in this
                // case, convert the reader to a JsonNode
                // This mechanism is used for Operation and Document Adapter
                JsonNode payload = objectMapper.readTree(reader);
                if (payload.path(ENTITY_TYPE).isTextual()) {
                    String entityType = payload.get(ENTITY_TYPE).textValue();
                    Class<?> entityClass = entityTypeToClass.get(entityType);
                    if (entityClass != null) {
                        return objectMapper.readerFor(entityClass)
                                           .withAttribute(JACKSON_ATTRIBUTE_KEY, entityTypeToClass)
                                           .readValue(payload);
                    }
                }
                // If we can't find an appropriate class to map response just return the plain text
                return (T) objectMapper.writeValueAsString(payload);
            } else {
                // Delegate other cases to jackson
                return objectMapper.readerFor(javaType)
                                   .withAttribute(JACKSON_ATTRIBUTE_KEY, entityTypeToClass)
                                   .readValue(reader);
            }
        } catch (IOException reason) {
            throw new NuxeoClientException("Error during deserialization of HTTP body", reason);
        }
    }

}
