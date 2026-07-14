package jpl.mipl.mdms.FileService.komodo.client.handlers.examples.slidingwindow;

import java.util.Date;

import jpl.mipl.mdms.FileService.komodo.api.Result;

/**
 * Data structure represented a file record, including filename,
 * full filetype, location, filesize and modification time.
 * 
 * @author Nicholas Toole   (Nicholas.T.Toole@jpl.nasa.gov)
 * @version $Id: FileRecord.java,v 1.1 2011/05/25 22:18:46 ntt Exp $
 */
public class FileRecord
{
    String filename;
    String location;
    String servergroup;
    String filetype;
    long   filesize;
    long   modified;
    long   clientModified;
    
    //---------------------------------------------------------------------
    
    public FileRecord(String name, String servergroup, String filetype, 
                      String location, long size, long modTime,
                      long clientModTime)
    {
        this.filename = name;
        this.servergroup = servergroup;
        this.filetype = filetype;
        this.location = location;
        this.filesize = size;
        this.modified = modTime;    
        this.clientModified = clientModTime;
    }

    //---------------------------------------------------------------------
    
    public FileRecord(String name, String servergroup, String filetype)
    {
        this(name, servergroup, filetype, null, -1L, -1L, -1L);    
    }
    
    //---------------------------------------------------------------------
    
    public FileRecord(String servergroup, String filetype)
    {
        this(null, servergroup, filetype, null, -1L, -1L, -1L);    
    }
    //---------------------------------------------------------------------
    
    public FileRecord(Result fileResult, long clientModTime)
    {
        //result SHOULD have a name
        this.filename = fileResult.getName();

        this.location = fileResult.getLocalLocation();
        this.filesize = fileResult.getSize();
        
        this.servergroup = fileResult.getServerGroup();
        this.filetype = fileResult.getType();
//        
//        if (sg != null && ft != null)
//            this.filetype = FileType.toFullFiletype(sg, ft);
//        else
//            this.filetype = null;
//        
        
        Date modDate = fileResult.getFileModificationTime();
        if (modDate != null)
            this.modified = modDate.getTime();
        else 
            this.modified = -1L;
        
         this.clientModified = clientModTime;
        
    }
    
    //---------------------------------------------------------------------
    
    public FileRecord(Result fileResult)
    {
        this(fileResult, -1L);
    }
    
    //---------------------------------------------------------------------
    
    public String getFilename()
    {
        return filename;
    }

    //---------------------------------------------------------------------
    
    public String getLocation()
    {
        return location;
    }
    
    //---------------------------------------------------------------------
    
    public String getServerGroup()
    {
        return servergroup;
    }
    
    //---------------------------------------------------------------------
    
    public String getFiletype()
    {
        return filetype;
    }

    //---------------------------------------------------------------------
    
    public long getFilesize()
    {
        return filesize;
    }
    
    //---------------------------------------------------------------------

    public long getModified()
    {
        return modified;
    }

    //---------------------------------------------------------------------
    
    public long getClientModified()
    {
        return clientModified;
    }
    
    //---------------------------------------------------------------------
    
    
    //---------------------------------------------------------------------
    
}
