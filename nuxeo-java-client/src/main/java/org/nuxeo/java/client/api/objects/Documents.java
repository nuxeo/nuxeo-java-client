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

import org.nuxeo.java.client.api.ConstantsV1;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 1.0
 */
public class Documents extends NuxeoObject {

    @JsonProperty("entries")
    protected List<Document> documents;

    protected Boolean isPaginable;

    protected int resultsCount;

    protected int pageSize;

    protected int maxPageSize;

    protected int currentPageSize;

    protected int currentPageIndex;

    protected int numberOfPages;

    protected Boolean isPreviousPageAvailable;

    protected Boolean isNextPageAvailable;

    protected Boolean isLastPageAvailable;

    protected Boolean isSortable;

    protected Boolean hasError;

    protected String errorMessage;

    protected int totalSize;

    protected int pageIndex;

    protected int pageCount;

    public Documents() {
        super(ConstantsV1.ENTITY_TYPE_DOCUMENTS);
    }

    public Documents(List<Document> documents) {
        super(ConstantsV1.ENTITY_TYPE_DOCUMENTS);
        this.documents = documents;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public int getResultsCount() {
        return resultsCount;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getMaxPageSize() {
        return maxPageSize;
    }

    public int getCurrentPageSize() {
        return currentPageSize;
    }

    public int getCurrentPageIndex() {
        return currentPageIndex;
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public int getTotalSize() {
        return totalSize;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public int getPageCount() {
        return pageCount;
    }

    public Boolean getIsPaginable() {
        return isPaginable;
    }

    public Boolean getIsPreviousPageAvailable() {
        return isPreviousPageAvailable;
    }

    public Boolean getIsNextPageAvailable() {
        return isNextPageAvailable;
    }

    public Boolean getIsLastPageAvailable() {
        return isLastPageAvailable;
    }

    public Boolean getIsSortable() {
        return isSortable;
    }

    public Boolean getHasError() {
        return hasError;
    }
}
