/*
 * (C) Copyright 2016 Nuxeo SA (http://nuxeo.com/) and contributors.
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

package org.nuxeo.java.client.api.objects;

import java.util.List;
import java.util.Map;

import org.nuxeo.java.client.api.ConstantsV1;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 1.0
 */
public class RecordSet extends NuxeoObject {

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
