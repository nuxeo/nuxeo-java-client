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
package org.nuxeo.client.api.objects.upload;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.RequestBody;

import org.nuxeo.client.api.ConstantsV1;
import org.nuxeo.client.api.NuxeoClient;
import org.nuxeo.client.api.methods.BatchUploadAPI;
import org.nuxeo.client.api.objects.NuxeoEntity;
import org.nuxeo.client.api.objects.Operation;
import org.nuxeo.client.internals.spi.NuxeoClientException;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @since 0.1
 */
public class BatchUpload extends NuxeoEntity {

    @JsonIgnore
    protected int chunkSize;

    protected String batchId;

    protected String fileIdx;

    protected String uploadType;

    protected long uploadedSize;

    public BatchUpload(NuxeoClient nuxeoClient) {
        super(null, nuxeoClient, BatchUploadAPI.class);
    }

    public BatchUpload() {
        super(null);
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
        return (BatchUpload) getResponse();
    }

    protected Object upload(String fileName, long fileSize, String fileType, String uploadType,
            String uploadChunkIndex, String totalChunkCount, String batchId, String fileIdx, File file) {
        RequestBody fbody = RequestBody.create(MediaType.parse(fileType), file);
        return getResponse(fileName, Objects.toString(fileSize), fileType, uploadType, uploadChunkIndex,
                totalChunkCount, batchId, fileIdx, fbody);
    }

    public BatchUpload upload(String name, long length, String fileType, String batchId, String fileIdx, File file) {
        if (chunkSize == 0) {
            return (BatchUpload) upload(name, length, fileType, ConstantsV1.UPLOAD_NORMAL_TYPE, "0", "1", batchId,
                    fileIdx, file);
        }
        int partCounter = 1;
        int sizeOfFiles = chunkSize;
        List<File> files = new ArrayList<>();
        BatchUpload batchUpload = null;
        byte[] buffer = new byte[sizeOfFiles];
        int tmp = 0;
        try {
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
                while ((tmp = bis.read(buffer)) > 0) {
                    // write each chunk of data into separate file with different number in name
                    File newFile = new File(file.getParent(), name + "." + String.format("%03d", partCounter++));
                    try (FileOutputStream out = new FileOutputStream(newFile)) {
                        out.write(buffer, 0, tmp); // tmp is chunk size
                        files.add(newFile);
                    }
                }
            }
        } catch (IOException reason) {
            throw new NuxeoClientException(reason);
        }
        tmp = 0;
        for (File chunk : files) {
            if (tmp == files.size() - 1) {
                batchUpload = (BatchUpload) upload(name, length, fileType, ConstantsV1.UPLOAD_CHUNKED_TYPE,
                        Objects.toString(tmp), Objects.toString(files.size()), batchId, fileIdx, chunk);
            } else {
                upload(name, length, fileType, ConstantsV1.UPLOAD_CHUNKED_TYPE, Objects.toString(tmp),
                        Objects.toString(files.size()), batchId, fileIdx, chunk);
                tmp++;
            }
        }
        return batchUpload;
    }

    public void cancel(String batchId) {
        getResponse(batchId);
    }

    public void cancel() {
        cancel(batchId);
    }

    public List<BatchFile> fetchBatchFiles(String batchId) {
        return (List<BatchFile>) getResponse(batchId);
    }

    public BatchFile fetchBatchFile(String batchId, String fileIdx) {
        return (BatchFile) getResponse(batchId, fileIdx);
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
