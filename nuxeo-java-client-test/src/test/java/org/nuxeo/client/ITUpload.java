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
package org.nuxeo.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.nuxeo.client.objects.Document;
import org.nuxeo.client.objects.blob.FileBlob;
import org.nuxeo.client.objects.upload.BatchUpload;
import org.nuxeo.client.objects.upload.BatchUploadManager;
import org.nuxeo.client.spi.NuxeoClientException;
import org.nuxeo.common.utils.FileUtils;

/**
 * @since 0.1
 */
public class ITUpload extends AbstractITBase {

    @Test
    public void itCanManageBatch() {
        BatchUploadManager batchUploadManager = nuxeoClient.batchUploadManager();
        BatchUpload batchUpload = batchUploadManager.createBatch();
        assertNotNull(batchUpload);
        assertNotNull(batchUpload.getBatchId());
        batchUpload.cancel();
        try {
            batchUpload.fetchBatchUploads();
            fail("Should be not found");
        } catch (NuxeoClientException reason) {
            assertEquals(404, reason.getStatus());
        }
    }

    @Test
    public void itCanUploadFiles() {
        // Upload the file
        BatchUploadManager batchUploadManager = nuxeoClient.batchUploadManager();
        BatchUpload batchUpload = batchUploadManager.createBatch();
        assertNotNull(batchUpload);
        String batchId = batchUpload.getBatchId();
        assertNotNull(batchId);
        File file = FileUtils.getResourceFileFromContext("sample.jpg");
        batchUpload = batchUpload.upload("1", file);
        assertNotNull(batchUpload);
        assertEquals(batchId, batchUpload.getBatchId());
        assertEquals("1", batchUpload.getFileIdx());
        assertEquals(file.getName(), batchUpload.getName());
        assertEquals(ConstantsV1.UPLOAD_NORMAL_TYPE, batchUpload.getUploadType());

        // Check the batch by fetching it again
        batchUpload = batchUpload.fetchBatchUpload();
        assertNotNull(batchUpload);
        assertEquals(batchId, batchUpload.getBatchId());
        assertEquals("1", batchUpload.getFileIdx());
        assertEquals(file.getName(), batchUpload.getName());
        assertEquals(ConstantsV1.UPLOAD_NORMAL_TYPE, batchUpload.getUploadType());

        // Upload another file and check files
        file = FileUtils.getResourceFileFromContext("blob.json");
        batchUpload.upload("2", file);
        List<BatchUpload> batchUploads = batchUpload.fetchBatchUploads();
        assertNotNull(batchUploads);
        assertEquals(2, batchUploads.size());
        assertEquals("sample.jpg", batchUploads.get(0).getName());
        assertEquals(batchId, batchUploads.get(0).getBatchId());
        assertEquals("1", batchUploads.get(0).getFileIdx());
        assertEquals("blob.json", batchUploads.get(1).getName());
        assertEquals(batchId, batchUploads.get(1).getBatchId());
        assertEquals("2", batchUploads.get(1).getFileIdx());
    }

    @Test
    public void itCanUploadChunks() throws IOException {
        // Create batch upload
        BatchUploadManager batchUploadManager = nuxeoClient.batchUploadManager();
        BatchUpload batchUpload = batchUploadManager.createBatch().enableChunk();
        assertNotNull(batchUpload);
        String batchId = batchUpload.getBatchId();

        // Upload file chunks
        File file = FileUtils.getResourceFileFromContext("sample.jpg");
        batchUpload = batchUpload.upload("1", file);
        assertNotNull(batchUpload);
        assertEquals(batchId, batchUpload.getBatchId());
        assertEquals("1", batchUpload.getFileIdx());
        assertEquals(file.getName(), batchUpload.getName());
        assertEquals(ConstantsV1.UPLOAD_CHUNKED_TYPE, batchUpload.getUploadType());
        assertEquals(file.length(), batchUpload.getSize());
        assertEquals(4, batchUpload.getChunkCount());

        // Check the batch by fetching it again
        batchUpload = batchUpload.fetchBatchUpload();
        assertNotNull(batchUpload);
        assertEquals(file.getName(), batchUpload.getName());
        assertEquals(batchId, batchUpload.getBatchId());
        assertEquals("1", batchUpload.getFileIdx());
        assertEquals(ConstantsV1.UPLOAD_CHUNKED_TYPE, batchUpload.getUploadType());
        assertEquals(file.length(), batchUpload.getSize());
        assertEquals(4, batchUpload.getChunkCount());
        assertEquals(batchUpload.getChunkCount(), batchUpload.getUploadedChunkIds().length);
    }

    @Test
    public void itCanAttachABatchToADoc() {
        // Upload file chunks
        BatchUploadManager batchUploadManager = nuxeoClient.batchUploadManager();
        BatchUpload batchUpload = batchUploadManager.createBatch();
        assertNotNull(batchUpload);
        File file = FileUtils.getResourceFileFromContext("sample.jpg");
        batchUpload = batchUpload.upload("1", file);
        assertNotNull(batchUpload);

        // Getting a doc and attaching the batch file
        Document doc = Document.createWithName("file", "File");
        doc.setPropertyValue("dc:title", "new title");
        doc = nuxeoClient.repository().createDocumentByPath("/", doc);
        assertNotNull(doc);
        doc.setPropertyValue("file:content", batchUpload.getBatchBlob());
        doc = doc.updateDocument();
        assertEquals("sample.jpg", ((Map) doc.getPropertyValue("file:content")).get("name"));
    }

    @Test
    public void itCanExecuteOp() {
        // Upload file
        BatchUploadManager batchUploadManager = nuxeoClient.batchUploadManager();
        BatchUpload batchUpload = batchUploadManager.createBatch();
        assertNotNull(batchUpload);
        File file = FileUtils.getResourceFileFromContext("sample.jpg");
        batchUpload = batchUpload.upload("1", file);
        assertNotNull(batchUpload);

        // Getting a doc and attaching the batch file
        Document doc = Document.createWithName("file", "File");
        doc.setPropertyValue("dc:title", "new title");
        doc = nuxeoClient.repository().createDocumentByPath("/", doc);
        assertNotNull(doc);
        FileBlob blob = batchUpload.operation("Blob.AttachOnDocument").param("document", doc).execute();
        assertNotNull(blob);
    }

    /**
     * This test tests capabilities of client to be mapped behind a rest implementation.
     */
    @Test
    public void itCanUploadBatchFileThroughRestMapping() {
        BatchUploadManager batchUploadManager = nuxeoClient.batchUploadManager();

        // POST request to create batch
        BatchUpload batchUpload = batchUploadManager.createBatch();
        String batchId = batchUpload.getBatchId();
        // return batchId

        // POST request with blob and batch id to upload blob
        File file = FileUtils.getResourceFileFromContext("sample.jpg");
        batchUpload = batchUploadManager.getBatch(batchId);
        batchUpload = batchUpload.upload("1", file);
        assertNotNull(batchUpload);
        // return batchId and fileIdx

        // POST request with batchId and fileIdx to create document
        Document doc = Document.createWithName("file_rest", "File");
        doc.setPropertyValue("dc:title", "new title");
        doc.setPropertyValue("file:content", batchUploadManager.getBatch(batchId, "1").getBatchBlob());
        doc = nuxeoClient.repository().createDocumentByPath("/", doc);
        assertNotNull(doc);
        // return docId

        // GET request with docId to get file
        FileBlob blob = nuxeoClient.repository().fetchBlobById(doc.getId(), Document.DEFAULT_FILE_CONTENT);
        assertNotNull(blob);
        assertEquals("sample.jpg", blob.getFilename());
        assertEquals(file.length(), blob.getLength());
        assertNotNull(blob.getFile());
        assertEquals(file.length(), blob.getFile().length());
    }

}
