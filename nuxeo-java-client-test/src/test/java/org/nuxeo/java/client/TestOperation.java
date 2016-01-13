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
package org.nuxeo.java.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.restapi.test.RestServerFeature;
import org.nuxeo.ecm.restapi.test.RestServerInit;
import org.nuxeo.java.client.api.objects.Document;
import org.nuxeo.java.client.api.objects.Documents;
import org.nuxeo.java.client.api.objects.Operation;
import org.nuxeo.java.client.api.objects.blob.Blob;
import org.nuxeo.java.client.api.objects.blob.FileBlob;
import org.nuxeo.java.client.api.objects.operation.DocRef;
import org.nuxeo.java.client.api.objects.operation.DocRefs;
import org.nuxeo.java.client.internals.spi.NuxeoClientException;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.Jetty;

import com.google.common.io.Files;

/**
 * @since 0.1
 */
@RunWith(FeaturesRunner.class)
@Features({ RestServerFeature.class })
@Jetty(port = 18090)
@RepositoryConfig(cleanup = Granularity.METHOD, init = RestServerInit.class)
public class TestOperation extends TestBase {

    @Before
    public void authentication() {
        login();
    }

    @Test
    public void itCanExecuteOperationOnDocument() {
        Document result = (Document) nuxeoClient.automation().param("value",
                "/").execute("Repository.GetDocument");
        assertNotNull(result);
    }

    @Test
    public void itCanExecuteOperationOnDocuments() {
        Operation operation = nuxeoClient.automation("Repository.Query")
                                                  .param("query", "SELECT * " + "FROM Document");
        Documents result = (Documents) operation.execute();
        assertNotNull(result);
        assertTrue(result.getTotalSize() != 0);
    }

    @Test
    public void itCanExecuteOperationOnBlob() throws IOException {
        Document result = (Document) nuxeoClient.automation()
                                                .param("value", "/folder_2/file")
                                                .execute("Repository.GetDocument");
        Blob blob = (Blob) nuxeoClient.automation().input(result).execute("Document.GetBlob");
        assertNotNull(blob);
        List<String> lines = Files.readLines(((FileBlob) blob).getFile(), Charset.defaultCharset());
        assertEquals("[", lines.get(0));
        assertEquals("    \"fieldType\": \"string\",", lines.get(2));
    }

    @Ignore("JAVACLIENT-31")
    @Test
    public void itCanExecuteOperationOnBlobs() {
        FileBlob fileBlob = new FileBlob(new File("tmp"));
        Blob blob = (Blob) nuxeoClient.automation()
                                      .newRequest("Blob.AttachOnDocument")
                                      .param("xpath", "files:files")
                                      .param("doc", "/folder_2/file")
                                      .input(fileBlob)
                                      .execute();
        assertNotNull(blob);
        List<Blob> blobs = (List<Blob>) nuxeoClient.automation().input("/folder_2/file").execute("Document.GetBlobs");
        assertNotNull(blobs);
    }

    @Test
    public void itCanExecuteOperationOnVoid() {
        try {
            nuxeoClient.automation()
                       .newRequest("Log")
                       .param("message", "Error Log Test")
                       .param("level", "error")
                       .execute();
        } catch (NuxeoClientException reason) {
            fail("Void operation failing");
        }
    }

    // FIXME
    @Ignore
    @Test
    public void itCanExecuteOperationWithDocumentRefs() {
        Document result = (Document) nuxeoClient.automation().param("value", "/").execute("Repository.GetDocument");
        assertNotNull(result);
        DocRefs docRefs = new DocRefs();
        docRefs.addDoc(new DocRef(result.getId()));
        result = (Document) nuxeoClient.automation().input(docRefs).param("properties", null).execute("Document.Update");
        assertNotNull(result);
    }
}