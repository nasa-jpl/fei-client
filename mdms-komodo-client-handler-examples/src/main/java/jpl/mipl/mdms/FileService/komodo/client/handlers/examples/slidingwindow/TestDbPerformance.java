package jpl.mipl.mdms.FileService.komodo.client.handlers.examples.slidingwindow;

import java.util.Properties;

/**
 * <B>Purpose:<B>
 * Testing class that adds/updates entries into an SlidingWindowFileDatabase
 * implementation.
 *
 * @author Nicholas Toole (Nicholas.T.Toole@jpl.nasa.gov)
 * @version $Id: TestDbPerformance.java,v 1.1 2011/05/25 22:18:47 ntt Exp $
 *
 */
public class TestDbPerformance
{
    
    String dbLocation, dbName;
    Properties dbProperties;
    SlidingWindowFileDatabase database;
    
    long maxCount = 10000000;
    long stepSize = 10000;
    
    //---------------------------------------------------------------------
    
    public TestDbPerformance(String databaseName, String databaseLocation) throws Exception
    {
        
        this.dbLocation   = databaseLocation;
        this.dbName       = databaseName;
        this.dbProperties = new Properties();
        
        init();          
    }
    
    //---------------------------------------------------------------------
    
    protected void init() throws Exception
    {
        this.database = instantiateDatabase();
        
        try { 
            this.database.initialize(this.dbName, this.dbLocation, this.dbProperties);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }        
        
        System.out.println("Database location: "+this.dbLocation);
        System.out.println("Database name    : "+this.dbName);
    }
    
    //---------------------------------------------------------------------

    protected SlidingWindowFileDatabase instantiateDatabase() throws Exception
    {
        //return new SlidingWindowDerbyFileDatabase();
        String propName = System.getProperty(Configuration.PROPERTY_DATABASE_IMPLEMENTATION,
                                             Configuration.DEFAULT_DATABASE_IMPLEMENTATION);

        SlidingWindowFileDatabase db = null;

        if (propName != null)
        {
            SlidingWindowFileDatabaseFactory factory = new SlidingWindowFileDatabaseFactory();

            db = factory.getDatabase(propName);
        }

        return db;
    }
    
    //---------------------------------------------------------------------
    
    public void testInserts()
    {
        FileRecord currentRecord;
        long count = 0;
        String sg = "servergroup";
        String ft = "filetype";
        String loc = "/some/directory/location/";
        long size = 123456789;
        long modTime = System.currentTimeMillis();
        String namePrefix = "FILE_NUMBER_";
        
        long lastTime = 0;
        
        try {
            
            for (long i = 0; i < this.maxCount; ++i)
            {
                if (i % this.stepSize == 0)
                {
                    long curTime = System.currentTimeMillis();
                    long diffTime = 0;
                    if (lastTime > 0)
                    {
                        diffTime = curTime - lastTime;
                    }
                    lastTime = curTime;
                    
                    System.out.println("*************************************");
                    System.out.println("Adding entry # "+i);
                    System.out.println("Total Memory = "+Runtime.getRuntime().totalMemory());
                    System.out.println("Number of seconds = "+(diffTime / 1000));    
                }
                
                String curName = namePrefix + count++;    
                //curName = "samename";
                currentRecord = new FileRecord(curName, sg, ft, loc, size,
                                      modTime, System.currentTimeMillis());
            
                this.database.addRecord(currentRecord);
            }
            
        } catch (Exception ex) {
            System.out.println("Exception! Message = "+ex.getMessage());
            System.out.println("Current entry = "+count);
            System.out.println("Total Memory  = "+Runtime.getRuntime().totalMemory());
            ex.printStackTrace();
            
        } catch (Throwable t) {
            System.out.println("Throwable! Message = "+t.getMessage());
            System.out.println("Current entry = "+count);
            System.out.println("Total Memory  = "+Runtime.getRuntime().totalMemory());
            t.printStackTrace();
        }
        
    }
    
    //---------------------------------------------------------------------
    
    
    public static void main(String[] args)
    {
        if (args.length < 2)
        {
            System.err.println("Expected arguments: name location ");
            System.exit(1);
        }
        
        String name = args[0];
        String location = args[1];
        
        
        TestDbPerformance test = null;
        
        try {
            test = new TestDbPerformance(name, location);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        
        test.testInserts();
        
    }
    
    //---------------------------------------------------------------------
    
}
