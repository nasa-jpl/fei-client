/* ******************************************************************************
 * Copyright (C) 2022 California Institute of Technology. All rights reserved US
 * Government Sponsorship under NASA contract NAS7-918 is acknowledged
 ***************************************************************************** */
package jpl.mipl.mdms.FileService.komodo.client;


/**
 * <b>Purpose:</b>
 * Extension to the core interface which defined constants and error codes for the 
 * Komodo API, with client specific extensions
 */
 
 public interface Constants extends jpl.mipl.mdms.FileService.komodo.api.Constants
 {
    public static final String CLIENTVERSIONSTR = VersionInfo.CLIENT_VERSION_STR;
 }
 