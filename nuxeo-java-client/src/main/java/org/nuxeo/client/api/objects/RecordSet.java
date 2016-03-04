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
package org.nuxeo.client.api.objects;

import java.util.List;
import java.util.Map;

import org.nuxeo.client.api.ConstantsV1;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 0.1
 */
public class RecordSet extends NuxeoEntity {

    protected int pageSize = -1;

    protected int currentPageIndex = -1;

    protected int numberOfPages = -1;

    @JsonProperty("entries")
    protected List<Map<String, String>> uuids;

    public RecordSet() {
        super(ConstantsV1.ENTITY_TYPE_DOCUMENTS);
    }

    public RecordSet(int currentPageIndex, int pageSize, int numberOfPages) {
        super(ConstantsV1.ENTITY_TYPE_DOCUMENTS);
        this.currentPageIndex = currentPageIndex;
        this.pageSize = pageSize;
        this.numberOfPages = numberOfPages;
    }

    public boolean isPaginable() {
        return currentPageIndex >= 0;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getCurrentPageIndex() {
        return currentPageIndex;
    }

    public void setCurrentPageIndex(int currentPageIndex) {
        this.currentPageIndex = currentPageIndex;
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public List<Map<String, String>> getUuids() {
        return uuids;
    }
}
