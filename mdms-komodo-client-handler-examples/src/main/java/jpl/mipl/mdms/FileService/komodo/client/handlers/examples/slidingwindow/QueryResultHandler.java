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

package jpl.mipl.mdms.FileService.komodo.client.handlers.examples.slidingwindow;

import java.sql.ResultSet;

/**
 * <B>Purpose:<B>
 * Interface for query ResultSet handler that performs an action
 * for each result set entry.
 * 
 * Errors can be retrieved via the getError() method.
 *
 * @author Nicholas Toole (Nicholas.T.Toole@jpl.nasa.gov)

 *
 */
public interface QueryResultHandler
{
    //---------------------------------------------------------------------
    
    /**
     * Perform some action based on result set current
     * entry.  Recommended that the result set not be
     * advanced to next entry in this method.
     * @param resultSet ResultSet with pre-set entry
     */
    
    public void handleResultSet(ResultSet resultSet);
    
    //---------------------------------------------------------------------
    
    /**
     * Returns error object that may have been set during
     * processing, null if no error.
     * @return Error object or null
     */
    
    public Object getError();
    
    //---------------------------------------------------------------------
    
    /**
     * Returns true if error was found, false otherwise.
     * @return true if error, false otherwise
     */
    public boolean hasError();
    
    //---------------------------------------------------------------------
}
