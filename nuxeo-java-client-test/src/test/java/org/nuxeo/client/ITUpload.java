/*
 * (C) Copyright 2016-2018 Nuxeo (http://nuxeo.com/) and others.
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
import static org.nuxeo.client.Operations.BLOB_ATTACH_ON_DOCUMENT;

import java.io.File;
import java.util.List;

import org.junit.Test;
import org.nuxeo.client.methods.BatchUploadAPI;
import org.nuxeo.client.objects.Document;
import org.nuxeo.client.objects.blob.Blob;
import org.nuxeo.client.objects.blob.FileBlob;
import org.nuxeo.client.objects.blob.StreamBlob;
import org.nuxeo.client.objects.upload.BatchUpload;
import org.nuxeo.client.objects.upload.BatchUploadManager;
import org.nuxeo.client.spi.NuxeoClientRemoteException;
import org.nuxeo.client.spi.auth.PortalSSOAuthInterceptor;
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
        } catch (NuxeoClientRemoteException reason) {
            assertEquals(404, reason.getStatus());
        }
    }

    @Test
    public void itCanUploadFiles() {
        itCanUploadFiles(nuxeoClient);
    }

    /**
     * JAVACLIENT-142: Checks that using {@link PortalSSOAuthInterceptor} doesn't erase HTTP headers set from
     * {@link BatchUploadAPI}.
     */
    @Test
    public void itCanUploadFilesWithPortalSSOAuthentication() {
        NuxeoClient nuxeoClient = ITBase.createClientPortalSSO();
        itCanUploadFiles(nuxeoClient);
    }

    private void itCanUploadFiles(NuxeoClient nuxeoClient) {
        String filename1 = "sample.jpg";
        String filename2 = "blob.json";

        // Upload the file
        BatchUploadManager batchUploadManager = nuxeoClient.batchUploadManager();
        BatchUpload batchUpload = batchUploadManager.createBatch();
        assertNotNull(batchUpload);
        String batchId = batchUpload.getBatchId();
        assertNotNull(batchId);
        File file = FileUtils.getResourceFileFromContext(filename1);
        FileBlob fileBlob = new FileBlob(file);

        batchUpload = batchUpload.upload("1", fileBlob);
        assertBatchUpload(batchId, "1", filename1, batchUpload);

        // Check the batch by fetching it again
        batchUpload = batchUpload.fetchBatchUpload();
        assertBatchUpload(batchId, "1", filename1, batchUpload);

        // Check the batch by fetching it again and re-instantiating the batch upload (in BatchUpload we set back some
        // properties in order to handle responses with light metadata)
        batchUpload = batchUploadManager.fetchBatchUpload(batchId, "1");
        assertBatchUpload(batchId, "1", filename1, batchUpload);

        // Upload another file and check files
        file = FileUtils.getResourceFileFromContext(filename2);
        fileBlob = new FileBlob(file);
        batchUpload.upload("2", fileBlob);
        List<BatchUpload> batchUploads = batchUpload.fetchBatchUploads();
        assertNotNull(batchUploads);
        assertEquals(2, batchUploads.size());
        assertBatchUpload(batchId, "1", filename1, batchUploads.get(0));
        assertBatchUpload(batchId, "2", filename2, batchUploads.get(1));
    }

    private void assertBatchUpload(String expectedBatchId, String expectedFileIdx, String expectedName,
            BatchUpload actual) {
        assertNotNull(actual);
        assertEquals(expectedBatchId, actual.getBatchId());
        assertEquals(expectedFileIdx, actual.getFileIdx());
        assertEquals(expectedName, actual.getName());
        assertEquals(ConstantsV1.UPLOAD_NORMAL_TYPE, actual.getUploadType());
    }

    @Test
    public void itCanUploadChunks() {
        // Create batch upload
        BatchUploadManager batchUploadManager = nuxeoClient.batchUploadManager();
        BatchUpload batchUpload = batchUploadManager.createBatch().enableChunk();
        assertNotNull(batchUpload);
        String batchId = batchUpload.getBatchId();

        // Upload file chunks
        File file = FileUtils.getResourceFileFromContext("sample.jpg");
        FileBlob fileBlob = new FileBlob(file);
        batchUpload = batchUpload.upload("1", fileBlob);
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
        FileBlob fileBlob = new FileBlob(file);
        batchUpload = batchUpload.upload("1", fileBlob);
        assertNotNull(batchUpload);

        // Getting a doc and attaching the batch file
        Document doc = Document.createWithName("file", "File");
        doc.setPropertyValue("dc:title", "new title");
        doc = nuxeoClient.repository().createDocumentByPath("/", doc);
        assertNotNull(doc);
        doc.setPropertyValue("file:content", batchUpload.getBatchBlob());
        doc = doc.updateDocument();
        assertEquals("sample.jpg", doc.getPropertyValue("file:content/name"));
    }

    @Test
    public void itCanExecuteOp() {
        // Upload file
        BatchUploadManager batchUploadManager = nuxeoClient.batchUploadManager();
        BatchUpload batchUpload = batchUploadManager.createBatch();
        assertNotNull(batchUpload);
        File file = FileUtils.getResourceFileFromContext("sample.jpg");
        FileBlob fileBlob = new FileBlob(file);
        batchUpload = batchUpload.upload("1", fileBlob);
        assertNotNull(batchUpload);

        // Getting a doc and attaching the batch file
        Document doc = Document.createWithName("file", "File");
        doc.setPropertyValue("dc:title", "new title");
        doc = nuxeoClient.repository().createDocumentByPath("/", doc);
        assertNotNull(doc);
        Blob blob = batchUpload.operation(BLOB_ATTACH_ON_DOCUMENT).param("document", doc).execute();
        assertNotNull(blob);
        assertContentEquals("sample.jpg", blob);
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
        FileBlob fileBlob = new FileBlob(file);
        batchUpload = batchUploadManager.getBatch(batchId);
        batchUpload = batchUpload.upload("1", fileBlob);
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
        StreamBlob blob = nuxeoClient.repository().streamBlobById(doc.getId(), Document.DEFAULT_FILE_CONTENT);
        assertNotNull(blob);
        assertEquals("sample.jpg", blob.getFilename());
        assertEquals(file.length(), blob.getContentLength());
        assertContentEquals("sample.jpg", blob);
    }

    /*
     * JAVACLIENT-208
     */
    @Test
    public void itCanUploadFileWithNonAsciiCharacter() {
        BatchUploadManager batchUploadManager = nuxeoClient.batchUploadManager();
        BatchUpload batchUpload = batchUploadManager.createBatch();
        assertNotNull(batchUpload);
        File file = FileUtils.getResourceFileFromContext("sample.jpg");
        FileBlob fileBlob = new FileBlob(file, "Ümlaut.pdf");
        batchUpload = batchUpload.upload("1", fileBlob);
        assertNotNull(batchUpload);

        // Getting a doc and attaching the batch file
        Document doc = Document.createWithName("file", "File");
        doc.setPropertyValue("dc:title", "new title");
        doc = nuxeoClient.repository().createDocumentByPath("/", doc);
        assertNotNull(doc);
        doc.setPropertyValue("file:content", batchUpload.getBatchBlob());
        doc = doc.updateDocument();
        assertEquals("Ümlaut.pdf", doc.getPropertyValue("file:content/name"));

    }

}
