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
 *     Mincong Huang <mhuang@nuxeo.com>
 *     Kevin Leturc <kleturc@nuxeo.com>
 */
package org.nuxeo.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.junit.Test;
import org.nuxeo.client.cache.ResultCacheInMemory;
import org.nuxeo.client.objects.DataSet;
import org.nuxeo.client.objects.Document;
import org.nuxeo.client.objects.Documents;
import org.nuxeo.client.objects.Field;
import org.nuxeo.client.objects.RecordSet;
import org.nuxeo.client.objects.acl.ACE;
import org.nuxeo.client.objects.acl.ACP;
import org.nuxeo.client.objects.annotation.Annotation;
import org.nuxeo.client.objects.annotation.Annotations;
import org.nuxeo.client.objects.audit.Audit;
import org.nuxeo.client.objects.audit.LogEntry;
import org.nuxeo.client.objects.blob.Blob;
import org.nuxeo.client.objects.blob.Blobs;
import org.nuxeo.client.objects.blob.FileBlob;
import org.nuxeo.client.objects.user.User;
import org.nuxeo.client.spi.NuxeoClientRemoteException;
import org.nuxeo.common.utils.FileUtils;

/**
 * @since 0.1
 */
public class ITRepository extends AbstractITBase {

    @Override
    public void init() {
        super.init();
        initDocuments();
    }

    @Test
    public void itCanFetchFolder() {
        Document root = nuxeoClient.repository().fetchDocumentRoot();
        Document folder = nuxeoClient.repository().fetchDocumentByPath("/folder_2");
        assertNotNull(folder);
        assertEquals("Folder", folder.getType());
        assertEquals("document", folder.getEntityType());
        assertEquals(root.getUid(), folder.getParentRef());
        assertEquals("/folder_2", folder.getPath());
        assertEquals("Folder 2", folder.getTitle());
    }

    @Test
    public void itCanFetchFolderWithRepositoryName() {
        Document root = nuxeoClient.repository().fetchDocumentRoot();
        Document folder = nuxeoClient.repository(root.getRepositoryName()).fetchDocumentByPath("/folder_2");
        assertNotNull(folder);
        assertEquals("Folder", folder.getType());
        assertEquals("document", folder.getEntityType());
        assertEquals(root.getUid(), folder.getParentRef());
        assertEquals("/folder_2", folder.getPath());
        assertEquals("Folder 2", folder.getTitle());
    }

    @Test
    public void itCanFetchNote() {
        Document note = nuxeoClient.repository().fetchDocumentByPath("/folder_1/note_0");
        assertNotNull(note);
        assertEquals("Note", note.getType());
        assertEquals("document", note.getEntityType());

        assertEquals("/folder_1/note_0", note.getPath());
        assertEquals("Note 0", note.getTitle());
    }

    @Test
    public void itCanFetchSpecifSchema() {
        // Fetch only dublincore schema
        nuxeoClient.schemas("dublincore");
        Document note = nuxeoClient.repository().fetchDocumentByPath("/folder_1/note_0");
        assertNotNull(note);
        assertEquals("Note 0", note.getPropertyValue("dc:title"));
        assertNull(note.getPropertyValue("note:note"));

        // Fetch only note schema
        nuxeoClient.schemas("note");
        note = nuxeoClient.repository().fetchDocumentByPath("/folder_1/note_0");
        assertNotNull(note);
        assertNull(note.getPropertyValue("dc:title"));
        assertEquals("Note 0", note.getPropertyValue("note:note"));

        // Fetch several schemas
        nuxeoClient.schemas("dublincore", "note");
        note = nuxeoClient.repository().fetchDocumentByPath("/folder_1/note_0");
        assertNotNull(note);
        assertEquals("Note 0", note.getPropertyValue("dc:title"));
        assertEquals("Note 0", note.getPropertyValue("note:note"));
    }

    @Test
    public void itCanFetchSpecificProperty() {
        nuxeoClient.fetchPropertiesForDocument("dc:creator");
        Document note = nuxeoClient.repository().fetchDocumentByPath("/folder_1/note_0");
        assertNotNull(note);
        assertEquals("Note 0", note.getPropertyValue("dc:title"));
        // it should be a user
        assertTrue(note.getPropertyValue("dc:creator") instanceof User);
        User user = note.getPropertyValue("dc:creator");
        assertEquals("Administrator", user.getUserName());
    }

    @Test
    public void itCanQuery() {
        Documents documents = nuxeoClient.repository().query("SELECT * From Note WHERE ecm:isVersion = 0");
        assertEquals(1, documents.getDocuments().size());
        Document document = documents.getDocuments().get(0);
        assertEquals("Note", document.getType());
        assertEquals("default", document.getRepositoryName());
        assertEquals("project", document.getState());
        assertEquals("Note 0", document.getTitle());
    }

    @Test
    public void itCanUseCaching() {
        // Re-build a client with cache
        NuxeoClient client = ITBase.createClientBuilder().cache(new ResultCacheInMemory()).connect().schemas("*");
        // Retrieve a document from query
        Document document = client.repository().fetchDocumentByPath("/folder_1/note_0");
        assertEquals("Note 0", document.getPropertyValue("dc:title"));
        assertEquals(1, client.getNuxeoCache().size());

        // Update this document
        Document documentUpdated = Document.createWithId(document.getId(), document.getType());
        documentUpdated.setPropertyValue("dc:title", "note updated");
        documentUpdated = client.repository().updateDocument(documentUpdated);
        assertEquals("note updated", documentUpdated.getPropertyValue("dc:title"));

        // Update this document again (check update request doesn't go through cache - JAVACLIENT-138)
        documentUpdated = Document.createWithId(document.getId(), document.getType());
        documentUpdated.setPropertyValue("dc:title", "note updated again");
        documentUpdated = client.repository().updateDocument(documentUpdated);
        assertEquals("note updated again", documentUpdated.getPropertyValue("dc:title"));

        // Retrieve again this document within cache
        document = client.repository().fetchDocumentByPath("/folder_1/note_0");
        assertEquals("Note 0", document.getPropertyValue("dc:title"));
        assertEquals(1, client.getNuxeoCache().size());

        // Refresh the cache and check the update has been recovered.
        client.getNuxeoCache().invalidateAll();
        document = client.repository().fetchDocumentByPath("/folder_1/note_0");
        assertEquals("note updated again", document.getPropertyValue("dc:title"));
        assertEquals(1, client.getNuxeoCache().size());
    }

    @Test
    public void itCanUseQueriesAndResultSet() {
        RecordSet documents = nuxeoClient.operation("Repository.ResultSetQuery")
                                         .param("query", "SELECT * FROM Document")
                                         .execute();
        assertTrue(documents.getUuids().size() != 0);
    }

    @Test
    public void itCanFail() {
        try {
            nuxeoClient.repository().fetchDocumentByPath("/folder_1/wrong");
            fail("Should be not found");
        } catch (NuxeoClientRemoteException reason) {
            assertEquals(404, reason.getStatus());
        }
    }

    @Test
    public void itCanFetchBlob() {
        Document file = nuxeoClient.repository().fetchDocumentByPath("/folder_2/file");
        FileBlob blob = file.fetchBlob();
        assertNotNull(blob);
        assertEquals("blob.json", blob.getFilename());
        assertEquals("text/plain", blob.getMimeType());
    }

    @Test
    public void itCanFetchBlobByPath() {
        Document file = nuxeoClient.repository().fetchDocumentByPath("/folder_2/file");

        // Attach a blob
        File temp1 = FileUtils.getResourceFileFromContext("sample.jpg");
        File temp2 = FileUtils.getResourceFileFromContext("sample.jpg");
        Blobs inputBlobs = new Blobs();
        inputBlobs.add(temp1);
        inputBlobs.add(temp2);
        Blobs blobs = nuxeoClient.operation("Blob.AttachOnDocument")
                                 .param("document", file.getPath())
                                 .param("xpath", "files:files")
                                 .input(inputBlobs)
                                 .execute();
        assertNotNull(blobs);
        Blob blob0 = blobs.getBlobs().get(0);
        Blob blob1 = blobs.getBlobs().get(1);
        assertEquals("sample.jpg", blob0.getFilename());
        assertEquals("sample.jpg", blob1.getFilename());
        assertEquals("image/jpeg", blob0.getMimeType());
        assertEquals("image/jpeg", blob1.getMimeType());

        // Fetch blob by path
        FileBlob blob = nuxeoClient.repository().fetchBlobByPath(file.getPath(), "files:files/0/file");
        assertNotNull(blob);
    }

    @Test
    public void itCanFetchBlobById() {
        Document file = nuxeoClient.repository().fetchDocumentByPath("/folder_2/file");

        // Attach a blob
        File temp1 = FileUtils.getResourceFileFromContext("sample.jpg");
        File temp2 = FileUtils.getResourceFileFromContext("sample.jpg");
        Blobs inputBlobs = new Blobs();
        inputBlobs.add(temp1);
        inputBlobs.add(temp2);
        Blobs blobs = nuxeoClient.operation("Blob.AttachOnDocument")
                                 .param("document", file.getPath())
                                 .param("xpath", "files:files")
                                 .input(inputBlobs)
                                 .execute();
        assertNotNull(blobs);
        assertEquals("sample.jpg", blobs.getBlobs().get(0).getFilename());
        assertEquals("sample.jpg", blobs.getBlobs().get(1).getFilename());

        // Fetch blob by id
        FileBlob blob = nuxeoClient.repository().fetchBlobById(file.getUid(), "files:files/0/file");
        assertNotNull(blob);
    }

    @Test
    public void itCanFetchChildren() {
        Document folder = nuxeoClient.repository().fetchDocumentByPath("/folder_2");
        Documents children = folder.fetchChildren();
        assertTrue(children.size() != 0);
    }

    @Test
    public void itCanPlayWithChildren() {
        Document folder = nuxeoClient.repository().fetchDocumentByPath("/folder_2");
        Documents children = folder.fetchChildren();
        assertTrue(children.size() != 0);
        children = children.getDocument(0).fetchChildren();
        assertNotNull(children);
    }

    @Test
    public void itCanFetchACP() {
        Document folder = nuxeoClient.repository().fetchDocumentByPath("/folder_2");
        ACP acp = folder.fetchPermissions();
        assertTrue(acp.getAcls().size() != 0);
        assertEquals("inherited", acp.getAcls().get(0).getName());
        assertEquals("Administrator", acp.getAcls().get(0).getAces().get(0).getUsername());
    }

    @Test
    public void itCanManagePermissions() {
        // Create a user
        User user = nuxeoClient.userManager().createUser(ITBase.createUser());
        // First Check
        Document folder = nuxeoClient.repository().fetchDocumentByPath("/folder_2");
        ACP acp = folder.fetchPermissions();
        assertTrue(acp.getAcls().size() != 0);
        assertEquals(1, acp.getAcls().size());
        assertEquals(2, acp.getAcls().get(0).getAces().size());
        assertEquals("inherited", acp.getAcls().get(0).getName());
        // Settings
        GregorianCalendar begin = new GregorianCalendar(2015, Calendar.JUNE, 20, 12, 34, 56);
        GregorianCalendar end = new GregorianCalendar(2015, Calendar.JULY, 14, 12, 34, 56);
        ACE ace = new ACE();
        ace.setUsername(user.getUserName());
        ace.setPermission("Write");
        ace.setCreator("Administrator");
        ace.setBegin(begin);
        ace.setEnd(end);
        ace.setBlockInheritance(true);
        folder.addPermission(ace);
        // Final Check
        folder = nuxeoClient.repository().fetchDocumentByPath("/folder_2");
        acp = folder.fetchPermissions();
        assertTrue(acp.getAcls().size() != 0);
        assertEquals(1, acp.getAcls().size());
        assertEquals(4, acp.getAcls().get(0).getAces().size());
        assertEquals("local", acp.getAcls().get(0).getName());
        // ** DELETION **/
        folder.removePermission(user.getUserName());
        // Final Check
        folder = nuxeoClient.repository().fetchDocumentByPath("/folder_2");
        acp = folder.fetchPermissions();
        assertTrue(acp.getAcls().size() != 0);
        assertEquals(1, acp.getAcls().size());
        assertEquals(3, acp.getAcls().get(0).getAces().size());
        assertEquals("local", acp.getAcls().get(0).getName());
    }

    @Test
    public void itCanFetchAudit() {
        Document root = nuxeoClient.repository().fetchDocumentRoot();
        Audit audit = root.fetchAudit();
        assertFalse(audit.getLogEntries().isEmpty());
        List<String> categories = audit.getLogEntries().stream().map(LogEntry::getCategory).collect(
                Collectors.toList());
        assertTrue(categories.contains("eventDocumentCategory"));
    }

    @Test
    public void testMultiThread() throws InterruptedException {
        // TODO rework this test
        Thread t = new Thread(() -> {
            try {
                RecordSet documents = nuxeoClient.operation("Repository.ResultSetQuery")
                                                 .param("query", "SELECT * FROM Document")
                                                 .execute();
                assertTrue(documents.getUuids().size() != 0);
            } catch (Exception e) {
            }
        });
        Thread t2 = new Thread(() -> {
            try {
                RecordSet documents = nuxeoClient.operation("Repository.ResultSetQuery")
                                                 .param("query", "SELECT * FROM Document")
                                                 .execute();
                assertTrue(documents.getUuids().size() != 0);
            } catch (Exception e) {
            }
        });
        t.start();
        t2.start();
        t.join();
        t2.join();
    }

    @Test
    public void itCanFetchDocumentWithCallback() throws Exception {
        WaitCallback<Document> callback = new WaitCallback<>();
        nuxeoClient.repository().fetchDocumentByPath("/folder_2", callback);
        Document document = callback.waitForResponse();
        assertEquals("Folder", document.getType());
        assertEquals("document", document.getEntityType());
        assertEquals("/folder_2", document.getPath());
        assertEquals("Folder 2", document.getTitle());
    }

    @Test
    public void itCanUseEnrichers() {
        Document document = nuxeoClient.enrichersForDocument("acls", "breadcrumb").repository().fetchDocumentByPath(
                "/folder_2");
        assertNotNull(document);
        assertEquals(1, ((List) document.getContextParameters().get("acls")).size());
        assertEquals(1, document.<Documents> getContextParameter("breadcrumb").size());
    }

    /**
     * This test tests more aspect of enricher mechanism in nuxeo-java-client. In this tests, we will also test
     * deserialization of documents in breadcrumb and if they are correctly connected.
     */
    @Test
    public void itCanUseBreadcrumb() {
        // Test deserialization
        Document document = nuxeoClient.enrichersForDocument("breadcrumb").repository().fetchDocumentByPath(
                "/folder_2/file");
        assertNotNull(document);
        Documents documents = document.getContextParameter("breadcrumb");
        assertNotNull(documents);
        assertEquals(2, documents.size());
        Document folder2 = documents.getDocument(0);
        Document file = documents.getDocument(1);
        assertEquals("/folder_2", folder2.getPath());
        assertEquals("/folder_2/file", file.getPath());

        // Test connect
        Documents children = folder2.fetchChildren();
        assertNotNull(children);
        assertEquals(1, children.size());
        assertEquals("/folder_2/file", children.getDocument(0).getPath());
    }

    @Test
    public void itCanHandleGregorianCalendarUTC() {
        GregorianCalendar calendar = new GregorianCalendar(2017, Calendar.MAY, 4, 3, 2, 1);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        String calendarStr = formatter.format(calendar.getTime());
        assertEquals("2017-05-04T03:02:01.000Z", calendarStr);

        Document file = Document.createWithName("My Title", "File");
        file.setPropertyValue("dc:issued", calendarStr);
        file = nuxeoClient.repository().createDocumentByPath("/", file);
        assertEquals("2017-05-04T03:02:01.000Z", file.getPropertyValue("dc:issued"));

        calendar.add(Calendar.MONTH, 1);
        calendarStr = formatter.format(calendar.getTime());
        assertEquals("2017-06-04T03:02:01.000Z", calendarStr);
        file.setPropertyValue("dc:issued", calendarStr);
        file = nuxeoClient.repository().updateDocument(file);
        assertEquals("2017-06-04T03:02:01.000Z", file.getPropertyValue("dc:issued"));
    }

    @Test
    public void itCanHandleGregorianCalendarCET() {
        GregorianCalendar calendar = new GregorianCalendar(2017, Calendar.MAY, 4, 3, 2, 1);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        calendar.setTimeZone(TimeZone.getTimeZone("CET"));
        formatter.setTimeZone(TimeZone.getTimeZone("CET"));
        String calendarStr = formatter.format(calendar.getTime());
        assertEquals("2017-05-04T03:02:01.000+02:00", calendarStr);

        Document file = Document.createWithName("My Title", "File");
        file.setPropertyValue("dc:issued", calendarStr);
        file = nuxeoClient.repository().createDocumentByPath("/", file);
        assertEquals("2017-05-04T01:02:01.000Z", file.getPropertyValue("dc:issued"));

        calendar.add(Calendar.MONTH, 1);
        calendarStr = formatter.format(calendar.getTime());
        assertEquals("2017-06-04T03:02:01.000+02:00", calendarStr);
        file.setPropertyValue("dc:issued", calendarStr);
        file = nuxeoClient.repository().updateDocument(file);
        assertEquals("2017-06-04T01:02:01.000Z", file.getPropertyValue("dc:issued"));
    }

    @Test
    public void itCanHandleZonedDateTimeUTC() {
        ZonedDateTime dateTime = LocalDate.of(2017, Month.MAY, 4).atTime(3, 2, 1).atZone(ZoneId.of("UTC"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        String dateTimeStr = dateTime.format(formatter);
        assertEquals("2017-05-04T03:02:01.000Z", dateTimeStr);

        Document file = Document.createWithName("My Title", "File");
        file.setPropertyValue("dc:issued", dateTimeStr);
        file = nuxeoClient.repository().createDocumentByPath("/", file);
        assertEquals("File", file.getType());
        assertEquals("2017-05-04T03:02:01.000Z", file.getPropertyValue("dc:issued"));

        dateTime = dateTime.plus(1, ChronoUnit.MONTHS);
        file.setPropertyValue("dc:issued", dateTime.format(formatter));
        file = nuxeoClient.repository().updateDocument(file);
        assertEquals("2017-06-04T03:02:01.000Z", file.getPropertyValue("dc:issued"));
    }

    @Test
    public void itCanHandleZonedDateTimeCET() {
        ZonedDateTime dateTime = LocalDate.of(2017, Month.MAY, 4).atTime(3, 2, 1).atZone(ZoneId.of("CET"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        String dateTimeStr = dateTime.format(formatter);
        assertEquals("2017-05-04T03:02:01.000+02:00", dateTimeStr);

        Document file = Document.createWithName("My Title", "File");
        file.setPropertyValue("dc:issued", dateTimeStr);
        file = nuxeoClient.repository().createDocumentByPath("/", file);
        assertEquals("File", file.getType());
        assertEquals("2017-05-04T01:02:01.000Z", file.getPropertyValue("dc:issued"));

        dateTime = dateTime.plus(1, ChronoUnit.MONTHS);
        file.setPropertyValue("dc:issued", dateTime.format(formatter));
        file = nuxeoClient.repository().updateDocument(file);
        assertEquals("2017-06-04T01:02:01.000Z", file.getPropertyValue("dc:issued"));
    }

    /**
     * Dates can only be handled as {@link java.lang.String}. Otherwise, errorBody should be raised. There're several
     * examples showing how to convert date correctly into ISO 8601 format:
     * <ul>
     * <li>Convert {@link java.util.GregorianCalendar} in UTC, see {@link #itCanHandleGregorianCalendarUTC}.
     * <li>Convert {@link java.util.GregorianCalendar} in CET, see {@link #itCanHandleGregorianCalendarCET}.
     * <li>Convert {@link java.time.ZonedDateTime} in UTC, see {@link #itCanHandleZonedDateTimeCET}.
     * <li>Convert {@link java.time.ZonedDateTime} in CET, see {@link #itCanHandleZonedDateTimeCET}.
     * </ul>
     */
    @Test
    public void itCannotHandleDateByDefault() {
        Document file = Document.createWithName("My Title", "File");
        String expectedMsg1 = buildErrorMsgForDate("dc:issued", GregorianCalendar.class);
        String expectedMsg2 = buildErrorMsgForDate("dc:issued", Date.class);

        GregorianCalendar dateType1 = new GregorianCalendar();
        Date dateType2 = new Date(System.currentTimeMillis());

        assertExceptionFor(doc -> doc.setPropertyValue("dc:issued", dateType1), file, expectedMsg1);
        assertExceptionFor(doc -> doc.setPropertyValue("dc:issued", dateType2), file, expectedMsg2);
    }

    @Test
    public void itCannotHandlePropsIfDateFound() {
        Document file = Document.createWithName("My Title", "File");
        Map<String, Object> props = new HashMap<>();
        props.put("dc:issued", new GregorianCalendar());
        String expectedMsg = buildErrorMsgForDate("dc:issued", GregorianCalendar.class);

        assertExceptionFor(doc -> doc.setProperties(props), file, expectedMsg);
        assertExceptionFor(doc -> doc.setDirtyProperties(props), file, expectedMsg);
    }

    @Test
    public void itCannotHandlePropsIfDateArrayFound() {
        Document file = Document.createWithName("My Title", "File");
        Map<String, Object> props = new HashMap<>();
        props.put("sth:dateArray", new Object[] { "unused", new GregorianCalendar() });
        String expectedMsg = buildErrorMsgForDate("sth:dateArray", GregorianCalendar.class);

        assertExceptionFor(doc -> doc.setProperties(props), file, expectedMsg);
        assertExceptionFor(doc -> doc.setDirtyProperties(props), file, expectedMsg);
    }

    @Test
    public void itCannotHandlePropsIfDateListFound() {
        Document file = Document.createWithName("My Title", "File");
        Map<String, Object> props = new HashMap<>();
        props.put("sth:dateList", Arrays.asList(new GregorianCalendar(), new GregorianCalendar()));
        String expectedMsg = buildErrorMsgForDate("sth:dateList", GregorianCalendar.class);

        assertExceptionFor(doc -> doc.setProperties(props), file, expectedMsg);
        assertExceptionFor(doc -> doc.setDirtyProperties(props), file, expectedMsg);
    }

    @Test
    public void itCannotHandlePropsIfDateFoundInComplexProps() {
        Document file = Document.createWithName("My Title", "File");
        Map<String, Object> props = new HashMap<>();
        Map<String, Object> complexProps = new HashMap<>();

        complexProps.put("complex:date", new Date(System.currentTimeMillis()));
        props.put("sth:complex", complexProps);
        String expectedMsg = buildErrorMsgForDate("complex:date", Date.class);

        assertExceptionFor(doc -> doc.setProperties(props), file, expectedMsg);
        assertExceptionFor(doc -> doc.setDirtyProperties(props), file, expectedMsg);
    }

    private void assertExceptionFor(Consumer<Document> consumer, Document doc, String expectedMsg) {
        try {
            consumer.accept(doc);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), expectedMsg, e.getMessage());
        }
    }

    @Test
    public void itCannotConstructDocumentIfDateFoundInProps() {
        Map<String, Object> withoutComplexProps = Collections.singletonMap("dc:issued", new GregorianCalendar());
        try {
            Document document = Document.createWithName("ID", "TYPE");
            document.setProperties(withoutComplexProps);
            fail();
        } catch (IllegalArgumentException e) {
            String expectedMsg = buildErrorMsgForDate("dc:issued", GregorianCalendar.class);
            assertEquals(e.getMessage(), expectedMsg, e.getMessage());
        }

        Map<String, Object> complexProps = Collections.singletonMap("complex:date", new Date());
        Map<String, Object> withComplexProps = Collections.singletonMap("sth:complex", complexProps);
        try {
            Document document = Document.createWithName("ID", "TYPE");
            document.setProperties(withComplexProps);
            fail();
        } catch (IllegalArgumentException e) {
            String expectedMsg = buildErrorMsgForDate("complex:date", Date.class);
            assertEquals(e.getMessage(), expectedMsg, e.getMessage());
        }
    }

    /**
     * This test needs dataset.xsd schema deployed on Nuxeo server to work.
     */
    @Test
    public void itCanHandleComplexProperties() throws IOException, NoSuchFieldException, IllegalAccessException {
        // DataSet doctype comes from nuxeo-automation-test
        Document folder = nuxeoClient.repository().fetchDocumentByPath("/folder_1");
        Document document = Document.createWithName("dataSet1", "DataSet");
        document.setPropertyValue("dc:title", "new title");

        List<String> roles = Arrays.asList("BenchmarkIndicator", "Decision");
        Field field1 = new Field("string", "description", roles, "columnName", "sqlTypeHint", "name");
        Field field2 = new Field("string", "description", roles, "columnName", "sqlTypeHint", "name");
        List<Field> fields = Arrays.asList(field1, field2);

        Map<String, Object> creationProps = new HashMap<>();
        creationProps.put("ds:tableName", "MyTable");
        creationProps.put("ds:fields", fields);
        document.setProperties(creationProps);

        document = nuxeoClient.repository().createDocumentByPath("/folder_1", document);
        assertNotNull(document);
        assertEquals("DataSet", document.getType());
        List list = document.getPropertyValue("ds:fields");
        assertFalse(list.isEmpty());
        assertEquals(2, list.size());
        assertEquals("document", document.getEntityType());
        assertEquals(folder.getUid(), document.getParentRef());
        assertEquals("/folder_1/dataSet1", document.getPath());
        assertEquals("dataSet1", document.getTitle());

        // Here we are using a sub class DataSet of Document which let the dev implementing business logic.
        roles = Collections.singletonList("BenchmarkIndicator");
        fields = Collections.singletonList(
                new Field("string", "description", roles, "columnName", "sqlTypeHint", "name"));
        DataSet dataset = new DataSet(document.getId());
        dataset.setFields(fields);

        document = nuxeoClient.repository().updateDocument(dataset);
        dataset = new DataSet(document);
        assertNotNull(dataset);
        fields = dataset.getFields();
        assertNotNull(fields);
        assertFalse(fields.isEmpty());
        assertEquals(1, fields.size());
        assertEquals(1, fields.get(0).getRoles().size());

        // Use addFields
        dataset.addField(new Field("string", "description", roles, "columnName", "sqlTypeHint", "name"));

        document = nuxeoClient.repository().updateDocument(dataset);
        dataset = new DataSet(document);
        assertNotNull(dataset);
        fields = dataset.getFields();
        assertNotNull(fields);
        assertFalse(fields.isEmpty());
        assertEquals(2, fields.size());
    }

    /**
     * @since 2.3
     */
    @Test
    public void itCanCheckIfDocumentIsProxy() {
        assumeTrue("itCanCheckIfDocumentIsProxy works only since Nuxeo 8.10",
                nuxeoClient.getServerVersion().isGreaterThan(NuxeoVersion.LTS_8_10));

        Document root = nuxeoClient.repository().fetchDocumentRoot();
        Document folder = nuxeoClient.repository(root.getRepositoryName()).fetchDocumentByPath("/folder_1");

        assertFalse(folder.isProxy());

        Document proxy = nuxeoClient.operation("Document.CreateLiveProxy")
                                    .param("Destination Path", root.getPath())
                                    .input(folder)
                                    .execute();
        assertTrue(proxy.isProxy());
    }

    private String buildErrorMsgForDate(String key, Class<?> valueType) {
        return String.format(
                "Property '%s' has value of type '%s'. However, date values are not supported in Nuxeo Java Client."
                        + " Please convert it to String with ISO 8601 format \"yyyy-MM-dd'T'HH:mm:ss.SSSXXX\" before"
                        + " setting it as property.",
                key, valueType.getTypeName());
    }

    @Test
    public void itCanManageAnnotations() {
        assumeTrue("itCanManageAnnotations works only since Nuxeo 10.2",
                // TODO change the version to LTS
                nuxeoClient.getServerVersion().isGreaterThan(new NuxeoVersion(10, 2, 0, true)));
        Document file = nuxeoClient.repository().fetchDocumentByPath("/folder_2/file");

        file.createAnnotation("ANNOTATION_ID_001", "<entity />");
        file.createAnnotation("ANNOTATION_ID_002", "<entity />");

        Annotation annotation = file.fetchAnnotationById("ANNOTATION_ID_001");
        assertEquals(file.getId(), annotation.getDocumentId());
        assertEquals(Document.DEFAULT_FILE_CONTENT, annotation.getXPath());
        assertEquals("<entity />", annotation.getEntity());

        Annotations annotations = file.fetchAnnotations();
        assertEquals(2, annotations.size());
        List<Annotation> annotationList = annotations.getAnnotations();
        annotationList.sort(Comparator.comparing(Annotation::getId));
        assertEquals("ANNOTATION_ID_001", annotationList.get(0).getId());
        assertEquals("ANNOTATION_ID_002", annotationList.get(1).getId());

        annotation.setEntity("<entity>UPDATED</entity>");
        file.updateAnnotation(annotation);

        annotation = file.fetchAnnotationById("ANNOTATION_ID_001");
        assertEquals("<entity>UPDATED</entity>", annotation.getEntity());

        file.deleteAnnotation("ANNOTATION_ID_001");
        annotations = file.fetchAnnotations();
        assertEquals(1, annotations.size());
    }

}
