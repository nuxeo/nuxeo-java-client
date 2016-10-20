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
package org.nuxeo.client.api.objects;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;

import org.nuxeo.client.api.ConstantsV1;
import org.nuxeo.client.api.NuxeoClient;
import org.nuxeo.client.api.methods.OperationAPI;
import org.nuxeo.client.api.objects.blob.Blob;
import org.nuxeo.client.api.objects.blob.Blobs;
import org.nuxeo.client.api.objects.operation.OperationBody;

import retrofit2.Callback;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @since 0.1
 */
public class Operation extends NuxeoEntity {

    public static final String INPUT_PART = "input";

    public static final String INPUT_PARTS = "input#";

    @JsonIgnore
    protected OperationBody body;

    protected String operationId;

    public Operation(NuxeoClient nuxeoClient) {
        super(ConstantsV1.ENTITY_TYPE_OPERATION, nuxeoClient, OperationAPI.class);
        body = new OperationBody();
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public Operation newRequest(String operationId) {
        this.operationId = operationId;
        return this;
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

    public <T> T execute(String operationId, OperationBody body) {
        Object input = body.getInput();
        if (input instanceof Blob) { // If input is blob or blobs -> use multipart
            Map<String, RequestBody> fbodys = new HashMap<>();
            RequestBody fbody = RequestBody.create(MediaType.parse(((Blob) input).getMimeType()),
                    ((Blob) input).getFile());
            fbodys.put(INPUT_PART, fbody);
            return (T) getResponse(operationId, body, fbodys);
        } else if (input instanceof Blobs) { // If input is blob or blobs -> use multipart
            Map<String, RequestBody> fbodys = new HashMap<>();
            for (int i = 0; i < ((Blobs) input).size(); i++) {
                Blob fileBlob = ((Blobs) input).getBlobs().get(i);
                RequestBody fbody = RequestBody.create(MediaType.parse(fileBlob.getMimeType()), fileBlob.getFile());
                fbodys.put(INPUT_PARTS + String.valueOf(i), fbody);
            }
            return (T) getResponse(operationId, body, fbodys);
        } else {
            return (T) getResponse(operationId, body);
        }
    }

    public <T> T execute(String operationId) {
        return execute(operationId, this.body);
    }

    public <T> T execute(String batchId, String fileIdx, String operationId, OperationBody body) {
        return (T) getResponse(batchId, fileIdx, operationId, body);
    }

    public <T> T execute() {
        return execute(this.operationId, this.body);
    }

    /** Operation Execution Methods Async **/

    public void execute(String operationId, OperationBody body, Callback<Object> callback) {
        Object input = body.getInput();
        if (input instanceof Blob) { // If input is blob or blobs -> use multipart
            Map<String, RequestBody> fbodys = new HashMap<>();
            RequestBody fbody = RequestBody.create(MediaType.parse(((Blob) input).getMimeType()),
                    ((Blob) input).getFile());
            fbodys.put(INPUT_PART, fbody);
            super.execute(callback, operationId, body, fbodys);
        } else if (input instanceof Blobs) { // If input is blob or blobs -> use multipart
            Map<String, RequestBody> fbodys = new HashMap<>();
            for (int i = 0; i < ((Blobs) input).size(); i++) {
                Blob fileBlob = ((Blobs) input).getBlobs().get(i);
                RequestBody fbody = RequestBody.create(MediaType.parse(fileBlob.getMimeType()), fileBlob.getFile());
                fbodys.put(INPUT_PARTS + String.valueOf(i), fbody);
            }
            super.execute(callback, operationId, body, fbodys);
        } else {
            super.execute(callback, operationId, body);
        }
    }

    public void execute(String operationId, Callback<Object> callback) {
        this.execute(operationId, this.body, callback);
    }

    public void execute(String batchId, String fileIdx, String operationId, OperationBody body,
            Callback<Object> callback) {
        execute(callback, batchId, fileIdx, operationId, body);
    }

    public void execute(Callback<Object> callback) {
        this.execute(this.operationId, this.body, callback);
    }
}
