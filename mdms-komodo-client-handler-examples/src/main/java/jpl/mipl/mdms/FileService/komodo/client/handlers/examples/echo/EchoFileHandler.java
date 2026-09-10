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

package jpl.mipl.mdms.FileService.komodo.client.handlers.examples.echo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import jpl.mipl.mdms.FileService.komodo.api.Constants;
import jpl.mipl.mdms.FileService.komodo.api.FileType;
import jpl.mipl.mdms.FileService.komodo.api.Result;
import jpl.mipl.mdms.FileService.komodo.api.SessionException;
import jpl.mipl.mdms.FileService.komodo.client.handlers.AbstractFileEventHandler;
import jpl.mipl.mdms.FileService.komodo.client.handlers.FileEventHandlerInfo;
import jpl.mipl.mdms.FileService.komodo.client.handlers.FileEventsContext;
import jpl.mipl.mdms.FileService.komodo.client.handlers.FileResultError;
import jpl.mipl.mdms.FileService.komodo.client.handlers.FileResultEvent;
import jpl.mipl.mdms.FileService.komodo.client.handlers.HandlerException;
import jpl.mipl.mdms.utils.logging.Logger;

public class EchoFileHandler extends AbstractFileEventHandler
{   
    OutputStream os;
    
    //Loggers
    private Logger _logger = Logger.getLogger(EchoFileHandler.class.getName());
    
    //---------------------------------------------------------------------
    
    public void initialize(FileEventsContext context, FileEventHandlerInfo metadata)
                                                             throws HandlerException 
    {
        super.initialize(context, metadata);
        
        _logger.trace("EchoFileHandler is ALIVE! HAHAHA!");
        
        setup();
    }
    
    //---------------------------------------------------------------------
    
    
    protected void setup() throws HandlerException 
    {
        //init class properties
        initFromProperties();                              
        
        //addShutdownHandler
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() 
            { 
                EchoFileHandler.this.close();
            }
        });
       
    }
    
    //---------------------------------------------------------------------
    
    protected void initFromProperties() throws HandlerException
    {        
        String cwd = System.getProperty("user.dir");
        
        String filename = "echo_"+System.currentTimeMillis()+".log";
        
        File file = new File(cwd, filename);

        _logger.trace("EchoFileHandler file location = "+file.getAbsolutePath());
        
        try {
            this.os = new FileOutputStream(file);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
    }

    //---------------------------------------------------------------------
    
    protected void writeMessage(String entry)
    {
        entry += "\n";
        
        try {
            this.os.write(entry.getBytes());
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        }
    }
    
    //---------------------------------------------------------------------

    /**
     * Implementation of the <code>FileEventHandler.eventOccurred()</code>.
     * 
     * @param event Instance of FileResultEvent  
     * @throws HandlerException if handler error occurs
     */
    
    public void eventOccurred(FileResultEvent event) throws HandlerException 
    {
        
        String taskType  = event.getTaskId();
        
        _logger.trace("EchoFileHandler event occured: "+taskType);
        
        
        Result result    = event.getResult();
        
        String ft = FileType.toFullFiletype(result.getServerGroup(), result.getType());
       
        String mesg = "Event occurred. Filetype="+ft+", EventType="+taskType+", Mesg="+result.getMessage()
                      + ", Errno=" + result.getErrno() + " for filename=" + result.getName();
        
        this.writeMessage(mesg);       
    }

    //---------------------------------------------------------------------
    

    /**
     * Implementation of the <code>FileEventHandler.errorOccurred()</code>.
     * If error parameter is wrapping an IOException, this handler will
     * check if disk space has run-out.  If so, then a cleanup is performed.
     * Otherwise, no action is taken.
     * @param error Instance of FileResultError  
     * @throws HandlerException if handler error occurs
     */
    
    public void errorOccurred(FileResultError error) throws HandlerException 
    {
        
        Throwable t = error.getThrowable();
        Result    r = error.getResult();
        
        String errorMessage = "";
        int    errorCode  = Constants.EXCEPTION;
        
        if (t instanceof SessionException)
        {
            SessionException sesEx = (SessionException) t;
            errorMessage = sesEx.getMessage();
            errorCode    = sesEx.getErrno();
        }
        if (r != null)
        {
            errorMessage = r.getMessage();
            errorCode    = r.getErrno();
        }
        
        _logger.trace("EchoFileHandler error occured: "+errorCode);
        
        String mesg = "!! ERROR occurred.  Mesg = "+errorMessage +
                      "Errno = " + errorCode;

        this.writeMessage(mesg);   
    }

    //---------------------------------------------------------------------
    
    
    //---------------------------------------------------------------------
    
    public void close() 
    {
        _logger.trace("EchoFileHandler is Closing! CHEERIO!");
        
        if (os != null)
        {
            try {
                os.flush();
                os.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    //---------------------------------------------------------------------
    
}
