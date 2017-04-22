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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import org.nuxeo.client.ConstantsV1;
import org.nuxeo.client.MediaTypes;
import org.nuxeo.client.NuxeoClient;
import org.nuxeo.client.methods.BatchUploadAPI;
import org.nuxeo.client.objects.AbstractConnectable;
import org.nuxeo.client.objects.Operation;
import org.nuxeo.client.spi.NuxeoClientException;

import com.fasterxml.jackson.annotation.JsonIgnore;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * @since 0.1
 */
public class BatchUpload extends AbstractConnectable<BatchUploadAPI> {

    @JsonIgnore
    protected int chunkSize;

    protected String batchId;

    protected String fileIdx;

    protected String uploadType;

    protected long uploadedSize;

    public BatchUpload() {
        super(BatchUploadAPI.class);
    }

    public BatchUpload(NuxeoClient nuxeoClient) {
        super(BatchUploadAPI.class, nuxeoClient);
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

    public BatchUpload createBatch() {
        return fetchResponse(api.createBatch());
    }

    public BatchUpload upload(String name, long length, String fileType, String batchId, String fileIdx, File file) {
        if (chunkSize == 0) {
            // Post file
            RequestBody fbody = RequestBody.create(MediaType.parse(fileType), file);
            return fetchResponse(api.upload(name, Objects.toString(length), fileType, ConstantsV1.UPLOAD_NORMAL_TYPE,
                    "0", "1", batchId, fileIdx, fbody));
        }
        Object response = null;
        int contentLength = 0;
        byte[] buffer = new byte[chunkSize];
        int chunkIndex = 0;
        long chunkCount = (file.length() + chunkSize - 1) / chunkSize;
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            while ((contentLength = bis.read(buffer)) > 0) {
                // Post chunk as a stream
                RequestBody requestBody = RequestBody.create(MediaTypes.APPLICATION_OCTET_STREAM.toOkHttpMediaType(),
                        buffer, 0, contentLength);
                response = fetchResponse(api.upload(name, Objects.toString(length), fileType, ConstantsV1.UPLOAD_CHUNKED_TYPE,
                        Objects.toString(chunkIndex), Objects.toString(chunkCount), batchId, fileIdx, requestBody));
                chunkIndex++;
            }
            return (BatchUpload) response;
        } catch (IOException reason) {
            throw new NuxeoClientException(reason);
        }
    }

    public void cancel(String batchId) {
        fetchResponse(api.cancel(batchId));
    }

    public void cancel() {
        cancel(batchId);
    }

    public List<BatchFile> fetchBatchFiles(String batchId) {
        return fetchResponse(api.fetchBatchFiles(batchId));
    }

    public BatchFile fetchBatchFile(String batchId, String fileIdx) {
        return fetchResponse(api.fetchBatchFile(batchId, fileIdx));
    }

    public BatchFile fetchBatchFile(String fileIdx) {
        return fetchBatchFile(batchId, fileIdx);
    }

    public List<BatchFile> fetchBatchFiles() {
        return fetchBatchFiles(batchId);
    }

    public BatchBlob getBatchBlob() {
        return getBatchBlob(batchId, fileIdx);
    }

    private BatchBlob getBatchBlob(String batchId, String fileIdx) {
        return new BatchBlob(batchId, fileIdx);
    }

    public BatchUpload chunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
        return this;
    }

    public BatchUpload enableChunk() {
        this.chunkSize = ConstantsV1.CHUNK_SIZE;
        return this;
    }

    public Object execute(Operation operation) {
        return execute(batchId, fileIdx, operation);
    }

    public Object execute(String batchId, String fileIdx, Operation operation) {
        return nuxeoClient.automation().execute(batchId, fileIdx, operation.getOperationId(), operation.getBody());
    }

}
