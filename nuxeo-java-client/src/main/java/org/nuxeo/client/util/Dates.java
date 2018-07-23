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
package org.nuxeo.client.util;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Parse / format ISO 8601 dates.
 *
 * @since 1.0
 */
public class Dates {

    private Dates() {
        // no instance allowed
    }

    public static Calendar parse(String str) throws ParseException {
        if (str == null) {
            return null;
        }
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        cal.clear();
        int len = str.length();
        if (len == 0) {
            return cal;
        }
        int i = 0;
        i = readYear(cal, str, i);
        i = readCharOpt('-', cal, str, i);
        if (i == -1) {
            return cal;
        }
        i = readMonth(cal, str, i);
        i = readCharOpt('-', cal, str, i);
        if (i == -1) {
            return cal;
        }
        i = readDay(cal, str, i);
        i = readCharOpt('T', cal, str, i);
        if (i == -1) {
            return cal;
        }
        i = readHours(cal, str, i);
        i = readCharOpt(':', cal, str, i);
        if (i == -1) {
            return cal;
        }
        i = readMinutes(cal, str, i);
        if (isChar(':', str, i)) {
            i = readSeconds(cal, str, i + 1);
            if (isChar('.', str, i)) {
                i = readMilliseconds(cal, str, i + 1);
            }
        }
        if (i > -1) {
            readTimeZone(cal, str, i);
        }
        return cal;
    }

    public static Date parseW3CDateTime(String str) {
        if (str == null) {
            return null;
        }
        try {
            return parse(str).getTime();
        } catch (ParseException e) {
            throw new IllegalArgumentException("Failed to parse ISO 8601 date: " + str, e);
        }
    }

    /**
     * 2011-10-23T12:00:00.00Z
     *
     * @return the formatted date.
     */
    public static String formatW3CDateTime(Date date) {
        if (date == null) {
            return null;
        }
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        cal.setTime(date);
        return String.format("%04d-%02d-%02dT%02d:%02d:%02dZ", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DATE), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE),
                cal.get(Calendar.SECOND));
    }

    public static String formatW3CDateTimeMs(Date date) {
        if (date == null) {
            return null;
        }
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        cal.setTime(date);
        return String.format("%04d-%02d-%02dT%02d:%02d:%02d.%03dZ", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DATE), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE),
                cal.get(Calendar.SECOND), cal.get(Calendar.MILLISECOND));
    }

    private static int readYear(Calendar cal, String str, int off) throws ParseException {
        if (str.length() >= off + 4) {
            cal.set(Calendar.YEAR, Integer.parseInt(str.substring(off, off + 4)));
            return off + 4;
        }
        throw new ParseException("Invalid year in date '" + str + "'", off);
    }

    private static int readMonth(Calendar cal, String str, int off) throws ParseException {
        if (str.length() >= off + 2) {
            cal.set(Calendar.MONTH, Integer.parseInt(str.substring(off, off + 2)) - 1);
            return off + 2;
        }
        throw new ParseException("Invalid month in date '" + str + "'", off);
    }

    private static int readDay(Calendar cal, String str, int off) throws ParseException {
        if (str.length() >= off + 2) {
            cal.set(Calendar.DATE, Integer.parseInt(str.substring(off, off + 2)));
            return off + 2;
        }
        throw new ParseException("Invalid day in date '" + str + "'", off);
    }

    private static int readHours(Calendar cal, String str, int off) throws ParseException {
        if (str.length() >= off + 2) {
            cal.set(Calendar.HOUR, Integer.parseInt(str.substring(off, off + 2)));
            return off + 2;
        }
        throw new ParseException("Invalid hours in date '" + str + "'", off);
    }

    private static int readMinutes(Calendar cal, String str, int off) throws ParseException {
        if (str.length() >= off + 2) {
            cal.set(Calendar.MINUTE, Integer.parseInt(str.substring(off, off + 2)));
            return off + 2;
        }
        throw new ParseException("Invalid minutes in date '" + str + "'", off);
    }

    private static int readSeconds(Calendar cal, String str, int off) throws ParseException {
        if (str.length() >= off + 2) {
            cal.set(Calendar.SECOND, Integer.parseInt(str.substring(off, off + 2)));
            return off + 2;
        }
        throw new ParseException("Invalid seconds in date '" + str + "'", off);
    }

    /**
     * Return -1 if no more content to read or the offset of the expected TZ
     *
     */
    private static int readMilliseconds(Calendar cal, String str, int off) {
        int e = str.indexOf('Z', off);
        if (e == -1) {
            e = str.indexOf('+', off);
            if (e == -1) {
                e = str.indexOf('-', off);
            }
        }
        String ms = e == -1 ? str.substring(off) : str.substring(off, e);
        // need to normalize the ms fraction to 3 digits.
        // If less than 3 digits right pad with 0
        // If more than 3 digits truncate to 3 digits.
        int mslen = ms.length();
        if (mslen > 0) {
            int f;
            switch (mslen) {
            case 1:
                f = Integer.parseInt(ms) * 100;
                break;
            case 2:
                f = Integer.parseInt(ms) * 10;
                break;
            case 3:
                f = Integer.parseInt(ms);
                break;
            default: // truncate
                f = Integer.parseInt(ms.substring(0, 3));
                break;
            }
            cal.set(Calendar.MILLISECOND, f);
        }
        return e;
    }

    private static boolean isChar(char c, String str, int off) {
        return str.length() > off && str.charAt(off) == c;
    }

    private static int readCharOpt(char c, Calendar cal, String str, int off) {
        if (str.length() > off) {
            if (str.charAt(off) == c) {
                return off + 1;
            }
        }
        return -1;
    }

    private static boolean readTimeZone(Calendar cal, String str, int off) throws ParseException {
        int len = str.length();
        if (len == off) {
            return false;
        }
        char c = str.charAt(off);
        if (c == 'Z') {
            return true;
        }
        off++;
        boolean plus = false;
        if (c == '+') {
            plus = true;
        } else if (c != '-') {
            throw new ParseException("Only Z, +, - prefixes are allowed in TZ", off);
        }
        int h;
        int m = 0;
        int d = len - off;
        if (d == 2) {
            h = Integer.parseInt(str.substring(off, off + 2));
        } else if (d == 5) {
            h = Integer.parseInt(str.substring(off, off + 2));
            m = Integer.parseInt(str.substring(off + 3, off + 5));
            // we do not check for ':'. we assume it is in the correct format
        } else {
            throw new ParseException("Invalid TZ in \"" + str + "\"", off);
        }

        if (plus) {
            cal.add(Calendar.HOUR, -h);
            cal.add(Calendar.MINUTE, -m);
        } else {
            cal.add(Calendar.HOUR, h);
            cal.add(Calendar.MINUTE, m);
        }

        return true;
    }

}
