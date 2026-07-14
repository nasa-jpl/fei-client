package jpl.mipl.mdms.FileService.komodo.client.handlers.examples.slidingwindow;

import java.util.Hashtable;
import java.util.Map;

import jpl.mipl.mdms.FileService.komodo.client.handlers.FileEventHandler;
import jpl.mipl.mdms.FileService.komodo.client.handlers.HandlerException;

/**
 * <B>Purpose:<B>
 * System info  is responsible for system level operations
 * such as space metrics.
 * 
 * Until support for Java 6 is accepted, the utility accepts
 * a total space value as an argument or part of the
 * handler properties.
 *
 * @author Nicholas Toole (Nicholas.T.Toole@jpl.nasa.gov)
 * @version $Id: SystemInfo.java,v 1.1 2011/05/25 22:18:47 ntt Exp $
 */

public class SystemInfo
{
    public static final String PROPERTY_DATABASE_MAX_USAGE = 
                                        "disk.soft.limit";

    public static final String PROPERTY_DATABASE_TOTAL_SPACE = 
                                        "disk.total.space";

    public static final long BYTES_IN_MEGABYTE = 1048576;

    protected Map<String, Long> constraintList;
    //protected FileEventHandler handler;

    //-----------------------------------------------------------------
    
    /**
     * Constructor
     * @param softLimit The size that is usable, in megabytes
     * @param hardLimit The maximum size of the storage device, in megabytes
     */
    public SystemInfo(long softLimit, long hardLimit) throws HandlerException
    {
        this.constraintList = new Hashtable<String, Long>();
       
                
        long softLimitBytes = softLimit < (Long.MAX_VALUE / BYTES_IN_MEGABYTE) ?
                              softLimit * BYTES_IN_MEGABYTE : Long.MAX_VALUE;
        long hardLimitBytes = hardLimit < (Long.MAX_VALUE / BYTES_IN_MEGABYTE) ?
                              hardLimit * BYTES_IN_MEGABYTE : Long.MAX_VALUE; 
        
 
        this.constraintList.put(PROPERTY_DATABASE_MAX_USAGE,   softLimitBytes);
        this.constraintList.put(PROPERTY_DATABASE_TOTAL_SPACE, hardLimitBytes);
    }

    //-----------------------------------------------------------------
    
    /**
     * Constructor
     * @param handler Instance of handler with required properties
     * included.
     */
    
    public SystemInfo(FileEventHandler handler) throws HandlerException
    {
        this.constraintList = new Hashtable<String, Long>();
        initFromHandlerProperties(handler);
    }
    
    //-----------------------------------------------------------------
    
    protected void initFromHandlerProperties(FileEventHandler handler)
                                             throws HandlerException
    {               
        String[] propNames = new String[] {
                PROPERTY_DATABASE_MAX_USAGE, 
                PROPERTY_DATABASE_TOTAL_SPACE};
        
        for (int i = 0; i < propNames.length; ++i)
        {
            String keyword = propNames[i];
            //String value = this.handler.getProperty(keyword);
            String value = handler.getProperty(keyword);
            if (value == null)
                throw new HandlerException("Handler properties does not contain a " +
                                           "value for "+keyword);
            
            Long valueAsLong;
            try {
                valueAsLong = Long.parseLong(value);
                this.constraintList.put(keyword, valueAsLong);
            } catch (NumberFormatException nfEx) {
                throw new HandlerException("Handler property could not be parsed " +
                        "as numeric. Property: "+keyword+", Value: "+value);
            }            
        }
           
        //---------------------
        //convert megabytes to bytes
        
        String[] propNamesInMegaBytes = new String[] {
                                    PROPERTY_DATABASE_MAX_USAGE, 
                                    PROPERTY_DATABASE_TOTAL_SPACE};
        
        for (int i = 0; i < propNamesInMegaBytes.length; ++i)
        {
            String keyword = propNamesInMegaBytes[i];
            long numberOfMegaBytes = this.constraintList.get(keyword);
            long numberOfBytes = numberOfMegaBytes * BYTES_IN_MEGABYTE;         
            this.constraintList.put(keyword, numberOfBytes);                
        }
    }
    
    //-----------------------------------------------------------------
    
    /**
     * Returns the total available disk space (regardless of free/used)
     * in units of bytes.
     * @return Available memory in bytes
     */
    public long getAvailableDiskSpace()
    {
        return constraintList.get(PROPERTY_DATABASE_TOTAL_SPACE);
    }
    
    //-----------------------------------------------------------------

    /**
     * Returns the limit of allowable disk space to be used by this handler.
     * This limit is enforced for successive single sessions of the handler,
     * multiple instances of this handler are unaware of each other, so the
     * total limit for all sessions would be num_sessions * limit.
     * @return Limit memory in bytes
     */
    public long getMaximumLimit()
    {
        return constraintList.get(PROPERTY_DATABASE_MAX_USAGE);
    }
    
    //-----------------------------------------------------------------
}
