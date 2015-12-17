/*
 * (C) Copyright 2015 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *          Nuxeo
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
import org.nuxeo.java.client.api.objects.Blob;
import org.nuxeo.java.client.api.objects.Document;
import org.nuxeo.java.client.api.objects.Documents;
import org.nuxeo.java.client.api.objects.blob.FileBlob;
import org.nuxeo.java.client.internals.spi.NuxeoClientException;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.Jetty;

import com.google.common.io.Files;

/**
 * @since 1.0
 */
@RunWith(FeaturesRunner.class)
@Features({ RestServerFeature.class })
@Jetty(port = 18090)
@RepositoryConfig(cleanup = Granularity.METHOD, init = RestServerInit.class)
public class OperationTest extends BaseTest {

    @Before
    public void authentication() {
        login();
    }

    @Test
    public void itCanExecuteOperationOnDocument() {
        Document result = (Document) nuxeoClient.automation().param("value", "/").execute("Repository.GetDocument");
        assertNotNull(result);
    }

    @Test
    public void itCanExecuteOperationOnDocuments() {
        Documents result = (Documents) nuxeoClient.automation()
                                                  .param("query", "SELECT * " + "FROM Document")
                                                  .execute("Repository.Query");
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
}