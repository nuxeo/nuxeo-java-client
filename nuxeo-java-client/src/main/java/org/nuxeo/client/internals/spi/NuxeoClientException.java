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
package org.nuxeo.client.internals.spi;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.nuxeo.client.api.ConstantsV1;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 0.1
 */
public class NuxeoClientException extends RuntimeException {

    private static final int INTERNAL_ERROR_STATUS = 666;

    protected final int status;

    protected String code;

    protected final String type;

    protected final String info;

    @JsonProperty("stacktrace")
    protected final String exception;

    @JsonProperty("exception")
    protected final Throwable throwable;

    @JsonProperty("entity-type")
    private final String entityType;

    public String getEntityType() {
        return entityType;
    }

    public NuxeoClientException(Throwable e) {
        this("error", e);
    }

    public NuxeoClientException(String message, Throwable e) {
        throwable = e;
        status = INTERNAL_ERROR_STATUS;
        type = "Error";
        info = e == null ? null : e.getMessage();
        entityType = ConstantsV1.ENTITY_TYPE_EXCEPTION;
        exception = message;
    }

    public NuxeoClientException(int code, String message) {
        info = message;
        throwable = null;
        type = null;
        status = code;
        entityType = ConstantsV1.ENTITY_TYPE_EXCEPTION;
        exception = null;
    }

    public NuxeoClientException(String message) {
        this(message, null);
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
            try {
                if (Modifier.isPrivate(field.getModifiers()) || field.get(this) == null) {
                    continue;
                }
                result.append("  ");
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

    @Override
    public void printStackTrace(PrintStream s) {
        if (status == INTERNAL_ERROR_STATUS) {
            super.printStackTrace(s);
        }
        s.println("Exception:");
        s.print(getRemoteStackTrace());
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        if (status == INTERNAL_ERROR_STATUS) {
            super.printStackTrace(s);
        }
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
