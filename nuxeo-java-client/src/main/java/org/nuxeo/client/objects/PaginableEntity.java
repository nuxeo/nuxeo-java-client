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
package org.nuxeo.client.objects;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Paginable entity is meant for entity using paginable mechanism.
 *
 * @param <E> The underlying entity type.
 * @since 3.0
 */
public class PaginableEntity<E> extends Entity {

    protected List<E> entries;

    protected int resultsCount;

    protected int pageSize;

    protected int maxPageSize;

    protected int currentPageSize;

    protected int currentPageIndex;

    protected int numberOfPages;

    protected int totalSize;

    protected int pageIndex;

    protected int pageCount;

    // we need this attribute as we don't declare getIsNextPageAvailable, unless jackson can't properly deserialize
    @JsonProperty("isPaginable")
    protected Boolean isPaginable;

    // we need this attribute as we don't declare getIsNextPageAvailable, unless jackson can't properly deserialize
    @JsonProperty("isPreviousPageAvailable")
    protected Boolean isPreviousPageAvailable;

    // we need this attribute as we don't declare getIsNextPageAvailable, unless jackson can't properly deserialize
    @JsonProperty("isNextPageAvailable")
    protected Boolean isNextPageAvailable;

    // we need this attribute as we don't declare getIsNextPageAvailable, unless jackson can't properly deserialize
    @JsonProperty("isLastPageAvailable")
    protected Boolean isLastPageAvailable;

    // we need this attribute as we don't declare getIsNextPageAvailable, unless jackson can't properly deserialize
    @JsonProperty("isSortable")
    protected Boolean isSortable;

    // we need this attribute as we don't declare getIsNextPageAvailable, unless jackson can't properly deserialize
    @JsonProperty("hasError")
    protected Boolean hasError;

    protected String errorMessage;

    public PaginableEntity(String entityType) {
        super(entityType);
    }

    public List<E> getEntries() {
        return entries;
    }

    public E getEntry(int index) {
        return entries.get(index);
    }

    public int size() {
        return entries.size();
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

    public int getTotalSize() {
        return totalSize;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public int getPageCount() {
        return pageCount;
    }

    public boolean isPaginable() {
        return isTrue(isPaginable);
    }

    public boolean isPreviousPageAvailable() {
        return isTrue(isPreviousPageAvailable);
    }

    public boolean isNextPageAvailable() {
        return isTrue(isNextPageAvailable);
    }

    public boolean isLastPageAvailable() {
        return isTrue(isLastPageAvailable);
    }

    public boolean isSortable() {
        return isTrue(isSortable);
    }

    public boolean hasError() {
        return isTrue(hasError);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public static boolean isTrue(Boolean bool) {
        return bool != null && bool;
    }

}
