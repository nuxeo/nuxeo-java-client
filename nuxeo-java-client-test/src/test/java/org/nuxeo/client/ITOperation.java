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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;
import static org.nuxeo.client.Operations.BLOB_ATTACH_ON_DOCUMENT;
import static org.nuxeo.client.Operations.DIRECTORY_ENTRIES;
import static org.nuxeo.client.Operations.DOCUMENT_GET_BLOB;
import static org.nuxeo.client.Operations.DOCUMENT_GET_BLOBS;
import static org.nuxeo.client.Operations.DOCUMENT_GET_BLOBS_BY_PROPERTY;
import static org.nuxeo.client.Operations.DOCUMENT_UPDATE;
import static org.nuxeo.client.Operations.REPOSITORY_GET_DOCUMENT;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.nuxeo.client.marshaller.NuxeoConverterFactory;
import org.nuxeo.client.objects.CustomJSONObject;
import org.nuxeo.client.objects.Document;
import org.nuxeo.client.objects.Documents;
import org.nuxeo.client.objects.Operation;
import org.nuxeo.client.objects.StringEntity;
import org.nuxeo.client.objects.blob.Blob;
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

    @Override
    public void init() {
        super.init();
        initDocuments();
    }

    @Test
    public void itCanExecuteOperationOnDocument() {
        Document result = nuxeoClient.operation(REPOSITORY_GET_DOCUMENT).param("value", "/").execute();
        assertNotNull(result);
    }

    @Test
    public void itCanExecuteOperationOnDocuments() {
        Operation operation = nuxeoClient.operation("Repository.Query").param("query", "SELECT * FROM Document");
        Documents result = operation.execute();
        assertNotNull(result);
        assertTrue(result.getTotalSize() != 0);
    }

    /**
     * @deprecated since 3.1
     */
    @Test
    @Deprecated
    public void itCanBeBackwardCompatible() {
        // Get a blob
        Document result = nuxeoClient.operation(REPOSITORY_GET_DOCUMENT).param("value", FOLDER_2_FILE).execute();
        FileBlob blob = nuxeoClient.operation(DOCUMENT_GET_BLOB).input(result).execute();
        assertNotNull(blob);
        // convert blob to a real File
        assertNotNull(blob.getFile());
        // then assert content which assert stream switch
        assertContentEquals("blob.json", blob);
    }

    @Test
    public void itCanExecuteOperationWithBlobs() {
        // Get a blob
        Document result = nuxeoClient.operation(REPOSITORY_GET_DOCUMENT).param("value", FOLDER_2_FILE).execute();
        Blob blob = nuxeoClient.operation(DOCUMENT_GET_BLOB).input(result).execute();
        assertNotNull(blob);
        assertContentEquals("blob.json", blob);
        // Attach a blob
        File temp1 = FileUtils.getResourceFileFromContext("sample.jpg");
        FileBlob fileBlob = new FileBlob(temp1);
        long length = fileBlob.getContentLength();
        // Execute with void header
        Void aVoid = nuxeoClient.operation(BLOB_ATTACH_ON_DOCUMENT)
                                .voidOperation(true)
                                .param("document", FOLDER_2_FILE)
                                .input(fileBlob)
                                .execute();
        assertNull(aVoid);

        blob = nuxeoClient.operation(DOCUMENT_GET_BLOB).input(FOLDER_2_FILE).execute();
        assertNotNull(blob);
        assertEquals(length, blob.getContentLength());
        assertContentEquals("sample.jpg", blob);

        // Attach a blobs and get them
        File temp2 = FileUtils.getResourceFileFromContext("sample.jpg");
        Blobs inputBlobs = new Blobs();
        inputBlobs.addEntry(new FileBlob(temp1));
        inputBlobs.addEntry(new FileBlob(temp2));
        // Execute with void header
        aVoid = nuxeoClient.operation(BLOB_ATTACH_ON_DOCUMENT)
                           .voidOperation(true)
                           .param("document", FOLDER_2_FILE)
                           .param("xpath", "files:files")
                           .input(inputBlobs)
                           .execute();
        assertNull(aVoid);

        Blobs resultBlobs = nuxeoClient.operation(DOCUMENT_GET_BLOBS).input(FOLDER_2_FILE).execute();
        assertNotNull(resultBlobs);
        assertEquals(3, resultBlobs.size());
        resultBlobs.getBlobs().forEach(b -> assertContentEquals("sample.jpg", b));
    }

    @Test
    public void itCanExecuteOperationWithStreamBlob() throws IOException {
        // Attach a blob
        File temp1 = FileUtils.getResourceFileFromContext("sample.jpg");
        long length = temp1.length();
        StreamBlob byteBlob = new StreamBlob(new FileInputStream(temp1), "sample.jpg");
        Void aVoid = nuxeoClient.operation(BLOB_ATTACH_ON_DOCUMENT)
                                .voidOperation(true)
                                .param("document", FOLDER_2_FILE)
                                .input(byteBlob)
                                .execute();
        assertNull(aVoid);

        Blob blob = nuxeoClient.operation(DOCUMENT_GET_BLOB).input(FOLDER_2_FILE).execute();
        assertNotNull(blob);
        assertEquals("sample.jpg", blob.getFilename());
        assertEquals(length, blob.getContentLength());
        assertContentEquals("sample.jpg", blob);
    }

    /**
     * JAVACLIENT-101
     */
    @Test
    public void itCanExecuteOperationReturningEmptyBlobs() {
        assumeTrue("itCanExecuteOperationReturningEmptyBlobs works only since Nuxeo 8.10",
                nuxeoClient.getServerVersion().isGreaterThan(NuxeoVersion.LTS_8_10));

        // Get blobs
        Blobs resultBlobs = nuxeoClient.operation(DOCUMENT_GET_BLOBS_BY_PROPERTY).input(FOLDER_2_FILE).execute();
        assertNotNull(resultBlobs);
        assertTrue(resultBlobs.getBlobs().isEmpty());
    }

    @Test
    public void testMultiThread() throws InterruptedException {
        // TODO rework this test
        Thread t = new Thread(() -> {
            try {
                Document result = nuxeoClient.operation(REPOSITORY_GET_DOCUMENT).param("value", "/").execute();
                assertNotNull(result);
            } catch (Exception e) {
            }
        });
        Thread t2 = new Thread(() -> {
            try {
                Document result = nuxeoClient.operation(REPOSITORY_GET_DOCUMENT).param("value", "/").execute();
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
        Document result = nuxeoClient.operation(REPOSITORY_GET_DOCUMENT).param("value", "/").execute();
        assertNotNull(result);
        DocRef doc = new DocRef(result.getId());
        result = nuxeoClient.operation(DOCUMENT_UPDATE).input(doc).param("properties", null).execute();
        assertNotNull(result);
        DocRefs docRefs = new DocRefs();
        docRefs.add(doc);
        Documents docs = nuxeoClient.operation(DOCUMENT_UPDATE).input(docRefs).param("properties", null).execute();
        assertNotNull(docs);
        assertEquals(1, docs.size());
    }

    @Test
    public void itCanFetchDirectoriesJsonBlob() {
        String result = nuxeoClient.operation(DIRECTORY_ENTRIES).param("directoryName", "continent").execute();
        List<Map<String, Serializable>> directoryExamples = nuxeoClient.getConverterFactory()
                                                                       .readJSON(result, List.class, Map.class);
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

    @Test
    public void itCanFetchString() {
        StringEntity result = nuxeoClient.operation("Scripting.TestString").execute();
        assertNotNull(result);
        assertEquals("Hello test!", result.getValue());
    }
}
