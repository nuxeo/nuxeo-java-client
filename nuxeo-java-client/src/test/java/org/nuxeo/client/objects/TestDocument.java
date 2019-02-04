package org.nuxeo.client.objects;

import org.junit.Test;
import org.nuxeo.client.objects.user.User;
import org.nuxeo.client.spi.NuxeoClientException;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * @since 3.3
 */
public class TestDocument {

    @Test
    public void testGetPropertyValueWithXPathSimpleProperty() {
        Document document = Document.createWithName("test.doc", "File");
        document.setPropertyValue("simple:string", "myString");
        document.setPropertyValue("simple:boolean", true);
        document.setPropertyValue("simple:int", 42);
        document.setPropertyValue("simple:float", 2.7f);
        document.setPropertyValue("simple:double", Math.PI);

        assertEquals("myString", document.getPropertyValue("simple:string"));
        assertEquals(true, document.getPropertyValue("simple:boolean"));
        assertEquals(Integer.valueOf(42), document.getPropertyValue("simple:int"));
        assertEquals(Float.valueOf(2.7f), document.getPropertyValue("simple:float"));
        assertEquals(Double.valueOf(Math.PI), document.getPropertyValue("simple:double"));
    }

    @Test
    public void testGetPropertyValueWithXPathListProperty() {
        Document document = Document.createWithName("test.doc", "File");
        List<String> list = Arrays.asList("first", "second", "third");
        document.setPropertyValue("list:list", list);

        assertEquals(list, document.getPropertyValue("list:list"));
        assertEquals(list, document.getPropertyValue("list:list/*"));
        assertEquals("second", document.getPropertyValue("list:list/1"));
        assertEquals("third", document.getPropertyValue("list:list/2"));
    }

    @Test
    public void testGetPropertyValueWithXPathComplexProperty() {
        Document document = Document.createWithName("test.doc", "File");
        Map<String, Object> complex = new HashMap<>();
        complex.put("string", "myString");
        complex.put("boolean", true);
        document.setPropertyValue("complex:complex", complex);

        assertEquals(complex, document.getPropertyValue("complex:complex"));
        assertEquals("myString", document.getPropertyValue("complex:complex/string"));
        assertEquals(true, document.getPropertyValue("complex:complex/boolean"));
    }

    @Test
    public void testGetPropertyValueWithXPathListComplexProperty() {
        Document document = Document.createWithName("test.doc", "File");
        Map<String, Object> complex1 = Collections.singletonMap("string", "myString1");
        Map<String, Object> complex2 = Collections.singletonMap("string", "myString2");
        List<Map<String, Object>> list = Arrays.asList(complex1, complex2);
        document.setPropertyValue("list:list", list);

        assertEquals(list, document.getPropertyValue("list:list"));
        assertEquals(list, document.getPropertyValue("list:list/*"));
        assertEquals("myString1", document.getPropertyValue("list:list/0/string"));
        assertEquals("myString2", document.getPropertyValue("list:list/1/string"));
        assertEquals(Arrays.asList("myString1", "myString2"), document.getPropertyValue("list:list/*/string"));
    }

    @Test
    public void testGetPropertyValueWithXPathMissingProperty() {
        Document document = Document.createWithName("test.doc", "File");
        Map<String, Object> complex1 = Collections.singletonMap("string", "myString1");
        List<Map<String, Object>> list = Collections.singletonList(complex1);
        document.setPropertyValue("list:list", list);

        assertNull(document.getPropertyValue("simple:missing"));
        assertNull(document.getPropertyValue("list:list/0/boolean"));
        assertNull(document.getPropertyValue("list:list/1"));
        assertNull(document.getPropertyValue("list:list/1/string"));
        assertNull(document.getPropertyValue("list:missing/0/string"));
    }
    
    @Test
    public void testGetPropertyValueWithWrongXPath() {
        Document document = Document.createWithName("test.doc", "File");
        Map<String, Object> complex1 = Collections.singletonMap("user", new User());
        List<Map<String, Object>> list = Collections.singletonList(complex1);
        document.setPropertyValue("list:list", list);

        try {
            document.getPropertyValue("list:list/indexRequired");
            fail("Expected to fail because list:list/indexRequired is not a valid XPath");
        } catch (NuxeoClientException e) {
            assertEquals("Unable to get list element with segment=indexRequired", e.getMessage());
        }
        try {
            document.getPropertyValue("list:list/0/123");
            fail("Expected to fail because list:list/0/123 is not a valid XPath");
        } catch (NuxeoClientException e) {
            assertEquals("Unable to get map element with segment=123", e.getMessage());
        }
        try {
            document.getPropertyValue("list:list/0/*");
            fail("Expected to fail because list:list/0/* is not a valid XPath");
        } catch (NuxeoClientException e) {
            assertEquals("Unable to get map element with segment=*", e.getMessage());
        }
        try {
            document.getPropertyValue("list:list/0/user/id");
            fail("Expected to fail because list:list/0/user/id is not a valid XPath");
        } catch (NuxeoClientException e) {
            assertEquals("Unable to traverse User object" +
                    " with segment=id", e.getMessage());
        }
    }
}
