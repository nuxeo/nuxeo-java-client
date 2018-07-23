/*
 * (C) Copyright 2018 Nuxeo (http://nuxeo.com/) and others.
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
package org.nuxeo.client.util;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Date;
import java.util.Random;

import org.junit.Test;

/**
 * @since 3.1
 * @deprecated since 3.1
 */
@Deprecated
public class TestBase64 {

    protected static final Encoder ENCODER = Base64.getEncoder();

    protected static final String USERNAME = "ihaveaverylongname@onaverylongdomain.withaverylongtld";

    protected static final String PASSWORD = "ohmypasswordislongtoo:abcdefghijklmnopqrstuvwxyz";

    @Test
    public void testEncode() {
        // compute token
        long ts = new Date().getTime();
        long random = new Random(ts).nextInt();

        String clearToken = String.format("%d:%d:%s:%s", ts, random, PASSWORD, USERNAME);

        byte[] hashedToken;

        try {
            hashedToken = MessageDigest.getInstance("MD5").digest(clearToken.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Cannot compute token", e);
        }

        String formerToken = org.nuxeo.client.util.Base64.encode(hashedToken);
        String token = ENCODER.encodeToString(hashedToken);
        assertEquals(token, formerToken);
    }

    @Test
    public void testEncodeDoesNotBreakLine() {

        String info = USERNAME + ":" + PASSWORD;
        String formerToken = "Basic "
                + org.nuxeo.client.util.Base64.encode(info, org.nuxeo.client.util.Base64.DONT_BREAK_LINES);
        String token = "Basic " + ENCODER.encodeToString(info.getBytes(UTF_8));
        assertEquals(token, formerToken);
    }

}
