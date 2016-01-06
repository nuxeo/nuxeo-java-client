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

import java.io.File;
import java.util.List;
import java.util.Objects;

import org.nuxeo.java.client.api.NuxeoClient;
import org.nuxeo.java.client.api.methods.BatchUploadAPI;
import org.nuxeo.java.client.api.objects.NuxeoEntity;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * @since 0.1
 */
public class BatchUpload extends NuxeoEntity {

    protected String batchId;

    protected String fileIdx;

    protected String uploadType;

    protected long uploadedSize;

    protected String dropped;

    protected Integer[] uploadedChunkIds;

    protected Integer chunkCount;

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

    public Integer[] getUploadedChunkIds() {
        return uploadedChunkIds;
    }

    public Integer getChunkCount() {
        return chunkCount;
    }

    public BatchUpload createBatch() {
        return (BatchUpload) getResponse();
    }

    public BatchUpload upload(String fileName, long fileSize, String fileType, String batchId, String fileIdx,
            File file) {
        RequestBody fbody = RequestBody.create(MediaType.parse(fileType), file);
        return (BatchUpload) getResponse(fileName, Objects.toString(fileSize), fileType, batchId, fileIdx, fbody);
    }

    public BatchUpload upload(String fileName, long fileSize, String fileType, String uploadType,
            String uploadChunkIndex, String totalChunkCount, String batchId, String fileIdx, File file) {
        return (BatchUpload) getResponse(fileName, Objects.toString(fileSize), fileType, uploadType, uploadChunkIndex, totalChunkCount,
                batchId, fileIdx, file);
    }

    public BatchUpload cancel(String batchId) {
        return (BatchUpload) getResponse(batchId);
    }

    public List<BatchFile> getBatchFiles(String batchId) {
        return (List<BatchFile>) getResponse(batchId);
    }

    public BatchFile getBatchFile(String batchId, String fileIdx) {
        return (BatchFile) getResponse(batchId, fileIdx);
    }
}
