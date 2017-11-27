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

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Exception thrown by the client when a HTTP error occurred.
 *
 * @since 3.0
 */
public class NuxeoClientRemoteException extends NuxeoClientException {

    /**
     * This field represents the JSON property {@code code} which is the exception class name happened on server.
     * <p />
     * Since Nuxeo LTS 2017 - 9.10, this property is absent from error response. Needed exception/error information are
     * now status code and message.
     *
     * @deprecated on Nuxeo server since LTS 2017 - 9.10
     */
    @Deprecated
    @JsonProperty("code")
    protected String exceptionClassName;

    protected final int status;

    protected final String errorBody;

    /**
     * Constructor to instantiate a remote errorBody with a HTTP status code, message, a stacktrace and an error body.
     * <p />
     * Response form server always define stacktrace and exception or none of them
     */
    // Stacktrace JSON value is the Java stacktrace server side in text/plain.
    // Store it in the error body.
    @JsonCreator // candidate for an error response
    public NuxeoClientRemoteException(@JsonProperty(value = "status") int status,
            @JsonProperty("message") String message, @JsonProperty("stacktrace") String errorBody,
            @JsonProperty("exception") Throwable cause) {
        super(message, cause);
        this.status = status;
        this.errorBody = errorBody;
    }

    /**
     * @see #exceptionClassName
     * @deprecated on Nuxeo server since LTS 2017 - 9.10
     */
    @Deprecated
    public String getExceptionClassName() {
        return exceptionClassName;
    }

    public int getStatus() {
        return status;
    }

    public String getErrorBody() {
        return errorBody;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(getClass().getName());
        s.append(": HTTP/").append(status);
        String message = getLocalizedMessage();
        if (StringUtils.isNotBlank(message)) {
            s.append(": ").append(message);
        }
        return s.toString();
    }

}
