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
package org.nuxeo.client.objects;

import java.util.List;

import org.nuxeo.client.NuxeoClient;

/**
 * @since 0.1
 */
public class Documents extends PaginableEntity<Document> implements Connectable {

    public Documents() {
        super(EntityTypes.DOCUMENTS);
    }

    public Documents(List<Document> documents) {
        this();
        this.entries = documents;
    }

    public void addDocument(Document document) {
        entries.add(document);
    }

    public void removeDocument(Document document) {
        entries.remove(document);
    }

    public Document getDocument(int index) {
        return getEntry(index);
    }

    public List<Document> getDocuments() {
        return getEntries();
    }

    @Override
    public void reconnectWith(NuxeoClient nuxeoClient) {
        for (Document entry : entries) {
            entry.reconnectWith(nuxeoClient);
        }
    }

}
