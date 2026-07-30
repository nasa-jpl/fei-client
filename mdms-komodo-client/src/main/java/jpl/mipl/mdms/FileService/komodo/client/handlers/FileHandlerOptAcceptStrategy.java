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
 * <B>Purpose:<B>
 * Implementation of FileEventHandlerAcceptStrategy that enables
 * handlers for the filehandler options.  
 * The options passed in via initialize is examined for all 
 * necessary properties, (servergroup, filetype, output dir) and 
 * passes if the filehandler option is set.
 * 
 * @see CMD for names of properties.
 *
 * @author Nicholas Toole (Nicholas.T.Toole@jpl.nasa.gov)

 *
 */
public class FileHandlerOptAcceptStrategy implements FileEventHandlerAcceptStrategy
{   
    boolean accept = false;

    //-----------------------------------------------------------------------
    
    public FileHandlerOptAcceptStrategy()
    {        
    }
    
    //-----------------------------------------------------------------------
    
    /* (non-Javadoc)
     * @see jpl.mipl.mdms.FileService.komodo.client.handlers.FileEventHandlerAcceptStrategy#initialize(java.util.Map, int)
     */
    
    public void initialize(Map options, String actionId) throws IllegalArgumentException
    {        
        String sg  = (String) options.get(CMD.SERVERGROUP);
        if (sg == null)
            throw new IllegalArgumentException("Options parameter does not " +
                    "contain value for '"+CMD.SERVERGROUP+"'");

        
        String ft  = (String) options.get(CMD.FILETYPE);
        if (ft == null)
            throw new IllegalArgumentException("Options parameter does not " +
                    "contain value for '"+CMD.FILETYPE+"'");
        
        
        String dir = (String) options.get(CMD.OUTPUT);        
        if (dir == null)
            dir = System.getProperty("user.dir");
        
        boolean fileHandleEnabled = options.containsKey(CMD.FILEHANDLER) ? 
                                    ((Boolean) options.get(CMD.FILEHANDLER)).booleanValue():
                                    false;
    
        this.accept = fileHandleEnabled;        
    }
    
    //-----------------------------------------------------------------------
    
    /* (non-Javadoc)
     * @see jpl.mipl.mdms.FileService.komodo.client.handlers.FileEventHandlerAcceptStrategy#accept()
     */
    
    public boolean accept()
    {
        return this.accept;
    }
    
    //-----------------------------------------------------------------------
}
