/*
 * Copyright (c) 2026 by the California Institute of Technology.
 * ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
 * Any commercial use must be negotiated with the Office of Technology
 * at the California Institute of Technology.
 *
 * The technical data in this document (or file) is controlled for export
 * under the Export Administration Regulations (EAR), 15 CFR, Parts 730-774.
 * Violations of these laws are subject to fines and penalties under the
 * Export Administration Act.
 */

/*
 * Created on Mar 22, 2005
 */
package jpl.mipl.mdms.FileService.komodo.ui.savannah.subscription.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jpl.mipl.mdms.FileService.komodo.ui.savannah.subscription.MetaParameters;

/**
 * <b>Purpose:</b>
 * Interface for the I/O class the reads and writes meta-subscription
 * parameters from and to files, respectively.
 *
 *   <PRE>
 *   Copyright 2005, California Institute of Technology.
 *   ALL RIGHTS RESERVED.
 *   U.S. Government Sponsorship acknowledge. 2005.
 *   </PRE>
 *
 * <PRE>
 * ============================================================================
 * <B>Modification History :</B>
 * ----------------------
 *
 * <B>Date              Who              What</B>
 * ----------------------------------------------------------------------------
 * 03/22/2005        Nick             Initial Release
 * ============================================================================
 * </PRE>
 *
 * @author Nicholas Toole   (Nicholas.T.Toole@jpl.nasa.gov)

 *
 */

public interface MetaParameterIO
{
    public static final int FORMAT_PLAIN = 0;
    public static final int FORMAT_XML   = 1;

    /**
     * Write contents of parameter instance to output stream
     * @param params metaparameter instance to be written
     * @param os Output stream instance
     * @param format Format flag (FORMAT_{PLAIN,XML})
     * @throws IOException if error occurs
     */

    public void write(MetaParameters params,
                      OutputStream os,
                      int format)  throws IOException;

    //---------------------------------------------------------------------

    /**
     * Read in parameter values from input stream
     * @param params metaparameter instance to be set
     * @param is Input stream instance
     * @param format Format flag (FORMAT_{PLAIN,XML})
     * @throws IOException if error occurs
     */

    public void read(MetaParameters params,
                     InputStream is,
                     int format) throws IOException;
}
