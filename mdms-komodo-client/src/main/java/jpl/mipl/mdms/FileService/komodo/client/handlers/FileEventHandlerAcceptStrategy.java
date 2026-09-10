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
 * <B>Purpose:<B>
 * Interface for classes that determine what handlers should
 * be loaded based upon the implementations accept()
 * strategy.
 *
 * @author Nicholas Toole (Nicholas.T.Toole@jpl.nasa.gov)

 *
 */
public interface FileEventHandlerAcceptStrategy
{

    /**
     * Initializes instance of handler factory accept strategy using argument 
     * options and actionId.
     * @param options Argument options from client
     * @param actionId Id of operation
     * @throws IllegalArgumentException if a required argument is
     * missing from options map.
     */

    public abstract void initialize(Map options, String actionId)
                                   throws IllegalArgumentException;

    
    
    /**
     * Returns true if strategy determined that handlers should be loaded.
     * @return True to load, false otherwise
     */

    public abstract boolean accept();

}