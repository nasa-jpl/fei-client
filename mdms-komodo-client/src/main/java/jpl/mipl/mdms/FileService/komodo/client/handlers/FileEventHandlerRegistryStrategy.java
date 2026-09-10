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

/**
 * Encapsulates the behavior used to determine whether or not
 * file event handler registry will be loaded.  Implementations
 * should check for necessary conditions in the initialize() method.
 * If conditions are met, then calls to getHandlers() should
 * return handlers associated with registry.  Else, an empty
 * handler set should be returned.
 * @author ntt
 *
 */
public interface FileEventHandlerRegistryStrategy
{
    
    //---------------------------------------------------------------------
    
    public void initialize(Map options, int actionId) 
                                throws IllegalArgumentException;    
    
    //---------------------------------------------------------------------
    
    public FileEventHandlerSet getHandlers();
    
    //---------------------------------------------------------------------
    
}
