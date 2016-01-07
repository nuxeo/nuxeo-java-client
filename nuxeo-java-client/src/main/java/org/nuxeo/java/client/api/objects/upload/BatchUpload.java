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
package org.nuxeo.java.client.api.objects.upload;

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

import org.nuxeo.java.client.api.ConstantsV1;
import org.nuxeo.java.client.api.NuxeoClient;
import org.nuxeo.java.client.api.methods.BatchUploadAPI;
import org.nuxeo.java.client.api.objects.NuxeoEntity;
import org.nuxeo.java.client.internals.spi.NuxeoClientException;

/**
 * @since 0.1
 */
public class BatchUpload extends NuxeoEntity {

    protected String batchId;

    protected String fileIdx;

    protected String uploadType;

    protected long uploadedSize;

    protected String dropped;

    public BatchUpload(NuxeoClient nuxeoClient) {
        super(null, nuxeoClient, BatchUploadAPI.class);
    }

    public BatchUpload() {
        super(null);
    }

    public String getDropped() {
        return dropped;
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

    public BatchUpload upload(String fileName, long fileSize, String fileType, String batchId, String fileIdx, File file) {
        RequestBody fbody = RequestBody.create(MediaType.parse(fileType), file);
        return (BatchUpload) getResponse(fileName, Objects.toString(fileSize), fileType, batchId, fileIdx, fbody);
    }

    protected Object uploadChunks(String fileName, long fileSize, String fileType, String uploadType,
            String uploadChunkIndex, String totalChunkCount, String batchId, String fileIdx, File file) {
        RequestBody fbody = RequestBody.create(MediaType.parse(fileType), file);
        return getResponse(fileName, Objects.toString(fileSize), fileType, uploadType, uploadChunkIndex,
                totalChunkCount, batchId, fileIdx, fbody);
    }


    public BatchUpload uploadChunks(String name, long length, String fileType, String batchId, String fileIdx, File file) {
        int partCounter = 1;
        int sizeOfFiles = ConstantsV1.CHUNK_SIZE; // 1MB
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
            if(tmp==files.size()-1){
                batchUpload = (BatchUpload) uploadChunks(name, length, fileType, ConstantsV1.UPLOAD_CHUNKED_TYPE, Objects.toString(tmp),
                        Objects.toString(files.size()), batchId, fileIdx, chunk);
            }else {
                uploadChunks(name, length, fileType, ConstantsV1.UPLOAD_CHUNKED_TYPE, Objects.toString(tmp),
                        Objects.toString(files.size()), batchId, fileIdx, chunk);
                tmp++;
            }
        }
        return batchUpload;
    }

    public BatchUpload cancel(String batchId) {
        return (BatchUpload) getResponse(batchId);
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

}
