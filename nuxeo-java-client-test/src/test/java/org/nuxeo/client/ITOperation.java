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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.nuxeo.client.marshaller.NuxeoConverterFactory;
import org.nuxeo.client.objects.CustomJSONObject;
import org.nuxeo.client.objects.Document;
import org.nuxeo.client.objects.Documents;
import org.nuxeo.client.objects.Operation;
import org.nuxeo.client.objects.blob.Blobs;
import org.nuxeo.client.objects.blob.FileBlob;
import org.nuxeo.client.objects.blob.StreamBlob;
import org.nuxeo.client.objects.directory.DirectoryEntry;
import org.nuxeo.client.objects.operation.DocRef;
import org.nuxeo.client.objects.operation.DocRefs;
import org.nuxeo.client.spi.NuxeoClientException;
import org.nuxeo.common.utils.FileUtils;

/**
 * @since 0.1
 */
public class ITOperation extends AbstractITBase {

    public static final String FOLDER_2_FILE = "/folder_2/file";

    @Override
    public void init() {
        super.init();
        initDocuments();
    }

    @Test
    public void itCanExecuteOperationOnDocument() {
        Document result = nuxeoClient.operation("Repository.GetDocument").param("value", "/").execute();
        assertNotNull(result);
    }

    @Test
    public void itCanExecuteOperationOnDocuments() {
        Operation operation = nuxeoClient.operation("Repository.Query").param("query", "SELECT * FROM Document");
        Documents result = operation.execute();
        assertNotNull(result);
        assertTrue(result.getTotalSize() != 0);
    }

    @Test
    public void itCanExecuteOperationWithBlobs() throws IOException {
        // Get a blob
        Document result = nuxeoClient.operation("Repository.GetDocument").param("value", FOLDER_2_FILE).execute();
        FileBlob blob = nuxeoClient.operation("Document.GetBlob").input(result).execute();
        assertNotNull(blob);
        List<String> lines = Files.readAllLines(blob.getFile().toPath());
        assertEquals("[", lines.get(0));
        assertEquals("    \"fieldType\": \"string\",", lines.get(2));
        // Attach a blob
        File temp1 = FileUtils.getResourceFileFromContext("sample.jpg");
        FileBlob fileBlob = new FileBlob(temp1);
        int length = fileBlob.getLength();
        blob = nuxeoClient.operation("Blob.AttachOnDocument")
                          .param("document", FOLDER_2_FILE)
                          .input(fileBlob)
                          .execute();
        assertNotNull(blob);
        assertEquals("sample.jpg", blob.getFilename());
        assertEquals(length, blob.getLength());
        FileBlob resultBlob = nuxeoClient.operation("Document.GetBlob").input(FOLDER_2_FILE).execute();
        assertNotNull(resultBlob);
        assertEquals(length, resultBlob.getLength());
        // Attach a blobs and get them
        File temp2 = FileUtils.getResourceFileFromContext("sample.jpg");
        Blobs inputBlobs = new Blobs();
        inputBlobs.add(temp1);
        inputBlobs.add(temp2);
        Blobs blobs = nuxeoClient.operation("Blob.AttachOnDocument")
                                 .param("document", FOLDER_2_FILE)
                                 .param("xpath", "files:files")
                                 .input(inputBlobs)
                                 .execute();
        assertNotNull(blobs);
        assertEquals("sample.jpg", blobs.getBlobs().get(0).getFilename());
        assertEquals("sample.jpg", blobs.getBlobs().get(1).getFilename());
        Blobs resultBlobs = nuxeoClient.operation("Document.GetBlobs").input(FOLDER_2_FILE).execute();
        assertNotNull(resultBlobs);
        assertEquals(3, resultBlobs.size());
    }

    @Test
    public void itCanExecuteOperationWithStreamBlob() throws IOException {
        // Attach a blob
        File temp1 = FileUtils.getResourceFileFromContext("sample.jpg");
        int length = (int) temp1.length();
        StreamBlob byteBlob = new StreamBlob(new FileInputStream(temp1), "sample.jpg");
        FileBlob blob = nuxeoClient.operation("Blob.AttachOnDocument")
                                   .param("document", FOLDER_2_FILE)
                                   .input(byteBlob)
                                   .execute();
        assertNotNull(blob);
        assertEquals("sample.jpg", blob.getFilename());
        assertEquals(length, blob.getLength());

        FileBlob resultBlob = nuxeoClient.operation("Document.GetBlob").input(FOLDER_2_FILE).execute();
        assertNotNull(resultBlob);
        assertEquals(length, resultBlob.getLength());
    }

    /**
     * JAVACLIENT-101
     */
    @Test
    public void itCanExecuteOperationReturningEmptyBlobs() throws IOException {
        assumeTrue("itCanExecuteOperationReturningEmptyBlobs works only since Nuxeo 8.10",
                nuxeoClient.getServerVersion().isGreaterThan(NuxeoVersion.LTS_8_10));

        // Get blobs
        Blobs resultBlobs = nuxeoClient.operation("Document.GetBlobsByProperty").input(FOLDER_2_FILE).execute();
        assertNotNull(resultBlobs);
        assertTrue(resultBlobs.getBlobs().isEmpty());
    }

    @Test
    public void testMultiThread() throws InterruptedException {
        // TODO rework this test
        Thread t = new Thread(() -> {
            try {
                Document result = nuxeoClient.operation("Repository.GetDocument").param("value", "/").execute();
                assertNotNull(result);
            } catch (Exception e) {
            }
        });
        Thread t2 = new Thread(() -> {
            try {
                Document result = nuxeoClient.operation("Repository.GetDocument").param("value", "/").execute();
                assertNotNull(result);
            } catch (Exception e) {
            }
        });
        t.start();
        t2.start();
        t.join();
        t2.join();
    }

    @Test
    public void itCanExecuteOperationOnVoid() {
        try {
            nuxeoClient.operation("Log").param("message", "Error Log Test").param("level", "error").execute();
        } catch (NuxeoClientException reason) {
            fail("Void operation failing");
        }
    }

    @Test
    public void itCanExecuteOperationWithDocumentRefs() {
        Document result = nuxeoClient.operation("Repository.GetDocument").param("value", "/").execute();
        assertNotNull(result);
        DocRef doc = new DocRef(result.getId());
        result = nuxeoClient.operation("Document.Update").input(doc).param("properties", null).execute();
        assertNotNull(result);
        DocRefs docRefs = new DocRefs();
        docRefs.add(doc);
        Documents docs = nuxeoClient.operation("Document.Update").input(docRefs).param("properties", null).execute();
        assertNotNull(docs);
        assertEquals(1, docs.size());
    }

    @Test
    public void itCanFetchDirectoriesJsonBlob() throws IOException {
        String result = nuxeoClient.operation("Directory.Entries").param("directoryName", "continent").execute();
        List<Map<String, Serializable>> directoryExamples = nuxeoClient.getConverterFactory().readJSON(result,
                List.class, Map.class);
        assertNotNull(directoryExamples);
        assertEquals("europe", directoryExamples.get(0).get(DirectoryEntry.ID_PROPERTY));
    }

    @Test
    public void itCanFetchJSONBlob() {
        // register the entity
        NuxeoConverterFactory.registerEntity(CustomJSONObject.ENTITY_TYPE, CustomJSONObject.class);
        CustomJSONObject result = nuxeoClient.operation("CustomOperationJSONBlob").execute();
        assertNotNull(result);
        assertEquals("1", result.getUserId());
    }

}