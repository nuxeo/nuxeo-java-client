/*
 * (C) Copyright 2015 Nuxeo SA (http://nuxeo.com/) and contributors.
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
package org.nuxeo.java.client.api.objects.blob;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.nuxeo.java.client.api.ConstantsV1;
import org.nuxeo.java.client.api.objects.Blob;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @since 1.0
 */
public class FileBlob extends Blob {

    protected final File file;

    public FileBlob() {
        super();
        file = null;
    }

    public FileBlob(File file) {
        super(file.getName(), ConstantsV1.APPLICATION_OCTET_STREAM);
        this.file = file;
    }

    @JsonIgnore
    @Override
    public int getLength() {
        long length = file.length();
        if (length > (long) Integer.MAX_VALUE) {
            return -1;
        }
        return (int) length;
    }

    public File getFile() {
        return file;
    }

}
