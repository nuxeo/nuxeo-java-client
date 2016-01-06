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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.common.utils.FileUtils;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.restapi.test.RestServerFeature;
import org.nuxeo.ecm.restapi.test.RestServerInit;
import org.nuxeo.java.client.api.objects.upload.BatchUpload;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.Jetty;
import org.nuxeo.transientstore.test.TransientStoreFeature;

import java.io.File;

/**
 * @since 1.0
 */
@RunWith(FeaturesRunner.class)
@Features({ TransientStoreFeature.class, RestServerFeature.class })
@Jetty(port = 18090)
@RepositoryConfig(cleanup = Granularity.METHOD, init = RestServerInit.class)
public class TestUpload extends TestBase {

    @Before
    public void authentication() {
        login();
    }

    @Test
    public void itCanManageBatch() {
        BatchUpload batchUpload = nuxeoClient.fetchUploadManager();
        assertNotNull(batchUpload);
        assertNotNull(batchUpload.getBatchId());
        batchUpload = batchUpload.cancel(batchUpload.getBatchId());
        assertTrue(Boolean.parseBoolean(batchUpload.getDropped()));
    }

    @Test
    public void itCanUploadFile() {
        BatchUpload batchUpload = nuxeoClient.fetchUploadManager();
        assertNotNull(batchUpload);
        assertNotNull(batchUpload.getBatchId());
        File file = FileUtils.getResourceFileFromContext("blob.json");
        batchUpload = batchUpload.upload(file.getName(), file.length(), "json", batchUpload.getBatchId(), "1", file);
        assertNotNull(batchUpload);
        assertEquals("normal", batchUpload.getUploadType());
        //assertEquals(file.length(), batch.getUploadedSize());
    }
}