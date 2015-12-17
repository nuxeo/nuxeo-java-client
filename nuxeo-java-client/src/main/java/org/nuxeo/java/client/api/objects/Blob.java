/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     bstefanescu
 */
package org.nuxeo.java.client.api.objects;

import org.nuxeo.java.client.api.ConstantsV1;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @since 1.0
 */
public abstract class Blob extends NuxeoObject {

    @JsonIgnore
    protected String mimeType;

    @JsonIgnore
    protected String fileName;

    public Blob(String fileName) {
        this(fileName, null);
    }

    public Blob(String fileName, String mimeType) {
        super(ConstantsV1.ENTITY_TYPE_BLOB);
        this.fileName = fileName;
        setMimeType(mimeType);
    }

    public Blob() {
        super(ConstantsV1.ENTITY_TYPE_BLOB);
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType == null ? ConstantsV1.APPLICATION_OCTET_STREAM : mimeType;
    }

    @JsonIgnore
    public int getLength() {
        return -1;
    }

    protected String formatLength(int len) {
        int k = len / 1024;
        if (k <= 0) {
            return len + " B";
        } else if (k < 1024) {
            return k + " K";
        } else {
            return (k / 1024) + " M";
        }
    }

    @Override
    public String toString() {
        return fileName + " - " + mimeType + " - " + formatLength(getLength());
    }
}
