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

package jpl.mipl.mdms.FileService.komodo.ui.savannah.logging;

import jpl.mipl.mdms.utils.logging.Log4JPlugin;
import org.apache.logging.log4j.Level;

/**
 * <b>Purpose:</b>
 * Translates priority levels from one framework to that
 * used by the <code>LogEntry</code> class.
 *
 *   <PRE>
 *   Copyright 2004, California Institute of Technology.
 *   ALL RIGHTS RESERVED.
 *   U.S. Government Sponsorship acknowledge. 2004.
 *   </PRE>
 *
 * <PRE>
 * ============================================================================
 * <B>Modification History :</B>
 * ----------------------
 *
 * <B>Date              Who              What</B>
 * ----------------------------------------------------------------------------
 * 12/03/2004        Nick             Initial Release
 * 09/27/2005        Nick             Added Benchmark level
 * ============================================================================
 * </PRE>
 *
 * @author Nicholas Toole   (Nicholas.T.Toole@jpl.nasa.gov)

 *
 */

public class LevelTranslation
{
    public static int translate(int level)
    {
        if (Level.FATAL.intLevel() == level) {
            return LogEntry.LEVEL_FATAL;
        }
        if (Level.ERROR.intLevel() == level) {
            return LogEntry.LEVEL_ERROR;
        }
        if (Level.WARN.intLevel() == level) {
            return LogEntry.LEVEL_WARN;
        }
        if (Level.INFO.intLevel() == level) {
            return LogEntry.LEVEL_INFO;
        }
        if (Log4JPlugin.BENCH.intLevel() == level) {
            return LogEntry.LEVEL_BENCHMARK;
        }
        if (Level.DEBUG.intLevel() == level) {
            return LogEntry.LEVEL_DEBUG;
        }
        if (Level.TRACE.intLevel() == level) {
            return LogEntry.LEVEL_TRACE;
        }
        return LogEntry.LEVEL_UNKNOWN;
    }
}
