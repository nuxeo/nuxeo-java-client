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
package org.nuxeo.client.objects.upload;

import static org.nuxeo.client.ConstantsV1.UPLOAD_CHUNKED_TYPE;
import static org.nuxeo.client.ConstantsV1.UPLOAD_NORMAL_TYPE;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import org.nuxeo.client.ConstantsV1;
import org.nuxeo.client.MediaTypes;
import org.nuxeo.client.NuxeoClient;
import org.nuxeo.client.Requests;
import org.nuxeo.client.methods.BatchUploadAPI;
import org.nuxeo.client.objects.AbstractConnectable;
import org.nuxeo.client.objects.blob.Blob;
import org.nuxeo.client.objects.blob.FileBlob;
import org.nuxeo.client.objects.operation.OperationBody;
import org.nuxeo.client.spi.NuxeoClientException;

import com.fasterxml.jackson.annotation.JsonIgnore;

import okhttp3.RequestBody;

/**
 * @since 0.1
 */
public class BatchUpload extends AbstractConnectable<BatchUploadAPI, BatchUpload> {

    @JsonIgnore
    protected int chunkSize;

    protected String name;

    protected String batchId;

    protected String fileIdx;

    protected String uploadType;

    protected long uploadedSize;

    protected int[] uploadedChunkIds;

    protected int chunkCount;

    /**
     * For deserialization purpose.
     */
    protected BatchUpload() {
        super(BatchUploadAPI.class);
    }

    protected BatchUpload(NuxeoClient nuxeoClient, String batchId) {
        super(BatchUploadAPI.class, nuxeoClient);
        this.batchId = batchId;
    }

    protected BatchUpload(NuxeoClient nuxeoClient, String batchId, String fileIdx) {
        this(nuxeoClient, batchId);
        this.fileIdx = fileIdx;
    }

    public String getName() {
        return name;
    }

    public String getBatchId() {
        return batchId;
    }

    public String getFileIdx() {
        return fileIdx;
    }

    public String getUploadType() {
        return uploadType;
    }

    public long getUploadedSize() {
        return uploadedSize;
    }

    public long getSize() {
        return uploadedSize;
    }

    public int[] getUploadedChunkIds() {
        return uploadedChunkIds;
    }

    public int getChunkCount() {
        return chunkCount;
    }

    /**
     * Use for deserialization purpose on GET calls.
     */
    protected void setSize(long size) {
        uploadedSize = size;
    }

    /**
     * @deprecated since 3.1, use {@link #upload(String, Blob)} instead
     */
    @Deprecated
    public BatchUpload upload(String fileIdx, File file) {
        return upload(fileIdx, new FileBlob(file));
    }

    /**
     * @deprecated since 3.1, use {@link #upload(String, Blob)} instead
     */
    @Deprecated
    public BatchUpload upload(String fileIdx, File file, String name) {
        return upload(fileIdx, new FileBlob(file, name));
    }

    /**
     * @deprecated since 3.1, use {@link #upload(String, Blob)} instead
     */
    @Deprecated
    public BatchUpload upload(String fileIdx, File file, String name, String fileType) {
        return upload(fileIdx, new FileBlob(file, name, fileType));
    }

    /**
     * @deprecated since 3.1, use {@link #upload(String, Blob)} instead
     */
    @Deprecated
    public BatchUpload upload(String fileIdx, File file, String name, String fileType, long length) {
        // length parameter is kinda weird because we have it in File -> forget it
        return upload(fileIdx, new FileBlob(file, name, fileType));
    }

    /**
     * Uploads the given blob to the current {@link BatchUpload} for given index.
     *
     * @since 3.1
     */
    public BatchUpload upload(String fileIdx, Blob blob) {
        String filename = blob.getFilename();
        String encodedFilename = encodeToAsciiForHeader(filename);
        String mimeType = blob.getMimeType();
        long length = blob.getContentLength();
        if (chunkSize == 0) {
            // Post blob
            RequestBody fbody = Requests.create(blob);
            BatchUpload response = fetchResponse(
                    api.upload(encodedFilename, length, mimeType, UPLOAD_NORMAL_TYPE, 0, 1, batchId, fileIdx, fbody));
            response.name = filename;
            response.batchId = batchId;
            response.fileIdx = fileIdx;
            return response;
        }
        if (length <= 0) {
            throw new NuxeoClientException("You have to give blob length in order to upload it by chunk");
        }
        BatchUpload response = null;
        int bufferLength;
        byte[] buffer = new byte[chunkSize];
        long chunkIndex = 0;
        long chunkNumber = (length + chunkSize - 1) / chunkSize;
        try (BufferedInputStream bis = new BufferedInputStream(blob.getStream())) {
            while ((bufferLength = bis.read(buffer)) > 0) {
                // Post chunk as a stream
                RequestBody requestBody = RequestBody.create(MediaTypes.APPLICATION_OCTET_STREAM.toOkHttpMediaType(),
                        buffer, 0, bufferLength);
                response = fetchResponse(api.upload(encodedFilename, length, mimeType, UPLOAD_CHUNKED_TYPE, chunkIndex,
                        chunkNumber, batchId, fileIdx, requestBody));
                chunkIndex++;
            }
            if (response != null) {
                response.name = filename;
                // batchId and fileIdx are retrieved
                // set back the internal value in order to upload with same settings
                response.chunkSize = chunkSize;
                // uploadedSize doesn't have the right value
                response.uploadedSize = length;
            }
            return response;
        } catch (IOException reason) {
            throw new NuxeoClientException("Error during batch upload", reason);
        }
    }

    protected String encodeToAsciiForHeader(String string) {
        try {
            return URLEncoder.encode(string, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new NuxeoClientException("Unable to encode the header: " + string, e);
        }
    }

    public void cancel() {
        fetchResponse(api.cancel(batchId));
    }

    public List<BatchUpload> fetchBatchUploads() {
        List<BatchUpload> response = fetchResponse(api.fetchBatchUploads(batchId));
        int i = 0;
        for (BatchUpload upload : response) {
            upload.batchId = batchId;
            upload.fileIdx = String.valueOf(++i);
        }
        return response;
    }

    /**
     * This method can only be called on a {@link BatchUpload} representing a real upload (ie: fileIdx != null).
     */
    public BatchUpload fetchBatchUpload() {
        if (fileIdx == null) {
            throw new NuxeoClientException("Unable to fetch BatchUpload because fileIdx is null");
        }
        return fetchBatchUpload(fileIdx);
    }

    public BatchUpload fetchBatchUpload(String fileIdx) {
        BatchUpload response = fetchResponse(api.fetchBatchUpload(batchId, fileIdx));
        if (name != null) {
            response.name = name;
        }
        response.batchId = batchId;
        response.fileIdx = fileIdx;
        return response;
    }

    /**
     * This method can only be called on a {@link BatchUpload} representing a real upload (ie: fileIdx != null).
     */
    public BatchBlob getBatchBlob() {
        if (fileIdx == null) {
            throw new NuxeoClientException("Unable to instantiate BatchBlob because fileIdx is null");
        }
        return getBatchBlob(fileIdx);
    }

    public BatchBlob getBatchBlob(String fileIdx) {
        return new BatchBlob(batchId, fileIdx);
    }

    public BatchUpload chunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
        // For consistency
        this.uploadType = UPLOAD_CHUNKED_TYPE;
        return this;
    }

    public BatchUpload enableChunk() {
        return chunkSize(ConstantsV1.CHUNK_SIZE);
    }

    /**
     * This method can only be called on a {@link BatchUpload} representing a real upload (ie: fileIdx != null).
     */
    public BatchUploadOperation operation(String operationId) {
        if (fileIdx == null) {
            throw new NuxeoClientException(
                    "Unable to execute operation on a BatchUpload not representing a blob (fileIdx == null)");
        }
        return new BatchUploadOperation(fileIdx, operationId);
    }

    public class BatchUploadOperation {

        protected final String fileIdx;

        protected final String operationId;

        protected final OperationBody body;

        public BatchUploadOperation(String fileIdx, String operationId) {
            this.fileIdx = fileIdx;
            this.operationId = operationId;
            this.body = new OperationBody();
        }

        public BatchUploadOperation param(String key, Object parameter) {
            body.getParameters().put(key, parameter);
            return this;
        }

        public BatchUploadOperation context(String key, Object contextValue) {
            body.getContext().put(key, contextValue);
            return this;
        }

        public BatchUploadOperation parameters(Map<String, Object> parameters) {
            body.setParameters(parameters);
            return this;
        }

        public BatchUploadOperation context(Map<String, Object> context) {
            body.setContext(context);
            return this;
        }

        @SuppressWarnings("unchecked")
        public <T> T execute() {
            return (T) fetchResponse(api.execute(batchId, fileIdx, operationId, body));
        }

    }

}
