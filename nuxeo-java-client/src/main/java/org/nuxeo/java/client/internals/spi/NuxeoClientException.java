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
package org.nuxeo.java.client.internals.spi;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 1.0
 */
public class NuxeoClientException extends RuntimeException {

    protected final int status;

    protected String code;

    protected final String type;

    protected final String info;

    @JsonProperty("stacktrace")
    protected final String exception;

    @JsonProperty("exception")
    protected final Throwable throwable;

    @JsonProperty("entity-type")
    protected final String entityType;

    public String getEntityType() {
        return entityType;
    }

    public NuxeoClientException(int status, String type, String message, Throwable cause) {
        this.status = status;
        this.type = type;
        info = extractInfo(cause);
        throwable = cause;
        entityType = "exception";
        exception = null;
    }

    public NuxeoClientException(int status, String type, String message, String info) {
        this.status = status;
        this.type = type;
        this.info = info;
        throwable = null;
        entityType = "exception";
        exception = null;
    }

    public NuxeoClientException(Throwable e) {
        throwable = e;
        status = 500;
        type = "Error";
        info = e.getMessage();
        entityType = "exception";
        exception = null;
    }

    public NuxeoClientException(int code, String message) {
        info = message;
        throwable = null;
        type = null;
        status = code;
        entityType = "exception";
        exception = null;
    }

    public NuxeoClientException(String message) {
        info = message;
        throwable = null;
        type = null;
        status = 666;
        entityType = "exception";
        exception = null;
    }

    public int getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }

    protected static String extractInfo(Throwable t) {
        if (t == null) {
            return "";
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public String getRemoteStackTrace() {
        Field[] fields = this.getClass().getDeclaredFields();
        StringBuilder result = new StringBuilder();
        for (Field field : fields) {
            result.append("  ");
            try {
                result.append(field.getName());
                result.append(": ");
                result.append(field.get(this));
            } catch (IllegalAccessException ex) {
                System.out.println(ex);
            }
            result.append(System.lineSeparator());
        }

        return result.toString();

    }

    public void printStackTrace(PrintStream s) {
        s.println("Exception:");
        s.print(getRemoteStackTrace());
    }

    public void printStackTrace(PrintWriter s) {
        s.println("Exception:");
        s.print(getRemoteStackTrace());
    }

    public String getCode() {
        return code;
    }


    public String getException() {
        return exception;
    }

    public String getInfo() {
        return info;
    }

}
