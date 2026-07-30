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

import jpl.mipl.mdms.FileService.komodo.api.Result;

/**
 * <B>Purpose:<B>
 * Data type encapsulating the operation and Komodo Result 
 * associated with a file event.
 *
 * @author Nicholas Toole (Nicholas.T.Toole@jpl.nasa.gov)

 *
 */
public class FileResultEvent
{
    protected Result _result;
    protected String _taskId;
    
    public FileResultEvent(String taskId, Result result)
    {
         this._result = result;
         this._taskId = taskId;        
    }
    
    public Result getResult()
    {
        return this._result;
    }
    
    public String getTaskId()
    {
        return this._taskId;
    }        
}
