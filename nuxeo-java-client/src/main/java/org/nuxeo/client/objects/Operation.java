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
package org.nuxeo.client.objects;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.nuxeo.client.NuxeoClient;
import org.nuxeo.client.methods.OperationAPI;
import org.nuxeo.client.objects.blob.Blob;
import org.nuxeo.client.objects.blob.Blobs;
import org.nuxeo.client.objects.operation.OperationBody;

import com.fasterxml.jackson.annotation.JsonIgnore;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.MultipartBody.Part;
import okhttp3.RequestBody;
import okhttp3.internal.Util;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * @since 0.1
 */
public class Operation extends ConnectableEntity<OperationAPI> {

    public static final String INPUT_PART = "input";

    public static final String INPUT_PARTS = "input#";

    @JsonIgnore
    protected OperationBody body;

    protected String operationId;

    /**
     * For deserialization purpose
     */
    protected Operation() {
        super(EntityTypes.OPERATION, OperationAPI.class);
    }

    public Operation(NuxeoClient nuxeoClient, String operationId) {
        super(EntityTypes.OPERATION, OperationAPI.class, nuxeoClient);
        this.body = new OperationBody();
        this.operationId = operationId;
    }

    public Operation input(Object input) {
        body.setInput(input);
        return this;
    }

    public Operation param(String key, Object parameter) {
        body.getParameters().put(key, parameter);
        return this;
    }

    public Operation context(String key, Object contextValue) {
        body.getContext().put(key, contextValue);
        return this;
    }

    public Operation parameters(Map<String, Object> parameters) {
        body.setParameters(parameters);
        return this;
    }

    public Operation context(Map<String, Object> context) {
        body.setContext(context);
        return this;
    }

    public OperationBody getBody() {
        return body;
    }

    public String getOperationId() {
        return operationId;
    }

    /** Operation Execution Methods Sync **/

    @SuppressWarnings("unchecked")
    public <T> T execute() {
        return (T) fetchResponse(getCallToExecute());
    }

    @SuppressWarnings("unchecked")
    public void execute(Callback<?> callback) {
        fetchResponse(getCallToExecute(), (Callback<Object>) callback);
    }

    protected Call<Object> getCallToExecute() {
        Object input = body.getInput();
        if (input instanceof Blob) { // If input is blob or blobs -> use multipart
            Blob blob = (Blob) input;
            RequestBody fbody = create(blob);
            Part formData = Part.createFormData(INPUT_PART, blob.getFilename(), fbody);
            return api.execute(operationId, body, Collections.singletonList(formData));
        } else if (input instanceof Blobs) { // If input is blob or blobs -> use multipart
            List<Blob> blobs = ((Blobs) input).getBlobs();
            List<MultipartBody.Part> fileParts = new ArrayList<>();
            for (int i = 0; i < blobs.size(); i++) {
                Blob blob = blobs.get(i);
                RequestBody fbody = create(blob);
                fileParts.add(MultipartBody.Part.createFormData(INPUT_PARTS + String.valueOf(i), blob.getFilename(),
                        fbody));
            }
            return api.execute(operationId, body, fileParts);
        } else {
            return api.execute(operationId, body);
        }
    }

    /**
     * Returns a new request body that transmits the content of {@link InputStream}.
     */
    public static RequestBody create(Blob blob) {
        if (blob == null) {
            throw new NullPointerException("content == null");
        }

        return new RequestBody() {

            @Override
            public MediaType contentType() {
                return MediaType.parse(blob.getMimeType());
            }

            @Override
            public long contentLength() throws IOException {
                return blob.getLength();
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                Source source = null;
                try {
                    source = Okio.source(blob.getStream());
                    sink.writeAll(source);
                } finally {
                    Util.closeQuietly(source);
                }
            }

        };
    }

}
