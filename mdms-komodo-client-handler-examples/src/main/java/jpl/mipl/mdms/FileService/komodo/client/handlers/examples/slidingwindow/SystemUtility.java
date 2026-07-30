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

import java.io.File;
import java.io.IOException;

import jpl.mipl.mdms.utils.logging.Logger;

/**
 * <B>Purpose:<B>
 * System utility is responsible for system level operations
 * such as space metrics and file deletions.
 * 
 * Until support for Java 6 is set, the utility accepts
 * a total space value as an argument or part of the
 * handler properties.
 *
 * @author Nicholas Toole (Nicholas.T.Toole@jpl.nasa.gov)

 *
 */
public class SystemUtility
{
    
    //-----------------------------------------------------------------
    
    public static final int CODE_FILE_DELETED  = 0;
    public static final int CODE_FILE_MISSING  = 1;
    public static final int CODE_PRESERVE_FILE = 2;
    public static final int CODE_DELETE_ERROR  = 3;
    
    
    private final Logger _logger = Logger.getLogger(SystemUtility.class.getName());
    
    //-----------------------------------------------------------------
    
    public SystemUtility()
    {        
    }
    
    //-----------------------------------------------------------------
//    
//    public long getUsedSpace() throws HandlerException
//    {
//        long size = 0;
//    
//        try {
//            size = database.getTotalFilesize(null, null);
//        } catch (SQLException sqlEx) {
//            throw new HandlerException("Exception occurred while getting total "+
//                                       " size: "+sqlEx.getMessage(), sqlEx);
//        }
//        return size;
//    }
    
    //-----------------------------------------------------------------
    
    public boolean deleteFile(File file, boolean pruneEmptyDirectories) 
                                                        throws IOException
    {       
        boolean deleted = false;
        
        if (file.exists())
        {
            if (!file.canWrite())
            {
                throw new IOException("Cannot delete file.  No write " +
                      "access: "+file.getAbsolutePath());
            }
            
            deleted = file.delete();
        }
        
        if (pruneEmptyDirectories)
        {
            File parent = file.getParentFile();
            String[] contents = parent.list();
            
            //while parent dir contains no children, go up to its parent
            //and delete the emptry child.  Keep this up until reaching 
            //go up to its p
            while (contents != null && contents.length == 0)
            {
                parent.delete();
                parent = parent.getParentFile();
                contents = parent.list();                    
            }
        }
        
        return deleted;
    }

    //-----------------------------------------------------------------
    
    public int attemptDeleteFileOnDisk(FileRecord record)
    {
        return attemptDeleteFileOnDisk(record, false, false);
    }
    
    //-----------------------------------------------------------------
    
    public int attemptDeleteFileOnDisk(FileRecord record,
                                       boolean preserveNewerFiles,
                                       boolean pruneEmptyDirs)
    {
        int returnCode = CODE_PRESERVE_FILE;
        
        String dirpath  = record.getLocation();
        String filename = record.getFilename();
        long   filesize = record.getFilesize();
        long   cliModTm = record.getClientModified();

        boolean shouldDelete  = true;
        boolean fileDeleted   = false;

        File tmpFile = new File(dirpath + File.separator + filename);

        //check if file exists
        if (!tmpFile.isFile())
        {
            shouldDelete = false;
            returnCode = CODE_FILE_MISSING;
        }
        else
        {                
            //determine if file is newer than database thinks 
            if (preserveNewerFiles && cliModTm != -1L)
            {
                long lastMod = tmpFile.lastModified();
                if (lastMod > 0 && cliModTm < lastMod)
                {
                    this._logger.trace(tmpFile.getAbsolutePath()+
                            " has been modified locally and " +
                    "preserve new files is enabled.");
                    shouldDelete = false;
                    returnCode = CODE_PRESERVE_FILE;
                }
            }
        }    
                
                
        //check that we should proceed with deletion
        if (shouldDelete)
        {
            try { 
                fileDeleted = this.deleteFile(tmpFile, pruneEmptyDirs);
                if (fileDeleted)
                {
                    returnCode = CODE_FILE_DELETED;
                }
                else
                {
                    returnCode = CODE_DELETE_ERROR;
                }                
            } catch (Exception ex) {
                ex.printStackTrace();
                returnCode = CODE_DELETE_ERROR;
//                
//                throw new HandlerException("Exception caught while deleting " +
//                                            "file: "+ex.getMessage(), ex);
            }
        }
        
        return returnCode;        
    }
    
    //-----------------------------------------------------------------
}
