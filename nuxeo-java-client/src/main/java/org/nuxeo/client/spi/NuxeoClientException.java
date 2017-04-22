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
package org.nuxeo.client.spi;

import org.nuxeo.client.objects.EntityTypes;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 0.1
 */
public class NuxeoClientException extends RuntimeException {

    private static final int INTERNAL_ERROR_STATUS = 666;

    @JsonProperty("entity-type")
    private final String entityType;

    @JsonProperty("stacktrace")
    protected final String exception;

    @JsonProperty("exception")
    private final Throwable throwable;

    private final int status;

    private String code;

    private final String type;

    private final String info;

    public NuxeoClientException(String message) {
        this(message, null);
    }

    /**
     * Constructor for exceptions in client.
     */
    public NuxeoClientException(Throwable cause) {
        this("An internal error occurred", cause);
    }

    /**
     * Constructor for exceptions in client.
     */
    public NuxeoClientException(String message, Throwable cause) {
        super(message, cause);
        entityType = EntityTypes.EXCEPTION;
        exception = message;
        throwable = cause;
        status = INTERNAL_ERROR_STATUS;
        type = "Error";
        info = cause == null ? null : cause.getMessage();
    }

    public NuxeoClientException(int code, String message) {
        super("An error occurred, code=" + code);
        entityType = EntityTypes.EXCEPTION;
        exception = null;
        throwable = null;
        status = code;
        type = null;
        info = message;
    }

    public String getEntityType() {
        return entityType;
    }

    public String getException() {
        return exception;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public int getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getType() {
        return type;
    }

    public String getInfo() {
        return info;
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        appendValue(sb, "status", status);
        appendValue(sb, "code", code);
        appendValue(sb, "type", type);
        if (exception != null && !"null".equals(exception)) {
            appendValue(sb, "exception", exception);
            sb.append("------ END OF CLIENT EXCEPTION MESSAGE ------");
        }
        return sb.toString();
    }

    private void appendValue(StringBuilder sb, String key, Object value) {
        if (value != null) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(key).append('=').append(value.toString().replace("\\n", "\n"));
        }
    }

}
