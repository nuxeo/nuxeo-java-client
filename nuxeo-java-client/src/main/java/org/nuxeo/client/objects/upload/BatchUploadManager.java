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

import java.util.List;

import org.nuxeo.client.NuxeoClient;
import org.nuxeo.client.methods.BatchUploadAPI;
import org.nuxeo.client.objects.AbstractConnectable;
import org.nuxeo.client.objects.blob.Blob;

/**
 * @since 3.0
 */
public class BatchUploadManager extends AbstractConnectable<BatchUploadAPI, BatchUploadManager> {

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

    /**
     * Uploads the given blob to the current {@link BatchUpload} for given index.
     *
     * @since 3.1
     */
    public BatchUpload upload(String batchId, String fileIdx, Blob blob) {
        return getBatch(batchId).upload(fileIdx, blob);
    }

    public void cancel(String batchId) {
        getBatch(batchId).cancel();
    }

}
