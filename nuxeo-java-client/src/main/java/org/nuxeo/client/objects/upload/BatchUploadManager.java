/*
 * (C) Copyright 2017 Nuxeo (http://nuxeo.com/) and others.
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
 *     Kevin Leturc <kleturc@nuxeo.com>
 */
package org.nuxeo.client.objects.upload;

import java.io.File;
import java.util.List;

import org.nuxeo.client.NuxeoClient;
import org.nuxeo.client.methods.BatchUploadAPI;
import org.nuxeo.client.objects.AbstractConnectable;

/**
 * @since 3.0
 */
public class BatchUploadManager extends AbstractConnectable<BatchUploadAPI> {

    public BatchUploadManager(NuxeoClient nuxeoClient) {
        super(BatchUploadAPI.class, nuxeoClient);
    }

    public BatchUpload createBatch() {
        return fetchResponse(api.createBatch());
    }

    public BatchUpload getBatch(String batchId) {
        return new BatchUpload(nuxeoClient, batchId);
    }

    public BatchUpload getBatch(String batchId, String fileIdx) {
        return new BatchUpload(nuxeoClient, batchId, fileIdx);
    }

    public List<BatchUpload> fetchBatchUploads(String batchId) {
        return getBatch(batchId).fetchBatchUploads();
    }

    public BatchUpload fetchBatchUpload(String batchId, String fileIdx) {
        return getBatch(batchId, fileIdx).fetchBatchUpload();
    }

    public BatchUpload upload(String batchId, String fileIdx, File file) {
        return getBatch(batchId).upload(fileIdx, file);
    }

    public BatchUpload upload(String batchId, String fileIdx, File file, String name) {
        return getBatch(batchId).upload(fileIdx, file, name);
    }

    public BatchUpload upload(String batchId, String fileIdx, File file, String name, String fileType) {
        return getBatch(batchId).upload(fileIdx, file, name, fileType);
    }

    public BatchUpload upload(String batchId, String fileIdx, File file, String name, String fileType,
            long length) {
        return getBatch(batchId).upload(fileIdx, file, name, fileType, length);
    }

    public void cancel(String batchId) {
        getBatch(batchId).cancel();
    }

}
