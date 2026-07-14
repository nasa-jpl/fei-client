package jpl.mipl.mdms.FileService.komodo.client.handlers.examples.slidingwindow;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import jpl.mipl.mdms.FileService.komodo.api.Constants;
import jpl.mipl.mdms.FileService.komodo.api.Result;
import jpl.mipl.mdms.FileService.komodo.api.SessionException;
import jpl.mipl.mdms.FileService.komodo.client.handlers.AbstractFileEventHandler;
import jpl.mipl.mdms.FileService.komodo.client.handlers.FileEventHandlerInfo;
import jpl.mipl.mdms.FileService.komodo.client.handlers.FileEventsContext;
import jpl.mipl.mdms.FileService.komodo.client.handlers.FileResultError;
import jpl.mipl.mdms.FileService.komodo.client.handlers.FileResultEvent;
import jpl.mipl.mdms.FileService.komodo.client.handlers.HandlerException;
import jpl.mipl.mdms.utils.logging.Logger;

/**
 * The primary purpose of this handler is to ensure a limit on disk
 * space usage for subscribed files at remote locations.
 * <BR>
 * An embedded-database is used to store file information to 
 * ensure tracking to facilitate LRU-based file removal for
 * the cleanup process when the space limit is passed.
 * 
 * The location of the database is a hidden komodo directory
 * directly underneath the output directory as set in the
 * context (i.e. /sys/fei/output/dir/.komodo).  The name of the
 * database is the servergroup name.
 * <BR>
 * There are various settings for this handler that can be set
 * via non-system properties.  
 * 
 * <PRE>
 * Handler Properties:
 * 
 * ++ Property: disk.soft.limit
 * -- Description: Indicates the amount of disk space, which, when 
 * crossed, triggers a cleanup of older file content from the client.   
 * NOTE: This is not a hard-maximum, as only when it is exceeded
 * will a cleanup be performed.
 * -- Type: number of megabytes (MB) (long)
 * -- Required: Yes
 *
 * ++ Property: disk.total.space
 * -- Description: Indicates the total amount of space on the partition.
 * NOTE: This property is a work around while waiting for Java 6.0 to 
 * be adopted, as it contains file disk methods for ascertaining this
 * information within the system.
 * -- Type: number of megabytes (MB) (long)
 * -- Required: Yes
 *
 * ++ Property: disk.cleanup.ratio
 * -- Description: Ratio of the used disk space to be freed during a 
 * cleanup, so that old  files will be removed until the sum of the 
 * filesize just reaches or passes (ratio * maxUsage).  
 * Default: 0.25 (25%)
 * -- Type: Ratio, between 0.0 and 1.0 (float)
 * -- Required: No
 *
 * ++ Property: prune.empty.directories
 * -- Description: Informs handler that it should remove directories if,
 * during cleanup, they become empty.  This effect cascades up the parent
 * directory path until a non-empty directory is encountered. Default: 
 * false.
 * -- Type: true/false
 * -- Required: No
 *
 * ++ Property: preserve.newer.files
 * -- Description: Informs handler that it should delete files if
 * during cleanup, their client-based modification time is not 
 * newer than the recorded value. This allows files that are updated
 * since download to be preserved during cleanup. Default: false. 
 * -- Type: true/false
 * -- Required: No
 * 
 * ++ Property: query.batch.size
 * -- Description: During a cleanup operation, it is possible for a large
 * number of files to be queried.  The value of this property indicates 
 * how many files should be queried for each iteration, until cleanup
 * frees enough disk space.  Default: 50
 * -- Type: count (int)
 * -- Required: No
 *
 * ++ Property: slidingwindow.database.implementation
 * -- Description: Indicates the fully-qualified class name of the 
 * implementation of the SlidingWindowFileDatabase interface used by the
 * deployment.  Class must be discoverable via the system classpath.
 * -- Type: Fully-qualified class (string)
 * -- Required: Yes
 *
 * ++ Property: database.properties.location
 * -- Description: Indicates the location of database properties file.
 * This properties file will be parsed and used by the database.
 * -- Type: Filepath (string)
 * -- Required: Dependent upon database implementation
 *
 * </PRE>
 * 
 * Values to the properties can be set in the properties
 * element node  of the handler plugin descriptor or overridden 
 * via a local file.  The local file is specified by setting its 
 * location as the value of the system property 
 * 'jpl.mipl.mdms.fei.handlers.slidingwindow.properties'
 * OR by including a file named 'komodo.filehandling.slidingwindow.props'
 * in the output directory.  Priority is given to the output 
 * directory local file.
 * 
 * @author Nicholas Toole   (Nicholas.T.Toole@jpl.nasa.gov)
 * @version $Id: SlidingWindowFileHandler.java,v 1.1 2011/05/25 22:18:46 ntt Exp $
 * 
 */

public class SlidingWindowFileHandler extends AbstractFileEventHandler
{
    
    public static final String HANDLER_TITLE = "FIFO Sliding Window Handler";
    
    public static final String PROPERTY_SYSTEM_OVERRIDE_PROPERTIES_FILE = 
                                "jpl.mipl.mdms.fei.handlers.slidingsindow.properties";
    
    public static final String LOCAL_OVERRIDE_PROPERTIES_FILE = 
                                "komodo.filehandling.slidingwindow.props";
    

    
    public static final String DATABASE_NAME_PREFIX = "";
    public static final String DATABASE_NAME_SUFFIX = "";
    
    public static final long BYTES_IN_MEGABYTE = 1048576;
    
    
    
    //database impl
    protected SlidingWindowFileDatabase database;
    
    //system utility serves disk space information
    protected SystemInfo systemInfo;
    
    //system utility that deletes files and whatnot
    protected SystemUtility systemUtility;
    
    //database specific properties from db props file
    protected Properties dbProperties;

    //clean up properties
    protected boolean pruneEmptyDirectories = false;    
    protected boolean preserveNewerModFiles = false;
    protected double makespaceRatio = 0.25;
    protected int makespaceBatchSize = 50; 
    
    protected boolean logDeleteMessages = true;
    
    
//    protected final int CODE_FILE_DELETED  = 0;
//    protected final int CODE_FILE_MISSING  = 1;
//    protected final int CODE_PRESERVE_FILE = 2;
//    protected final int CODE_DELETE_ERROR  = 3;
    
    //Loggers
    private final Logger _logger = Logger.getLogger(SlidingWindowFileHandler.class.getName());
    
    //---------------------------------------------------------------------
    
    public void initialize(FileEventsContext context, FileEventHandlerInfo metadata)
                                                             throws HandlerException 
    {
        super.initialize(context, metadata);
        
        setup();
    }
    
    //---------------------------------------------------------------------
    
    
    protected void setup() throws HandlerException 
    {
        //check for overrides to defaults
        checkForPropertyOverrides();
        
        //init class properties
        initFromProperties();                              
        
        
        //addShutdownHandler
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() 
            {                 
                SlidingWindowFileHandler.this.close();
            }
        });
        
        //lookup database file
        //load database to memory
        loadDatabase();
        
        this._logger.debug("Fifo subscription handler initialized.");
    }
    

    
    //---------------------------------------------------------------------
    
    protected void initFromProperties() throws HandlerException
    {       
        
        //determine if empty dir pruning applies
        String value = this._properties.getProperty(Configuration.PROPERTY_CLEANUP_PRUNE_DIRS, 
                                                    Configuration.DEFAULT_CLEANUP_PRUNE_DIRS);
        this.pruneEmptyDirectories = Boolean.parseBoolean(value);    

        //determine if preserve newer mod files applies
        value = this._properties.getProperty(Configuration.PROPERTY_PRESERVE_LOCAL_MODIFIED, 
                                             Configuration.DEFAULT_PRESERVE_LOCAL_MODIFIED);
        this.preserveNewerModFiles = Boolean.parseBoolean(value);    
        
        //get make space ratio 
        value = this._properties.getProperty(Configuration.PROPERTY_DATABASE_SPACE_RATIO, 
                                             Configuration.DEFAULT_DATABASE_SPACE_RATIO);
        try {
            this.makespaceRatio = Double.parseDouble(value);
            if (this.makespaceRatio <= 0.0 || this.makespaceRatio >= 1.0)
                this.makespaceRatio = Double.parseDouble(
                        Configuration.DEFAULT_DATABASE_SPACE_RATIO);            
        } catch (NumberFormatException nfEx) {
            throw new HandlerException("Exception occurred while parsing " +
            		            "makespace ratio: "+nfEx.getMessage(), nfEx);
        }
        
        
        //get make space ratio 
        value = this._properties.getProperty(Configuration.PROPERTY_DATABASE_BATCH_SIZE, 
                                             Configuration.DEFAULT_DATABASE_BATCH_SIZE);
        try {
            this.makespaceBatchSize = Integer.parseInt(value);
        } catch (NumberFormatException nfEx) {
            throw new HandlerException("Exception occurred while parsing " +
                           "database batch size: "+nfEx.getMessage(), nfEx);
        }
        
        //get log cleanup messages flag
        value = this._properties.getProperty(Configuration.PROPERTY_LOG_CLEAN_MESSASGES, 
                                             Configuration.DEFAULT_LOG_CLEAN_MESSAGES);
        this.logDeleteMessages = Boolean.parseBoolean(value);                       
        
        //-------------------------
        
        //create new system utility
        this.systemInfo    = new SystemInfo(this);
        this.systemUtility = new SystemUtility();
        
        
        lookupDbPropertiesFile();
        
        
//        this.constraintList = new Hashtable<String, Long>();
//        
//        String[] propNames = new String[] {
//                PROPERTY_DATABASE_MAX_USAGE, 
//                PROPERTY_DATABASE_TOTAL_SPACE,
//                PROPERTY_DATABASE_SPACE_RATIO};
//        
//        for (int i = 0; i < propNames.length; ++i)
//        {
//            String value = this._properties.getProperty(propNames[i]);
//            if (value == null)
//                throw new Exception("Handler properties does not contain a " +
//                		            "value for "+propNames[i]);
//            
//            Long valueAsLong;
//            try {
//                valueAsLong = Long.parseLong(value);
//                this.constraintList.put(value, valueAsLong);
//            } catch (NumberFormatException nfEx) {
//                throw new Exception("Handler property could not be parsed " +
//                		"as numeric. Property: "+propNames[i]+", Value: "+value);
//            }            
//        }
        
    }

    //---------------------------------------------------------------------
    
    /**
     * Checks for resources containing property overrides.  First, a check
     * is made in the output directory for a file named the value of
     * the constant <code>LOCAL_OVERRIDE_PROPERTIES_FILE</code>, currently
     * "komodo.filehandling.slidingwindow.props".  If not found,
     * then the Java property <code>PROPERTY_SYSTEM_OVERRIDE_PROPERTIES_FILE
     * </code> is checked.  If either is found, its properties are
     * used to overwrite the defaults.
     */
    
    protected void checkForPropertyOverrides()
    {        
        InputStream is = null;
        
        //first check for local file
        String output = this._context.getDirectory();
        String localFileStr = output + File.separator + LOCAL_OVERRIDE_PROPERTIES_FILE;
        File localFile = new File(localFileStr);
        
        if (localFile.isFile() && localFile.canRead())
        {
            try {
                is = new FileInputStream(localFile);
                _logger.trace("Handler options override file specified at "+localFile);
            } catch (FileNotFoundException fnfEx) {
                is = null;
            }
        }
        
        
        if (is == null)
        {        
            String value = System.getProperty(PROPERTY_SYSTEM_OVERRIDE_PROPERTIES_FILE);
            
            if (value != null)
            {
                _logger.trace("Handler options override file specified at "+value);
                
                
                //check to see if its a local file on the file system
                File file = new File(value);
                if (file.canRead())
                {
                    try {
                        is = new FileInputStream(file);
                    } catch (IOException ioEx) {
                        is = null;
                    }
                }
                else //otherwise assume its a resource on classpath
                {
                    is = this.getClass().getClassLoader().getResourceAsStream(value);                   
                }
            }
        }
            
        //---------------------
                       
        if (is != null)
        {               
            Properties overrideProps = new Properties();
            try {
                
                overrideProps.load(is);               
                
                this._properties.putAll(overrideProps);
                    
            } catch (IOException ioEx) {
                ioEx.printStackTrace();
            }
        }       
            
        //---------------------

    }
        
    //---------------------------------------------------------------------
    
    protected void lookupDbPropertiesFile() throws HandlerException
    {
        this.dbProperties = new Properties();
        
        String value = this._properties.getProperty(Configuration.PROPERTY_DATABASE_PROPERTIES_LOCATION);
        if (value == null)
        {
            this._logger.debug("Handler properties does not contain a value for "+
                               Configuration.PROPERTY_DATABASE_PROPERTIES_LOCATION+
                               ".  Using empty properties.");
        }
//            throw new HandlerException("Handler properties does not contain a value for "+
//                                PROPERTY_DATABASE_PROPERTIES_LOCATION);
//        
        else
        {
            //-------------------------
            
            File dbPropsFile = new File(value);
            if (!dbPropsFile.canRead())
            {
                throw new HandlerException("Handler properties contains missing/unreadable " +
                                    "value for "+ 
                                    Configuration.PROPERTY_DATABASE_PROPERTIES_LOCATION);
            }
            
            //-------------------------
                    
            InputStream dbIs = null;
            try {
                dbIs = new FileInputStream(dbPropsFile);
                
                this.dbProperties.load(dbIs);
                
            } catch (Exception ex) {
                throw new HandlerException("Unable to load database properties from "+ 
                                           dbPropsFile.getAbsolutePath());
            }        
        }
        
        //-------------------------
        
    }
    
    //---------------------------------------------------------------------
    
    protected void loadDatabase() throws HandlerException
    {
//        //get the DB implementation from the properties
//        String classname = this._properties.getProperty(
//                                PROPERTY_DATABASE_IMPLEMENTATION);
//        if (classname == null)
//            throw new HandlerException("Cannot configure without " +
//            		                   "database implementation");
        
        SlidingWindowFileDatabaseFactory dbFactory = new SlidingWindowFileDatabaseFactory();
        
        try {
            this.database = dbFactory.getDatabase(this._properties);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new HandlerException("Exception occurred while creating " +
            		              "database instance: "+ex.getMessage(), ex);
        }  
        
        if (this.database == null)
        {
            throw new HandlerException("Could not instantiate database.");
        }
        
        //-------------------------

        String dbName      = this.getDatabaseName();
        String dbLocation  = this.getDatabaseLocation();
        
        try {
            this.database.initialize(dbName, dbLocation, this.dbProperties);
        } catch (HandlerException hEx) {
            throw hEx;
        } catch (Exception ex) {
            throw new HandlerException("Exception thrown while initializing " +
            		                   "database: "+ex.getMessage(), ex);
        }
        
        //-------------------------
    }
    

    //---------------------------------------------------------------------

    /**
     * Implementation of the <code>FileEventHandler.eventOccurred()</code>.
     * -File gets and subscriptions will add/update an entry in the database
     * tracking file receival time.
     * -File deletes will remove entry from database.
     * -All other tasks are ignored.
     * @param event Instance of FileResultEvent  
     * @throws HandlerException if handler error occurs
     */
    
    public void eventOccurred(FileResultEvent event) throws HandlerException 
    {
        String taskType  = event.getTaskId();
        Result result = event.getResult();
        
        if (taskType.equals(Constants.GETFILES) || 
            taskType.equals(Constants.AUTOGETFILES))
        {
            processNewFile(result);
        }
        else if (taskType.equals(Constants.DELETEFILE))
        {
            processDeletedFile(result);
        }
            
        //hacky way to enable the database to print during trace
        if (this._logger.isTraceEnabled())
            showRecords();
        
    }
    
    
    protected void showRecords()
    {
        try {
            System.out.println("Printing database records: ");
            this.database.printRecords(System.out);
            long totalSize = this.database.getTotalFilesize(null, null);
            System.out.println("TOTAL SIZE = " + totalSize +" bytes");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    //---------------------------------------------------------------------
    
    /**
     * In this implementation, we record the entry first, then perform
     * cleanup.  This avoids the case of accidently deleting an 
     * updated file.
     */
    protected boolean processNewFile(Result fileInfo) throws HandlerException
    {        
        addResultEntry(fileInfo);
        
        boolean makeSpace = shouldMakeSpace(0); 
        if (makeSpace)
        {
            makeSpace();
        }
        
        makeSpace = shouldMakeSpace(0);
        if (makeSpace)
        {
            throw new HandlerException("No space available following cleanup attempt");
        }        
        
        return true;        
    }
    
//    protected boolean processNewFile(Result fileInfo) throws HandlerException
//    {        
//        boolean makeSpace = shouldMakeSpace(fileInfo.getSize()); 
//        if (makeSpace)
//        {
//            makeSpace();
//        }
//        
//        makeSpace = shouldMakeSpace(fileInfo.getSize());
//        if (makeSpace)
//        {
//            throw new HandlerException("No space available following cleanup attempt");
//        }
//            
//        addResultEntry(fileInfo);
//        
//        return true;        
//    }
    
    //---------------------------------------------------------------------
    
    protected void addResultEntry(Result fileInfo) throws HandlerException
    {
        //create file record from Result info
        long clientModTime = getClientModTimeForResult(fileInfo);            
        FileRecord record = new FileRecord(fileInfo, clientModTime);
        
        
        try {
            
            //database should have duplicate records (i.e. replace)
            this.database.addRecord(record);
            
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();            
            throw new HandlerException("Error occurred while adding record " +
                        "to database.  Message = "+sqlEx.getMessage(), sqlEx);
        }
    }
    
    //---------------------------------------------------------------------
    
    protected long getClientModTimeForResult(Result result)
    {
        long clientModTime = -1L;
        
        String name = result.getName();
        String loca = result.getLocalLocation();
        
        String path = loca + File.separator + name;
        File file = new File(path);
        
        if (file.exists() && file.isFile())
        {
            clientModTime = file.lastModified();            
        }
                
        return clientModTime;
    }

    //---------------------------------------------------------------------
    
    protected boolean processDeletedFile(Result fileInfo) throws HandlerException
    {        
        removeResultEntry(fileInfo);
        
        return true;        
    }

    //---------------------------------------------------------------------
        
    protected void removeResultEntry(Result fileInfo) throws HandlerException
    {
        //create file record from Result info
        FileRecord record = new FileRecord(fileInfo);
        
        try {                      
            this.database.removeRecord(record);
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();            
            throw new HandlerException("Error occurred while deleting record " +
                      "from database.  Message = "+sqlEx.getMessage(), sqlEx);
        }
    }
    

    //---------------------------------------------------------------------
    

    /**
     * Implementation of the <code>FileEventHandler.errorOccurred()</code>.
     * If error parameter is wrapping an IO_ERROR, this handler will
     * check if disk space has run-out.  If so, then a cleanup is performed.
     * Otherwise, no action is taken.
     * @param error Instance of FileResultError  
     * @throws HandlerException if handler error occurs
     */
    
    public void errorOccurred(FileResultError error) throws HandlerException 
    {
        Throwable t = error.getThrowable();
        Result    r = error.getResult();
        int       errno = Constants.EXCEPTION;
        
        //-------------------------
        
        //get errno from Result or from SessionException
        
        if (r != null)
        {
            errno = r.getErrno();
        }
        else if (t != null && t instanceof SessionException)
        {
            SessionException sesEx = (SessionException) t;
            errno = sesEx.getErrno();            
        }
        
        //-------------------------
        
        //all we care about are IO_ERRORs.  However, there are various causes,
        //(i.e. disk space, network timeout, etc), so check that we are out of 
        //space before invoking cleanup procedure.
        
        if (errno == Constants.IO_ERROR)
        {
            //handle potential IO error
            if (shouldMakeSpace(0))
                makeSpace();           
        }        
    }


    //---------------------------------------------------------------------
    
    /**
     * Returns true if the amount of disk space used by files
     * exceeds the limit.  The parameter indicates the size of
     * a file that will be added, so as to determine if the
     * resulting size would trigger a cleanup
     * @param deltaValue Value to be added to total 
     * @return True if limit has been reached or exceeded, false otherwise
     */
    
    public boolean shouldMakeSpace(long deltaValue) throws HandlerException
    {
        long totalSpaceThusFar = 0;
        try {
            totalSpaceThusFar = this.database.getTotalFilesize(null, null);
        } catch (SQLException sqlEx) {
            throw new HandlerException("Error occurred while getting total "+
                                       "file size: "+sqlEx.getMessage(),sqlEx);
        }
        
        long totalSpaceAfterUpdate = totalSpaceThusFar + deltaValue;
        long maxSpaceLimit = this.systemInfo.getMaximumLimit(); 
        
        return (totalSpaceAfterUpdate > maxSpaceLimit);        
    }
    
    
    //---------------------------------------------------------------------
    
    public void close() 
    {
        _logger.trace("Invoking close operation.");
        
        try {
            if (this.database != null)
            {
                _logger.debug("Closing internal database.");
                database.close();
            }
        } catch (Throwable t) {
            _logger.error("Error occured while attempting to close the " +
            		      "database: "+t.getMessage());
            throw new RuntimeException("Error occurred while closing" +
                    "database in shutdown: "+t.getMessage(), t);
        }
    }

    //---------------------------------------------------------------------
    
    /**
     * This method calculates the amount of space that should be freed, where
     * oldest products are discarded.  
     */
    
    protected void makeSpaceOld() throws HandlerException
    {
        this._logger.debug("Invoking clean-up routine.");
        //spaceToFree = soft_max * ration_to_delete;
        //long spaceToFree = (long) (this.systemUtility.getSoftLimit() * 
        //                                        this.makespaceRatio);
        
        long usedSpace = 0;
        int filesAffected = 0;
        
        try {
            usedSpace = this.database.getTotalFilesize(null, null);
        } catch (SQLException sqlEx) {
            throw new HandlerException("Exception occurred while getting total "+
                                       " size: "+sqlEx.getMessage(), sqlEx);
        }
                
        long spaceToFree = (long) (usedSpace * this.makespaceRatio);

        this._logger.trace("Clearing "+spaceToFree+" out of "+usedSpace+
                           " bytes of older files from database and system...");

        while (spaceToFree > 0)
        {
            List<FileRecord> records;
            
            FileRecord filetypeRecord = new FileRecord(
                                            this._context.getServerGroup(), 
                                            this._context.getType());
            try {                
                records = this.database.getOldestRecords(filetypeRecord, 
                                                         this.makespaceBatchSize);

            } catch (SQLException sqlEx) {
                throw new HandlerException("Exception occurred while querying "+
                                 " oldest entries: "+sqlEx.getMessage(), sqlEx);
            }
            
            for (FileRecord record: records)
            {                
                String dirpath  = record.getLocation();
                String filename = record.getFilename();
                long   filesize = record.getFilesize();
                long   cliModTm = record.getClientModified();

                boolean shouldDelete  = true;
                boolean shouldRemove  = true;
                boolean fileDeleted   = false;
                boolean recordRemoved = false;

               
                File tmpFile = new File(dirpath + File.separator + filename);
                this._logger.trace("Removing "+tmpFile.getAbsolutePath()+"; size = "+filesize);
                shouldDelete = tmpFile.exists() && tmpFile.isFile();
                
                if (shouldDelete)
                {
                    try {      
                        //determine if file is newer than database thinks 
                        if (this.preserveNewerModFiles && cliModTm != -1L)
                        {
                            long lastMod = tmpFile.lastModified();
                            if (lastMod > 0 && cliModTm < lastMod)
                            {
                                this._logger.trace("Not deleting "+tmpFile.getAbsolutePath()+
                                                 ". It has been modified locally and " +
                                                 "preserve new files is enabled.");
                                shouldDelete = false;
                            }
                        }
                        
                        if (shouldDelete)
                        {
                            fileDeleted = this.systemUtility.deleteFile(tmpFile, 
                                                             this.pruneEmptyDirectories);
                            if (fileDeleted)
                                this._logger.trace(tmpFile.getAbsolutePath()+" deleted.");
                            else
                                this._logger.warn("Could not delete "+tmpFile.getAbsolutePath());
                        }
                    } catch (Exception ex) {
                        throw new HandlerException("Exception caught while deleting " +
                        		                    "file: "+ex.getMessage(), ex);
                    }
                }
                else
                {
                    this._logger.warn("Missing file "+tmpFile.getAbsolutePath() +
                                      ". Will attempt to remove record");
                }
                
                
                if (!fileDeleted && tmpFile.exists())
                {
                    shouldRemove = false;
                }
                
                if (shouldRemove)
                {
                    try {
                        recordRemoved = this.database.removeRecord(record);
                        
                        if (recordRemoved)
                            this._logger.trace("Record for "+tmpFile.getAbsolutePath()+" removed.");
                        else
                            this._logger.trace("Unable to remove record for "+tmpFile.getAbsolutePath());
                    } catch (SQLException sqlEx) {
                        throw new HandlerException("Exception occurred while removing "+
                                                   " entry: "+sqlEx.getMessage(), sqlEx);
                    }
                }
                
                if (recordRemoved)
                {
                    ++filesAffected;
                }
                
                if (recordRemoved && fileDeleted)
                    spaceToFree -= filesize;
                
                //  time to break out of for loop?
                if (spaceToFree <= 0)
                    break;
                
            }               
        }
        
        this._logger.debug("Clean-up routine removed "+filesAffected+" files.");
        
        //while (spaceToFree > 0)
                
            //get a list of the first N entries from database sorted by mod time
            
            //for each file entry
              //delete the file on disk
              //delete the file entry on the database
              //spaceToFree -= file.size
            
              //if (pruneEmptyDirs)
              //  dir = file.parent();
              //  while (dir.isEmpty)
              //      deleteDir
              //      dir = dir.parent()                   
        
    }

    protected void makeSpace() throws HandlerException
    {
        this._logger.info("Invoking handler clean-up routine.");
        //spaceToFree = soft_max * ration_to_delete;
        //long spaceToFree = (long) (this.systemUtility.getSoftLimit() * 
        //                                        this.makespaceRatio);
        
        long usedSpace = 0;
        int numRecordsAffected = 0;
        
        try {
            usedSpace = this.database.getTotalFilesize(null, null);
        } catch (SQLException sqlEx) {
            throw new HandlerException("Exception occurred while getting total "+
                                       " size: "+sqlEx.getMessage(), sqlEx);
        }
                
        long spaceToFree = (long) (usedSpace * this.makespaceRatio);

        if (this.logDeleteMessages)
            this._logger.info(HANDLER_TITLE+": Clearing "+spaceToFree+" out of "+usedSpace+                    
                              " bytes of older files from database and system...");
        else
            this._logger.trace("Clearing "+spaceToFree+" out of "+usedSpace+
                               " bytes of older files from database and system...");

        //-------------------------
        
        while (spaceToFree > 0)
        {
            List<FileRecord> records;            
            FileRecord filetypeRecord = new FileRecord(
                                            this._context.getServerGroup(), 
                                            this._context.getType());
            try {                
                records = this.database.getOldestRecords(filetypeRecord, 
                                                         this.makespaceBatchSize);

            } catch (SQLException sqlEx) {
                throw new HandlerException("Exception occurred while querying "+
                                 " oldest entries: "+sqlEx.getMessage(), sqlEx);
            }
            
            //if empty, then the database is reporting size for rows that dont exists            
            if (records.isEmpty())
            {
                throw new HandlerException("No records exist in database.  " +
                		        "Cannot continue with makespace operation");
            }
            
            for (FileRecord record: records)
            {         
                String dirpath  = record.getLocation();
                String filename = record.getFilename();
                long   filesize = record.getFilesize();
                
                boolean shouldRemoveRecord  = true;
                boolean fileDeleted         = false;
                boolean recordRemoved       = false;
                
                File tmpFile = new File(dirpath + File.separator + filename);                
                this._logger.trace("Evaluating file and record for "+tmpFile.getAbsolutePath()+"; size = "+filesize);
                
                //-----------------
                
                String message = null;
                
                //int code = attemptDeleteFileOnDisk(record);
                int code = this.systemUtility.attemptDeleteFileOnDisk(record, 
                                                  this.preserveNewerModFiles,
                                                  this.pruneEmptyDirectories);
                switch (code)
                {
                    case SystemUtility.CODE_FILE_DELETED:          
                        message = "Deleted "+tmpFile.getAbsolutePath();
                        this._logger.trace(message);                    
                        fileDeleted = true;
                        break;
                
                    case SystemUtility.CODE_FILE_MISSING:           
                        this._logger.warn("Missing file "+tmpFile.getAbsolutePath() +
                                          ". Will attempt to remove record.");
                        break;
                
                    case SystemUtility.CODE_PRESERVE_FILE:
                        message = "Skipping deletion of "+tmpFile.getAbsolutePath()+
                                                ". Will attempt to remove record.";
                        this._logger.trace(message);
                        break;
                        
                    case SystemUtility.CODE_DELETE_ERROR:
                        this._logger.warn("Error occurred while attempting to delete "+
                                            tmpFile.getAbsolutePath()+
                                    " Will not remove record.");                    
                        shouldRemoveRecord = false;
                        break;
                        
                    default:
                        this._logger.warn("Unknown return code.  Skipping record.");                    
                        shouldRemoveRecord = false;
                }

                //print info message (otherwise it prolly went to TRACE)
                if (this.logDeleteMessages && message != null)
                    this._logger.info(HANDLER_TITLE+": "+message);
                    
                //-----------------
                
                
                if (shouldRemoveRecord)
                {
                    try {
                        recordRemoved = this.database.removeRecord(record);
                    } catch (SQLException sqlEx) {
                        throw new HandlerException("Exception occurred while removing "+
                                                   " entry: "+sqlEx.getMessage(), sqlEx);
                    }
                }
                
                if (recordRemoved)
                {
                    this._logger.trace("Removed record for "+tmpFile.getAbsolutePath());
                    ++numRecordsAffected;
                }
                else
                {
                    this._logger.trace("Unable to remove record for "+tmpFile.getAbsolutePath());
                }
                
                //-----------------
                
                if (fileDeleted)
                    spaceToFree -= filesize;
                
                //  time to break out of for loop?
                if (spaceToFree <= 0)
                    break;
                
                //-----------------
            }               
        }
        
        this._logger.info(HANDLER_TITLE+": Clean-up routine removed "+numRecordsAffected+" records.");
        
        //while (spaceToFree > 0)
                
            //get a list of the first N entries from database sorted by mod time
            
            //for each file entry
              //delete the file on disk
              //delete the file entry on the database
              //spaceToFree -= file.size
            
              //if (pruneEmptyDirs)
              //  dir = file.parent();
              //  while (dir.isEmpty)
              //      deleteDir
              //      dir = dir.parent()                   
        
    }
    
//    protected int attemptDeleteFileOnDisk(FileRecord record)
//    {
//        int returnCode = SystemUtility.CODE_PRESERVE_FILE;
//        
//        String dirpath  = record.getLocation();
//        String filename = record.getFilename();
//        long   filesize = record.getFilesize();
//        long   cliModTm = record.getClientModified();
//
//        boolean shouldDelete  = true;
//        boolean fileDeleted   = false;
//
//        File tmpFile = new File(dirpath + File.separator + filename);
//
//        //check if file exists
//        if (!tmpFile.isFile())
//        {
//            shouldDelete = false;
//            returnCode = SystemUtility.CODE_FILE_MISSING;
//        }
//        else
//        {                
//            //determine if file is newer than database thinks 
//            if (this.preserveNewerModFiles && cliModTm != -1L)
//            {
//                long lastMod = tmpFile.lastModified();
//                if (lastMod > 0 && cliModTm < lastMod)
//                {
//                    this._logger.trace(tmpFile.getAbsolutePath()+
//                            " has been modified locally and " +
//                    "preserve new files is enabled.");
//                    shouldDelete = false;
//                    returnCode = SystemUtility.CODE_PRESERVE_FILE;
//                }
//            }
//        }    
//                
//                
//        //check that we should proceed with deletion
//        if (shouldDelete)
//        {
//            try { 
//                fileDeleted = this.systemUtility.deleteFile(tmpFile, 
//                                                     this.pruneEmptyDirectories);
//                if (fileDeleted)
//                {
//                    returnCode = SystemUtility.CODE_FILE_DELETED;
//                }
//                else
//                {
//                    returnCode = SystemUtility.CODE_DELETE_ERROR;
//                }                
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                returnCode = SystemUtility. CODE_DELETE_ERROR;
////                
////                throw new HandlerException("Exception caught while deleting " +
////                                            "file: "+ex.getMessage(), ex);
//            }
//        }
//        
//        return returnCode;
//        
//    }
//    
    //---------------------------------------------------------------------
    
    /**
     * Database name will be created via a concatenation of prefix,
     * servergroup name, and suffix.
     */
    
    protected String getDatabaseName()
    {
        String servergroup = this._context.getServerGroup();
        
        String dbName = DATABASE_NAME_PREFIX + servergroup + DATABASE_NAME_SUFFIX;
        
        return dbName;
    }

    //---------------------------------------------------------------------
    
    /**
     * Database is stored in hidden directory (.komodo) under output directory.
     */
    
    protected String getDatabaseLocation()
    {
        String directory = this._context.getDirectory();
        
        String dbLocation = directory + File.separator + Constants.RESTARTDIR;
        
        return dbLocation;
    }
    
    //---------------------------------------------------------------------
    //---------------------------------------------------------------------

}
