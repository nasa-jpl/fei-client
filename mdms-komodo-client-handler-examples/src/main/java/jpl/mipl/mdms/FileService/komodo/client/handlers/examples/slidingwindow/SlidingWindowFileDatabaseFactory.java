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

import java.util.Properties;

/**
 * Factory instantiates instances of SlidingWindowFileDatabase based on the value
 * of a designated property 'slidingwindow.database.implementation', which can
 * be set in a passed-in Properties map or via the System properties.
 * 
 * @author Nicholas Toole   (Nicholas.T.Toole@jpl.nasa.gov)

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
