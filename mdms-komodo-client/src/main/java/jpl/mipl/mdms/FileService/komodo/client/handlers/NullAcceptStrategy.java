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

package jpl.mipl.mdms.FileService.komodo.client.handlers;

import java.util.Map;

import jpl.mipl.mdms.FileService.komodo.client.CMD;

/**
 * Default implementation of the registry strategy, which
 * always returns false for <code>accept()</code>.
 */

public class NullAcceptStrategy implements
                                   FileEventHandlerAcceptStrategy
{    
    //---------------------------------------------------------------------
    
    public void initialize(Map options, String actionId) throws IllegalArgumentException
    {             
    }
    
    //-----------------------------------------------------------------------
    
    /* (non-Javadoc)
     * @see jpl.mipl.mdms.FileService.komodo.client.handlers.FileEventHandlerAcceptStrategy#accept()
     */
    
    public boolean accept()
    {
        return false;
    }

    //---------------------------------------------------------------------
}
