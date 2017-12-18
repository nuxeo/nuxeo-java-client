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
package org.nuxeo.client;

import java.nio.charset.Charset;
import java.util.Locale;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * @since 0.1
 */
public final class MediaType {

    private static final String TOKEN = "([a-zA-Z0-9-!#$%&'*+.^_`{|}~]+)";

    private static final String QUOTED = "\"([^\"]*)\"";

    private static final Pattern TYPE_SUBTYPE = Pattern.compile(TOKEN + "/" + TOKEN);

    private static final Pattern PARAMETER = Pattern.compile(";\\s*(?:" + TOKEN + "=(?:" + TOKEN + "|" + QUOTED + "))?");

    private final String originalString;

    private final String type;

    private final String subtype;

    private final String charset;

    private final String nuxeoEntity;

    private MediaType(String originalString, String type, String subtype, String charset, String nuxeoEntity) {
        this.originalString = originalString;
        this.type = type;
        this.subtype = subtype;
        this.charset = charset;
        this.nuxeoEntity = nuxeoEntity;
    }

    /**
     * Returns the high-level media type, such as "text", "image", "audio", "video", or "application".
     */
    public String type() {
        return type;
    }

    /**
     * Returns a specific media subtype, such as "plain" or "png", "mpeg", "mp4" or "xml".
     */
    public String subtype() {
        return subtype;
    }

    /**
     * Returns the charset of this media type, or null if this media type doesn't specify a charset.
     */
    public Charset charset() {
        return charset != null ? Charset.forName(charset) : null;
    }

    /**
     * Returns the Nuxeo Entity
     */
    public String nuxeoEntity() {
        return nuxeoEntity;
    }

    /**
     * Returns the charset of this media type, or {@code defaultValue} if this media type doesn't specify a charset.
     */
    public Charset charset(Charset defaultValue) {
        return charset != null ? Charset.forName(charset) : defaultValue;
    }

    public okhttp3.MediaType toOkHttpMediaType() {
        return okhttp3.MediaType.parse(originalString);
    }

    public boolean equalsType(MediaType mediaType) {
        return mediaType != null && StringUtils.equals(type, mediaType.type);
    }

    public boolean equalsTypeSubType(MediaType mediaType) {
        return equalsType(mediaType) && StringUtils.equals(subtype, mediaType.subtype);
    }

    public boolean equalsTypeSubTypeWithoutSuffix(MediaType mediaType) {
        if (equalsType(mediaType)) {
            UnaryOperator<String> removeSuffix = s -> s.replaceAll("\\+.*", "");
            String thisSubtype = removeSuffix.apply(subtype);
            String givenSubtype = removeSuffix.apply(mediaType.subtype);
            return thisSubtype.equals(givenSubtype);
        }
        return false;
    }

    /**
     * Returns the encoded media type, like "text/plain; charset=utf-8", appropriate for use in a Content-Type header.
     */
    @Override
    public String toString() {
        return originalString;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof MediaType && ((MediaType) o).originalString.equals(originalString);
    }

    @Override
    public int hashCode() {
        return originalString.hashCode();
    }

    public static MediaType fromOkHttpMediaType(okhttp3.MediaType mediaType) {
        if (mediaType == null) {
            return null;
        }
        return parse(mediaType.toString());
    }

    /**
     * Returns a media type for {@code string}, or null if {@code string} is not a well-formed media type.
     */
    public static MediaType parse(String string) {
        Matcher typeSubtype = TYPE_SUBTYPE.matcher(string);
        if (!typeSubtype.lookingAt()) {
            return null;
        }
        String type = typeSubtype.group(1).toLowerCase(Locale.US);
        String subtype = typeSubtype.group(2).toLowerCase(Locale.US);

        String charset = null;
        String nuxeoEntity = null;
        Matcher parameter = PARAMETER.matcher(string);
        for (int s = typeSubtype.end(); s < string.length(); s = parameter.end()) {
            parameter.region(s, string.length());
            if (!parameter.lookingAt()) {
                return null; // This is not a well-formed media type.
            }

            String name = parameter.group(1);
            if (name == null) {
                continue;
            }
            if (name.equalsIgnoreCase("nuxeo-entity")) {
                String nuxeoEntityParameter = parameter.group(2) != null ? parameter.group(2) // Value is a token.
                        : parameter.group(3); // Value is a quoted string.
                if (nuxeoEntity != null && !nuxeoEntityParameter.equalsIgnoreCase(nuxeoEntity)) {
                    throw new IllegalArgumentException("Multiple different nuxeo entities: " + string);
                }
                nuxeoEntity = nuxeoEntityParameter;
            }
            String charsetParameter = parameter.group(2) != null ? parameter.group(2) // Value is a token.
                    : parameter.group(3); // Value is a quoted string.
            if (charset != null && !charsetParameter.equalsIgnoreCase(charset)) {
                throw new IllegalArgumentException("Multiple different charsets: " + string);
            }
            charset = charsetParameter;
        }

        return new MediaType(string, type, subtype, charset, nuxeoEntity);
    }

}
