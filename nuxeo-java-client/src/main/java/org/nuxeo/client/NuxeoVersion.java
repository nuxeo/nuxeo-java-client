/*
 * (C) Copyright 2017-2020 Nuxeo (http://nuxeo.com/) and others.
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
package org.nuxeo.client;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nuxeo.client.spi.NuxeoClientException;

/**
 * Represents a Nuxeo server version.
 *
 * @since 3.0.0
 */
public class NuxeoVersion {

    public static final NuxeoVersion LTS_7_10 = new NuxeoVersion(7, 10, -1, 0, false);

    public static final NuxeoVersion LTS_8_10 = new NuxeoVersion(8, 10, -1, 0, false);

    public static final NuxeoVersion LTS_9_10 = new NuxeoVersion(9, 10, -1, 0, false);

    public static final NuxeoVersion LTS_10_10 = new NuxeoVersion(10, 10, -1, 0, false);

    private static final Pattern NUXEO_VERSION_PATTERN = Pattern.compile(
            "(\\d+)\\.(\\d+)(?:\\.(\\d+))?(?:-HF(\\d+))?(-SNAPSHOT)?(-I\\d{8}_\\d{4})?");

    private final int majorVersion;

    private final int minorVersion;

    /** @since 3.6.0 */
    private final int buildVersion;

    private final int hotfix;

    private final boolean snapshot;

    protected NuxeoVersion(int majorVersion, int minorVersion, int buildVersion, int hotfix, boolean snapshot) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.buildVersion = buildVersion;
        this.hotfix = hotfix;
        this.snapshot = snapshot;
    }

    public String version() {
        StringBuilder version = new StringBuilder();
        version.append(majorVersion()).append('.').append(minorVersion());
        if (buildVersion() != -1) {
            version.append('.').append(buildVersion());
        }
        if (hotfix() != 0) {
            version.append("-HF").append(String.format("%02d", hotfix()));
        }
        if (snapshot()) {
            version.append("-SNAPSHOT");
        }
        return version.toString();
    }

    public int majorVersion() {
        return majorVersion;
    }

    public int minorVersion() {
        return minorVersion;
    }

    /**
     * @return the build version or -1 if it is not a 11.x
     * @since 3.6.0
     */
    public int buildVersion() {
        return buildVersion;
    }

    public int hotfix() {
        return hotfix;
    }

    public boolean snapshot() {
        return snapshot;
    }

    public boolean equals(String version) {
        return version().equals(version);
    }

    public boolean equals(NuxeoVersion version) {
        return version != null && equals(version.version());
    }

    @Override
    public int hashCode() {
        return Objects.hash(majorVersion, minorVersion, hotfix, snapshot);
    }

    @Override
    public boolean equals(Object version) {
        if (version instanceof String) {
            return equals((String) version);
        } else if (version instanceof NuxeoVersion) {
            return equals((NuxeoVersion) version);
        }
        return false;
    }

    @Override
    public String toString() {
        return version();
    }

    /**
     * @return A new instance of {@link NuxeoVersion} with the same version + input hotfix number.
     */
    public NuxeoVersion hotfix(int hotfix) {
        return new NuxeoVersion(majorVersion, minorVersion, -1, hotfix, false);
    }

    /**
     * @return Whether or not the input version is greater than this one.
     */
    public boolean isGreaterThan(String version) {
        return isGreaterThan(parse(version));
    }

    /**
     * @return Whether or not the input version is greater than this one.
     */
    public boolean isGreaterThan(NuxeoVersion version) {
        if (majorVersion > version.majorVersion()
                || majorVersion == version.majorVersion() && minorVersion > version.minorVersion()) {
            // Check two cases:
            // - major is greater
            // - same major and minor is greater
            return true;
        } else if (majorVersion == version.majorVersion() && minorVersion == version.minorVersion()) {
            // Check build/hotfix only if major and minor are equals
            if (buildVersion == -1 && version.buildVersion() == -1) {
                // Here we assume that (X+1.Y') contains the needed fix in X.Y-HFZZ
                return hotfix >= version.hotfix();
            } else {
                // Here we assume that a build version doesn't have HF
                return buildVersion >= version.buildVersion();
            }
        }
        return false;
    }

    public static NuxeoVersion parse(String version) {
        Matcher matcher = NUXEO_VERSION_PATTERN.matcher(version);
        if (!matcher.matches()) {
            throw new NuxeoClientException(
                    "Input version=" + version + " doesn't represent a valid Nuxeo server version");
        }
        int majorVersion = Integer.parseInt(matcher.group(1));
        int minorVersion = Integer.parseInt(matcher.group(2));
        int buildVersion = -1;
        if (matcher.group(3) != null) {
            buildVersion = Integer.parseInt(matcher.group(3));
        }
        int hotfix = 0;
        if (matcher.group(4) != null) {
            hotfix = Integer.parseInt(matcher.group(4));
        }
        boolean snaphot = matcher.group(5) != null;
        return new NuxeoVersion(majorVersion, minorVersion, buildVersion, hotfix, snaphot);
    }

}
