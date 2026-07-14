package jpl.mipl.mdms.FileService.komodo.client.handlers.examples.slidingwindow;

/**
 * 
 * <B>Purpose:<B>
 * Configuration property names and some default values.
 *
 * There are various settings for the handler that can be set
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
 *
 *
 * ++ Property: log.cleanup.messages
 * -- Description: Indicates the location of database properties file.
 * This properties file will be parsed and used by the database.
 * -- Type: Filepath (string)
 * -- Required: Dependent upon database implementation
 * public static final String PROPERTY_LOG_CLEAN_MESSASGES =
        "log.cleanup.messages";
 * </PRE>
 * 
 * @author Nicholas Toole (Nicholas.T.Toole@jpl.nasa.gov)
 * @version $Id: Configuration.java,v 1.1 2011/05/25 22:18:46 ntt Exp $
 *
 */
public interface Configuration
{
    public static final String PROPERTY_DATABASE_MAX_USAGE = 
        "disk.soft.limit";

    public static final String PROPERTY_DATABASE_TOTAL_SPACE = 
        "disk.total.space";

    public static final String PROPERTY_DATABASE_SPACE_RATIO = 
        "disk.cleanup.ratio";

    public static final String PROPERTY_CLEANUP_PRUNE_DIRS = 
        "prune.empty.directories";

    public static final String PROPERTY_PRESERVE_LOCAL_MODIFIED = 
       "preserve.newer.files";

    public static final String PROPERTY_DATABASE_BATCH_SIZE = 
        "query.batch.size";

    public static final String PROPERTY_DATABASE_IMPLEMENTATION = 
        "slidingwindow.database.implementation";

    public static final String PROPERTY_DATABASE_PROPERTIES_LOCATION = 
        "database.properties.location";
    
    public static final String PROPERTY_LOG_CLEAN_MESSASGES =
        "log.cleanup.messages";
    
    //---------------------------------------------------------------------
    
    //default property files
    
    public static final String DEFAULT_DATABASE_SPACE_RATIO      = ".25";
    public static final String DEFAULT_CLEANUP_PRUNE_DIRS        = "false";
    public static final String DEFAULT_PRESERVE_LOCAL_MODIFIED   = "false";    
    public static final String DEFAULT_DATABASE_BATCH_SIZE       = "50";
    public static final String DEFAULT_LOG_CLEAN_MESSAGES        = "true";
    public static final String DEFAULT_DATABASE_IMPLEMENTATION   = "jpl.mipl.mdms.FileService.komodo.client.handlers.examples.slidingwindow.SlidingWindowDerbyFileDatabase";
    
    //---------------------------------------------------------------------
}
