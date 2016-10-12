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
package org.nuxeo.client.api.objects.audit;

import java.util.List;

import org.nuxeo.client.api.ConstantsV1;
import org.nuxeo.client.api.objects.NuxeoEntity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 0.1
 */
public class Audit extends NuxeoEntity {

    public Audit() {
        super(ConstantsV1.ENTITY_TYPE_AUDIT);
    }

    @JsonProperty("entries")
    protected List<LogEntry> logEntries;

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

    public List<LogEntry> getLogEntries() {
        return logEntries;
    }

    public Boolean getIsPaginable() {
        return isPaginable;
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
}
