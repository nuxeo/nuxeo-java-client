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

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.nuxeo.client.objects.Document;
import org.nuxeo.client.objects.task.Task;
import org.nuxeo.client.objects.task.TaskCompletionRequest;
import org.nuxeo.client.objects.task.TaskInfo;
import org.nuxeo.client.objects.task.TaskInfo.TaskInfoItem;
import org.nuxeo.client.objects.task.Tasks;
import org.nuxeo.client.objects.workflow.Graph;
import org.nuxeo.client.objects.workflow.Workflow;
import org.nuxeo.client.objects.workflow.Workflows;
import org.nuxeo.client.spi.NuxeoClientRemoteException;

/**
 * @since 0.1
 */
public class ITWorkflowAndTask extends AbstractITBase {

    private Document document;

    private Workflow serialWorkflow;

    @Override
    public void init() {
        super.init();
        // Create a note
        document = Document.createWithName("note", "Note");
        document = nuxeoClient.repository().createDocumentByPath("/", document);
        // Fetch serial workflow model
        serialWorkflow = nuxeoClient.repository().fetchWorkflowModel("SerialDocumentReview");
    }

    @Test
    public void itCanStartWorkflowInstance() {
        Workflow workflow = document.startWorkflowInstance(serialWorkflow);
        assertNotNull(workflow);
        assertEquals("running", workflow.getState());
        // TODO check why we have this complexity to retrieve doc ids
        assertEquals(document.getId(), workflow.getAttachedDocumentIds().get(0));
    }

    @Test
    public void itCanStartWorkflowInstanceWithDocId() {
        Workflow workflow = nuxeoClient.repository().startWorkflowInstanceWithDocId(document.getId(), serialWorkflow);
        assertNotNull(workflow);
        assertEquals("running", workflow.getState());
        // TODO check why we have this complexity to retrieve doc ids
        assertEquals(document.getId(), workflow.getAttachedDocumentIds().get(0));
    }

    @Test
    public void itCanStartWorkflowInstanceWithDocPath() {
        Workflow workflow = nuxeoClient.repository().startWorkflowInstanceWithDocPath(document.getPath(),
                serialWorkflow);
        assertNotNull(workflow);
        assertEquals("running", workflow.getState());
        // TODO check why we have this complexity to retrieve doc ids
        assertEquals(document.getId(), workflow.getAttachedDocumentIds().get(0));
    }

    @Test
    public void itCanFetchWorkflowInstances() {
        nuxeoClient.repository().startWorkflowInstanceWithDocPath(document.getPath(), serialWorkflow);
        Workflows workflowInstances = nuxeoClient.userManager().fetchWorkflowInstances();
        assertNotNull(workflowInstances);
        assertTrue(workflowInstances.size() != 0);
    }

    @Test
    public void itCanFetchDocWorflowInstances() {
        nuxeoClient.repository().fetchDocumentRoot().startWorkflowInstance(serialWorkflow);
        Workflows workflowInstances = nuxeoClient.repository().fetchDocumentRoot().fetchWorkflowInstances();
        assertNotNull(workflowInstances);
    }

    @Test
    public void itCanFetchWorkflowGraph() {
        Graph graph = nuxeoClient.repository().fetchWorkflowModelGraph(serialWorkflow.getWorkflowModelName());
        assertNotNull(graph);
    }

    @Test
    public void itCanCancelWorkflow() {
        Workflow workflow = nuxeoClient.repository().fetchDocumentRoot().startWorkflowInstance(serialWorkflow);
        nuxeoClient.repository().cancelWorkflowInstance(workflow.getId());
        try {
            nuxeoClient.repository().cancelWorkflowInstance(workflow.getId());
            fail("Should fail: wf instance already cancelled");
        } catch (NuxeoClientRemoteException reason) {
            int expectedStatus = 500;
            if (nuxeoClient.getServerVersion().isGreaterThan(NuxeoVersion.LTS_10_10.hotfix(30))) {
                expectedStatus = 400;
            }
            assertEquals(expectedStatus, reason.getStatus());
        }
    }

    @Test
    public void itCanFetchAllTasksFromWFAndUser() {
        Tasks tasks = fetchAllTasks();
        assertNotNull(tasks);
        assertTrue(tasks.size() != 0);
    }

    @Test
    public void itCanFetchTask() {
        Task task = fetchAllTasks().getEntry(0);
        String name = task.getName();
        task = nuxeoClient.taskManager().fetchTask(task.getId());
        assertNotNull(task);
        assertEquals(name, task.getName());
    }

    @Test
    public void itCanFetchTaskFromDoc() {
        Task task = fetchAllTasks().getEntry(0);
        Document target = nuxeoClient.repository().fetchDocumentById(task.getTargetDocumentIds().get(0));
        Tasks tasks = target.fetchTasks();
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        assertEquals(task.getId(), tasks.getEntry(0).getId());
        assertEquals(task.getWorkflowInstanceId(), tasks.getEntry(0).getWorkflowInstanceId());
    }

    @Test
    public void itCanFetchTaskInfo() {
        Task task = fetchAllTasks().getEntry(0);
        TaskInfo taskInfo = task.getTaskInfo();
        assertNotNull(taskInfo);
        List<TaskInfoItem> taskActions = taskInfo.getTaskActions();
        assertEquals(2, taskActions.size());
        assertEquals("cancel", taskActions.get(0).getName());
        assertEquals("start_review", taskActions.get(1).getName());

    }

    @Test
    public void itCanCompleteATask() {
        Task task = fetchAllTasks().getEntry(0);
        TaskCompletionRequest taskCompletionRequest = new TaskCompletionRequest();
        taskCompletionRequest.setComment("Please review");
        taskCompletionRequest.setVariables(singletonMap("participants", singletonList("user:Administrator")));
        task = nuxeoClient.taskManager().complete(task.getId(), "start_review", taskCompletionRequest);
        assertNotNull(task);
        assertEquals("ended", task.getState());
        assertEquals(1, task.getComments().size());
        assertEquals("Please review", task.getComments().get(0).getText());
    }

    @Ignore("JAVACLIENT-81")
    @Test
    public void itCanDelegate() {
        Task task = fetchAllTasks().getEntry(0);
        String name = task.getName();
        task = nuxeoClient.taskManager().delegate(task.getId(), "Administrator", "some comment");
        assertNotNull(task);
        assertEquals(name, task.getName());
    }

    @Test
    public void itCanReAssign() {
        Task task = fetchAllTasks().getEntry(0);
        try {
            nuxeoClient.taskManager().reassign(task.getId(), "Administrator", "some comment");
            fail("Should fail: not possible to reassign this task");
        } catch (NuxeoClientRemoteException reason) {
            assertEquals(500, reason.getStatus());
        }
    }

    protected Tasks fetchAllTasks() {
        nuxeoClient.repository().fetchDocumentRoot().startWorkflowInstance(serialWorkflow);
        Workflows workflows = nuxeoClient.userManager().fetchWorkflowInstances();
        Workflow workflow = workflows.getEntry(0);
        return nuxeoClient.taskManager().fetchTasks("Administrator", workflow.getId(), workflow.getWorkflowModelName());
    }

}
