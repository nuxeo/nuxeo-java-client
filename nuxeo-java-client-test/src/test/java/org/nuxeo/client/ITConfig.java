/*
 * (C) Copyright 2017 Nuxeo (http://nuxeo.com/) and others.
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
 *     Kevin Leturc <kleturc@nuxeo.com>
 */
package org.nuxeo.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import java.util.List;

import org.junit.Test;
import org.nuxeo.client.objects.config.DocType;
import org.nuxeo.client.objects.config.DocTypes;
import org.nuxeo.client.objects.config.Facet;
import org.nuxeo.client.objects.config.Schema;
import org.nuxeo.client.objects.config.SchemaField;

/**
 * @since 3.0
 */
public class ITConfig extends AbstractITBase {

    @Test
    public void itCanFetchTypes() {
        DocTypes types = nuxeoClient.configManager().fetchTypes();
        assertNotNull(types);

        // Assert docType
        DocType fileType = types.getDocType("File");
        assertNotNull(fileType);
        // Assert file fix is correct
        assertEquals("File", fileType.getName());
        // Assert schema are correctly bind
        Schema uidSchema = fileType.getSchema("uid");
        assertTrue(uidSchema.getField("uid").isString());

        // Assert schemas
        Schema commonSchema = types.getSchema("common");
        assertNotNull(commonSchema);
        assertTrue(commonSchema.getField("icon").isString());
    }

    @Test
    public void itCanFetchType() {
        DocType type = nuxeoClient.configManager().fetchType("File");
        assertNotNull(type);

        // Assert schemas
        // Assert simple type
        Schema dcSchema = type.getSchema("dublincore");
        assertNotNull(dcSchema);
        assertEquals("dc", dcSchema.getPrefix());
        assertTrue(dcSchema.getField("description").isString());
        assertTrue(dcSchema.getField("created").isDate());
        SchemaField dcContributors = dcSchema.getField("contributors");
        assertTrue(dcContributors.isString());
        assertTrue(dcContributors.isArray());

        // Assert complex type
        Schema filesSchema = type.getSchema("files");
        SchemaField files = filesSchema.getField("files");
        assertTrue(files.isComplex());
        assertTrue(files.isArray());
        assertTrue(files.hasComplex());
        SchemaField file = files.getComplexField("file");
        assertTrue(file.isBlob());
    }

    @Test
    public void itCanFetchSchemas() {
        List<Schema> schemas = nuxeoClient.configManager().fetchSchemas();
        assertNotNull(schemas);
        Schema filesSchema = schemas.stream()
                                    .filter(schema -> "files".equals(schema.getName()))
                                    .findFirst()
                                    .orElse(null);
        assertNotNull(filesSchema);
        SchemaField files = filesSchema.getField("files");
        assertTrue(files.isComplex());
        assertTrue(files.isArray());
        assertTrue(files.hasComplex());
        SchemaField file = files.getComplexField("file");
        assertTrue(file.isBlob());
    }

    @Test
    public void itCanFetchSchema() {
        Schema dcSchema = nuxeoClient.configManager().fetchSchema("dublincore");
        assertNotNull(dcSchema);
        assertEquals("dc", dcSchema.getPrefix());
        assertTrue(dcSchema.getField("description").isString());
        assertTrue(dcSchema.getField("created").isDate());
        SchemaField dcContributors = dcSchema.getField("contributors");
        assertTrue(dcContributors.isString());
        assertTrue(dcContributors.isArray());
    }

    @Test
    public void itCanFetchFacets() {
        // test failed on 7.10 because of malformed response
        assumeTrue("itCanFetchFacets works only since Nuxeo 8.10",
                nuxeoClient.getServerVersion().isGreaterThan(NuxeoVersion.LTS_8_10));

        List<Facet> facets = nuxeoClient.configManager().fetchFacets();
        assertNotNull(facets);
        Facet savedSearchFacet = facets.stream()
                                       .filter(facet -> "SavedSearch".equals(facet.getName()))
                                       .findFirst()
                                       .orElse(null);
        assertNotNull(savedSearchFacet);
        Schema savedSearchSchema = savedSearchFacet.getSchema("saved_search");
        assertEquals("saved", savedSearchSchema.getPrefix());
        assertTrue(savedSearchSchema.getField("queryLanguage").isString());
    }

    @Test
    public void itCanFetchFacet() {
        Facet facet = nuxeoClient.configManager().fetchFacet("Thumbnail");
        assertNotNull(facet);
        assertEquals("Thumbnail", facet.getName());
        Schema thumbnailSchema = facet.getSchema("thumbnail");
        assertNotNull(thumbnailSchema);
        assertEquals("thumb", thumbnailSchema.getPrefix());
        assertTrue(thumbnailSchema.getField("thumbnail").isBlob());
    }

}
