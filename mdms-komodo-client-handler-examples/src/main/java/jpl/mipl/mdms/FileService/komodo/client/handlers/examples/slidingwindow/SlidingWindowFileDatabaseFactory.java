package jpl.mipl.mdms.FileService.komodo.client.handlers.examples.slidingwindow;

import java.util.Properties;

/**
 * Factory instantiates instances of SlidingWindowFileDatabase based on the value
 * of a designated property 'slidingwindow.database.implementation', which can
 * be set in a passed-in Properties map or via the System properties.
 * 
 * @author Nicholas Toole   (Nicholas.T.Toole@jpl.nasa.gov)
 * @version $Id: SlidingWindowFileDatabaseFactory.java,v 1.1 2011/05/25 22:18:46 ntt Exp $
 */

public class SlidingWindowFileDatabaseFactory
{
    //---------------------------------------------------------------------
    
    /**
     * Empty constructor.
     */
    
    public SlidingWindowFileDatabaseFactory()
    {        
    }
    
    //---------------------------------------------------------------------
    
    /**
     * Retrieves the property value for 'slidingwindow.database.implementation'
     * for the class instance to be used for database interfacing.
     */
    
    public SlidingWindowFileDatabase getDatabase(Properties properties) throws Exception
    {                       
        if (properties == null)
            properties = System.getProperties();
        

        String clazzname = properties.getProperty(
                                        Configuration.PROPERTY_DATABASE_IMPLEMENTATION);        
        
        return getDatabase(clazzname);  
    }
    
    //---------------------------------------------------------------------
    
    public SlidingWindowFileDatabase getDatabase(String implementation) throws Exception
    {
        SlidingWindowFileDatabase sfDb = null;
        
        try {
            Class clazz = null;
            String clazzname = implementation;
            
            ClassLoader cLoader = SlidingWindowFileDatabaseFactory.class.getClassLoader();
            clazz = cLoader.loadClass(clazzname);

            Object o = clazz.newInstance();
            if (o instanceof SlidingWindowFileDatabase)
            {
                sfDb = (SlidingWindowFileDatabase) o;
                
            }
        } catch (Exception ex) {
            //ex.printStackTrace();
            if (sfDb != null)
            {
                sfDb.close();            
                sfDb = null;
            }
            throw ex;
        }
        
        return sfDb;
    }
    
    //---------------------------------------------------------------------
    
}
