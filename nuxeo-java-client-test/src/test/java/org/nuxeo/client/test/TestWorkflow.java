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
package org.nuxeo.client.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.client.api.objects.workflow.Graph;
import org.nuxeo.client.api.objects.workflow.Workflow;
import org.nuxeo.client.api.objects.workflow.Workflows;
import org.nuxeo.client.internals.spi.NuxeoClientException;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.platform.routing.test.WorkflowFeature;
import org.nuxeo.ecm.restapi.test.RestServerFeature;
import org.nuxeo.ecm.restapi.test.RestServerInit;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.Jetty;

/**
 * @since 0.1
 */
@RunWith(FeaturesRunner.class)
@Features({ RestServerFeature.class, WorkflowFeature.class })
@Deploy({ "org.nuxeo.ecm.platform.restapi.server.routing", "org.nuxeo.ecm.platform.routing.default",
        "org.nuxeo.ecm.platform.filemanager.api", "org.nuxeo.ecm.platform.filemanager.core" })
@Jetty(port = 18090)
@RepositoryConfig(cleanup = Granularity.METHOD, init = RestServerInit.class)
public class TestWorkflow extends TestBase {

    @Before
    public void authentication() {
        login();
    }

    @Test
    public void itCanFetchWorkflowInstances() {
        nuxeoClient.repository().startWorkflowInstanceWithDocPath("/", getWorkflowModel());
        Workflows workflows = nuxeoClient.fetchCurrentUser().fetchWorkflowInstances();
        assertNotNull(workflows);
        assertTrue(workflows.getWorkflows().size() != 0);
    }

    @Test
    public void itCanFetchDocWorflowInstances() {
        nuxeoClient.repository().fetchDocumentRoot().startWorkflowInstance(getWorkflowModel());
        Workflows workflows = nuxeoClient.repository().fetchDocumentRoot().fetchWorkflowInstances();
        assertNotNull(workflows);
    }

    @Test
    public void itCanFetchWorkflowGraph() {
        Graph graph = nuxeoClient.repository().fetchWorkflowModelGraph(getWorkflowModel().getWorkflowModelName());
        assertNotNull(graph);
    }

    @Test
    public void itCanCancelWorkflow() {
        Workflow workflow = nuxeoClient.repository().fetchDocumentRoot().startWorkflowInstance(getWorkflowModel());
        nuxeoClient.repository().cancelWorkflowInstance(workflow.getId());
        try {
            nuxeoClient.repository().cancelWorkflowInstance(workflow.getId());
            fail("Should fail: wf instance already cancelled");
        } catch (NuxeoClientException reason) {
            assertEquals(500, reason.getStatus());
        }
    }

    protected Workflow getWorkflowModel() {
        Workflows workflows = nuxeoClient.repository().fetchWorkflowModels();
        return workflows.get(0);
    }
}