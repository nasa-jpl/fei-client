/* ******************************************************************************
 * Copyright (C) 2022 California Institute of Technology. All rights reserved US
 * Government Sponsorship under NASA contract NAS7-918 is acknowledged
 ***************************************************************************** */
package jpl.mipl.mdms.FileService.komodo.client;

import java.io.InputStream;
import java.util.Properties;

/**
 * Reads the client version string from the build-time generated
 * version.properties resource (injected by Maven resource filtering).
 */
class VersionInfo {

    static final String CLIENT_VERSION_STR;

    static {
        String version = "FEI5 release (unknown)";
        try (InputStream in = VersionInfo.class.getResourceAsStream(
                "resources/version.properties")) {
            if (in != null) {
                Properties props = new Properties();
                props.load(in);
                String v = props.getProperty("client.version");
                if (v != null && !v.isBlank()) {
                    version = v;
                }
            }
        } catch (Exception ignored) {
            // fall through to default
        }
        CLIENT_VERSION_STR = version;
    }

    private VersionInfo() {}
}
