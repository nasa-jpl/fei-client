package jpl.mipl.mdms.FileService.komodo.client.handlers.examples.slidingwindow;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import jpl.mipl.mdms.FileService.io.BoundedBufferedReader;

/**
 * <B>Purpose:<B>
 * Utility tool that offers some simple services in maintaining
 * the embedded database.
 * 
 * Current supported operations: show, reset, sync.
 *
 * @author Nicholas Toole (Nicholas.T.Toole@jpl.nasa.gov)
 * @version $Id: SlidingWindowFileDatabaseTool.java,v 1.2 2013/10/14 17:24:27 ntt Exp $
 *
 */
public class SlidingWindowFileDatabaseTool
{
    
    public static final int GET_FILE_BATCH_SIZE = 50;
    
    protected String dbLocation, dbName;
    protected Properties dbProperties;
    protected String operation;
    
    SlidingWindowFileDatabase database;
    
    public static final String COMMAND_SHOW      = "show";
    public static final String COMMAND_DIFF      = "diff";
    public static final String COMMAND_SYNC      = "sync";
    public static final String COMMAND_RESET     = "reset";
    public static final String COMMAND_MAKESPACE = "makespace";
    
    public static final String OPTION_HELP   = "help";
    
    //---------------------------------------------------------------------
    
    public SlidingWindowFileDatabaseTool(String databaseName, String databaseLocation) throws Exception
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
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
            System.out.println("Error state = "+sqlEx.getSQLState());
            throw sqlEx;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
        
    }
    
    //---------------------------------------------------------------------
    
    protected SlidingWindowFileDatabase instantiateDatabase() throws Exception
    {
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
    
    public void close() throws Exception
    {
        try { 
            if (this.database != null)
            {
                this.database.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }
    
    //---------------------------------------------------------------------
    
    public void performCommand(String operation, String[] options) throws Exception
    {
        if (shouldPrintUsage(operation, options))
        {
            printUsage(operation);
            return;
        }
        
        if (operation == null || operation.equals(""))
        {
            System.out.println("No operation to perform");
        }
        else if (operation.equalsIgnoreCase(COMMAND_SHOW))
        {
            this.show(options);
        }
        
        else if (operation.equals(COMMAND_RESET))
        {
            this.reset(options);
            
        }
        else if (operation.equals(COMMAND_SYNC))
        {
            this.sync(options);            
        }
        else if (operation.equals(COMMAND_DIFF))
        {
            this.diff(options);            
        }
        else if (operation.equals(COMMAND_MAKESPACE))
        {
            this.makespace(options);            
        }
        
    }

    
    //---------------------------------------------------------------------
    
    protected boolean shouldPrintUsage(String operation, String[] options)
    {
        if (operation == null)
            return true;        
        else if (operation.equalsIgnoreCase(OPTION_HELP))
        {
            return true;
        }
        else if (options != null)
        {
            for (String option : options)
            {
                if (option.equalsIgnoreCase(OPTION_HELP))
                    return true;
            }
        }
        
        return false;
    }
    
    //---------------------------------------------------------------------

    public static void printUsage(String operation)
    {
        boolean printAll = false;
        String clazzname = SlidingWindowFileDatabaseTool.class.getName();
        if (operation == null || operation.equals("") || operation.equals(OPTION_HELP))
            printAll = true;
        
        StringBuffer buffer = new StringBuffer("USAGE: ");
        
        if (printAll)
        {           
            buffer.append("java ").append(clazzname).append(" dbName dbLocation command [options]\n");
            buffer.append("       command: show, diff, sync, reset, makespace\n");
            buffer.append("       options: command options");            
        }
        else
        {
            if (operation.equalsIgnoreCase(COMMAND_DIFF))
            {                
                buffer.append("java ").append(clazzname).append(" dbName dbLocation diff [servergroup [filetype]]\n");
            }
            else if (operation.equalsIgnoreCase(COMMAND_SHOW))
            {
                buffer.append("java ").append(clazzname).append(" dbName dbLocation show [servergroup [filetype]]\n");                
            }
            else if (operation.equalsIgnoreCase(COMMAND_RESET))
            {
                buffer.append("java ").append(clazzname).append(" dbName dbLocation reset\n");
            }
            else if (operation.equalsIgnoreCase(COMMAND_SYNC))
            {
                buffer.append("java ").append(clazzname).append(" dbName dbLocation sync [servergroup [filetype]]\n");
            }
            else if (operation.equalsIgnoreCase(COMMAND_MAKESPACE))
            {
                buffer.append("java ").append(clazzname).append(" dbName dbLocation makespace limit [servergroup [filetype]]\n");
                buffer.append("       limit: the maximum number of megabytes the database should track\n");                
            }
        }
         
        System.out.println(buffer.toString());
    }
    
    //---------------------------------------------------------------------
    
    
    protected boolean reset(String[] options) throws Exception
    {
        boolean success = false;
        String mesg = "Warning: reset command will remove ALL file entries in database";
        VerifyAction verifier = new VerifyAction(mesg);
        if (!verifier.isVerified())
        {
            System.out.println("reset operation aborted");
            return false;
        }
        
        System.out.println("Running reset...");
        
        success = this.database.clearRecords();
        if (success)
            System.out.println("File records removed");
        else
            System.out.println("Database remove all command was not successful");     
        
        return success;
    }
    
    //---------------------------------------------------------------------
    
    protected boolean show(String[] options) throws Exception
    {
        
        String sg = options.length > 0 ? options[0] : null;
        String ft = options.length > 1 ? options[1] : null;
        
        FileRecord keyRecord = new FileRecord(sg, ft);
        ShowFilesHandler handler = new ShowFilesHandler();
        
        System.out.println("Running show...");
        
        this.database.doForAllRecords(keyRecord, handler);
        
        if (handler.hasError())
        {
            System.err.println("Error occured while performing show.");
            System.err.println("Message: "+handler.getError());    
            return false;
        }
        
        long count = handler.getCount();
        if (count == 0)
        {
            System.out.println("No files found.");
        }
        else
        {
            System.out.println("Total count: "+count);
        }
        
        return true;
    }
    
    //---------------------------------------------------------------------
    
    /**
     * Iterates over all entries in database and checks if the associated
     * file is still available on disk.  If not, then its entry in the database
     * is removed.
     * @param options Not used.
     * @throws Exception If error occurs.
     */
    protected boolean sync(String[] options) throws Exception
    {        
        String mesg = "Warning: sync command will remove file entries if associated file cannot be found on disk";
        VerifyAction verifier = new VerifyAction(mesg);
        if (!verifier.isVerified())
        {
            System.out.println("sync operation aborted");
            return false;
        }
        
        System.out.println("Running sync...");
     
        String sg = options.length > 0 ? options[0] : null;
        String ft = options.length > 1 ? options[1] : null;
        
        FileRecord keyRecord = new FileRecord(sg, ft);
        SyncFileHandler handler = new SyncFileHandler();
        
        this.database.doForAllRecords(keyRecord, handler);
        
        if (handler.hasError())
        {
            System.err.println("Error occured while performing sync.");
            System.err.println("Message: "+handler.getError());
            return false;
        }
        
        List<FileRecord> results = handler.getRecords();
        if (results.isEmpty())
        {
            System.out.println("Congrats. No missing files.");
        }
        else
        {
            for (FileRecord record : results)
            {
                String path = record.getLocation() + File.separator + record.getFilename();
                System.out.println("Removing record for missing file "+path);
                this.database.removeRecord(record);
            }
            System.out.println("Done.");
        }
        
        return true;
    }
    
    //---------------------------------------------------------------------
    
    /**
     * Iterates over all entries in database and checks if the associated
     * file is still available on disk.  If not, then message is printed.
     * @param options server group, filetype
     * @throws Exception If error occurs.
     */
    protected boolean diff(String[] options) throws Exception
    {
        
        String sg = options.length > 0 ? options[0] : null;
        String ft = options.length > 1 ? options[1] : null;
        
        FileRecord keyRecord = new FileRecord(sg, ft);
        SyncFileHandler handler = new SyncFileHandler();
        
        System.out.println("Running diff...");
        
        this.database.doForAllRecords(keyRecord, handler);
        
        if (handler.hasError())
        {
            System.err.println("Error occured while performing diff.");
            System.err.println("Message: "+handler.getError());
            return false;
        }
        
        List<FileRecord> results = handler.getRecords();
        if (results.isEmpty())
        {
            System.out.println("Congrats. No missing files.");
        }
        else
        {
            for (FileRecord record : results)
            {
                String path = record.getLocation() + File.separator + record.getFilename();
                String fullFt = record.getServerGroup() + ":" + record.getFiletype();            
                System.out.println("["+fullFt+"] Missing file "+path);
            }
            System.out.println("Done.");
        }
        
        return true;
    }
    
    //---------------------------------------------------------------------
    
    protected boolean makespace(String[] options) throws Exception
    {

        String mesg = "makespace command may remove file entries and delete\nassociated files from disk";
        VerifyAction verifier = new VerifyAction(mesg);
        if (!verifier.isVerified())
        {
            System.out.println("makespace operation aborted");
            return false;
        }
        
        //parse max space
        long maxSpaceMB = -1;        
        if (options == null || options.length < 1)
        {
            throw new IllegalArgumentException("Options parameter does not contain a max size");
        }        
        try {
            maxSpaceMB = Long.parseLong(options[0]);
        } catch (NumberFormatException nfEx) {
            throw new IllegalArgumentException("Could not parse max size option "+options[0]);
        }
         
        if (maxSpaceMB < 1)
        {
            throw new IllegalArgumentException("maximum space must be greater than 1");
        }
        
        //attempt to get server group and filetype args
        String sg = options.length > 1 ? options[1] : null;
        String ft = options.length > 2 ? options[2] : null;
        
        //-------------------------
        
        System.out.println("Running makespace...");

        
        SystemUtility systemUtil = new SystemUtility();        
        long dblSpace = (maxSpaceMB < (Long.MAX_VALUE / 2)) ?
                         maxSpaceMB * 2 : Long.MAX_VALUE;     
        SystemInfo systemInfo    = new SystemInfo(maxSpaceMB, dblSpace);
        long maxSpaceBytes = systemInfo.getMaximumLimit();
        
        //-------------------------
        
        long usedSpace = 0;        
        int numRecordsAffected = 0;
        
        try {
            usedSpace = this.database.getTotalFilesize(sg, ft);
        } catch (SQLException sqlEx) {
            throw new Exception("Exception occurred while getting total "+
                                       " size: "+sqlEx.getMessage(), sqlEx);
        }
        
        long spaceToFree = usedSpace - maxSpaceBytes;

        System.out.println("Total number of bytes tracked by database: "+usedSpace);
        System.out.println("Mimimum number of bytes to free: "+spaceToFree);
        
        
        verifier = new VerifyAction(null);
        if (!verifier.isVerified())
        {
            System.out.println("makespace operation aborted");
            return false;
        }
        
        //-------------------------
        
        while (spaceToFree > 0)
        {
            List<FileRecord> records;                
            FileRecord filetypeRecord = new FileRecord(sg, ft);
            try {                
                records = this.database.getOldestRecords(filetypeRecord, 
                                                         GET_FILE_BATCH_SIZE);                    
            } catch (SQLException sqlEx) {
                throw new Exception("Exception occurred while querying "+
                                 " oldest entries: "+sqlEx.getMessage(), sqlEx);
            }
            
       
            if (records.isEmpty())
            {
                System.err.println("Error: No records found despite there still being space to be freed.");  
                System.out.println("makespace operation exiting");
                return false;
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
                
                //int code = attemptDeleteFileOnDisk(record);
                int code = systemUtil.attemptDeleteFileOnDisk(record);
                switch (code)
                {
                    case SystemUtility.CODE_FILE_DELETED:    
                        System.out.println("Deleted "+tmpFile.getAbsolutePath()+"");                   
                        fileDeleted = true;
                        break;
                
                    case SystemUtility.CODE_FILE_MISSING:                        
                        System.out.println("Missing file "+tmpFile.getAbsolutePath() +
                                ". Will attempt to remove record.");
                        break;
                
                    case SystemUtility.CODE_PRESERVE_FILE:
                        System.out.println("Skipping deletion of "+tmpFile.getAbsolutePath()+
                                           ". Will attempt to remove record.");
                        break;
                        
                    case SystemUtility.CODE_DELETE_ERROR:                 
                        System.err.println("Error occurred while attempting to delete "+tmpFile.getAbsolutePath()+
                                      " Will not remove record.");                    
                        shouldRemoveRecord = false;
                        break;
                        
                    default:
                        System.err.println("Unknown return code.  Skipping record.");                    
                        shouldRemoveRecord = false;
                }

                //-----------------
                
                if (shouldRemoveRecord)
                {
                    try {
                        recordRemoved = this.database.removeRecord(record);
                    } catch (SQLException sqlEx) {
                        throw new Exception("Exception occurred while removing "+
                                                   " entry: "+sqlEx.getMessage(), sqlEx);
                    }
                }
                
                if (recordRemoved)
                {
                    System.out.println("Removed record for "+tmpFile.getAbsolutePath());
                    ++numRecordsAffected;
                }
                else
                {
                    System.err.println("Unable to remove record for "+tmpFile.getAbsolutePath());
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
        
        if (numRecordsAffected > 0)
        {
            System.out.println("Total count of files removed: "+numRecordsAffected);
        } 
        else
        {
            System.out.println("No files/records were affected by makespace");
        }
        
        return true;
    }
    
    //---------------------------------------------------------------------
    
    //=====================================================================
    //=====================================================================
    
    class VerifyAction
    {
        String message = null;
        
        String[] LEGAL_YES = new String[]{"yes", "y"};
        String[] LEGAL_NO  = new String[]{"no",  "n"};
        
        
        public VerifyAction(String message)
        {
            this.message = message;
        }
        
        public boolean isVerified()
        {
            boolean cont  = false;
            boolean legalInput = false;
         
            // print out message if included
            if (message != null)
                System.out.println(message);
            
            System.out.println("Proceed? [Y/N]: ");

            //  open up standard input
            //Security code review prompted this change to prevent DOS attack (nttoole 08.27.2013)
            //BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader br = new BoundedBufferedReader(new InputStreamReader(System.in));
            
            String input = null;

            while (!legalInput)
            {
                //  read the username from the command-line; need to use try/catch with the
                //  readLine() method
                try {
                    input = br.readLine();
                } catch (IOException ioEx) {
                    ioEx.printStackTrace();
                    return false;                
                }
                
                if (input == null || input.equals(""))
                    continue;
                
                for (String entry : LEGAL_YES)
                {
                    if (entry.equalsIgnoreCase(input))
                    {
                        legalInput = true;
                        cont = true;
                    }
                }
                for (String entry : LEGAL_NO)
                {
                    if (entry.equalsIgnoreCase(input))
                    {
                        legalInput = true;
                        cont = false;
                    }
                }
            }
            
            return cont;
        }
        
    }
    
    
    //=====================================================================
    
    class SyncFileHandler implements QueryResultHandler
    {
        List<FileRecord> recordsToDelete;
        String errMessage;
        
        public SyncFileHandler()
        {
            this.recordsToDelete = new ArrayList<FileRecord>();
            this.errMessage = null;
        }
        
        public boolean hasError()
        {
            return this.errMessage != null;
        }
        
        public Object getError()
        {
            return this.errMessage;
        }
        
        public void handleResultSet(ResultSet resultSet) 
        {
            String type, srvg, loca, name;
            long modt, size, clmd;
            
            try {
                name = resultSet.getString("FILENAME");
                loca = resultSet.getString("LOCATION");
                srvg = resultSet.getString("SERVERGROUP");
                type = resultSet.getString("FILETYPE");
                modt = resultSet.getLong(  "MODIFIED");
                size = resultSet.getLong(  "FILESIZE");
                clmd = resultSet.getLong(  "CLIENTMODIFIED");
                
                if (name != null && loca != null && srvg != null && type != null)
                {                   
                    File file = new File(loca + File.separator + name);
                    if (!file.exists() || file.isDirectory())
                    {
                        FileRecord  record = new FileRecord(name, srvg, type, 
                                                 loca, size, modt, clmd);                
                        recordsToDelete.add(record);                        
                    }                                         
                }
                
            } catch (SQLException sqlEx) {
                sqlEx.printStackTrace();
                if (errMessage == null)
                {
                    errMessage = sqlEx.getMessage();
                }
            }
        }
        
        public List<FileRecord> getRecords()
        {
            return this.recordsToDelete;
        }        
    }
    
    //=====================================================================
    
    class ShowFilesHandler implements QueryResultHandler
    {
        String errMessage = null;
        OutputStream os;
        int count = 0;
        
        public ShowFilesHandler()
        {
            this(null);
        }
        
        public ShowFilesHandler(OutputStream os)
        {
            this.os = os == null ? System.out : os;
            this.errMessage = null;
            this.count = 0;
        }
        
        public boolean hasError()
        {
            
            return (this.errMessage != null);
        }
        
        public Object getError()
        {
            return this.errMessage;
        }
        
        public void handleResultSet(ResultSet resultSet) 
        {
            String type, srvg, loca, name;
            long modt, size, clmd;
            
            try {
                name = resultSet.getString("FILENAME");
                loca = resultSet.getString("LOCATION");
                srvg = resultSet.getString("SERVERGROUP");
                type = resultSet.getString("FILETYPE");
                modt = resultSet.getLong(  "MODIFIED");
                size = resultSet.getLong(  "FILESIZE");
                clmd = resultSet.getLong(  "CLIENTMODIFIED");
                
                if (name != null && loca != null && srvg != null && type != null)
                {       
                    ++count;
                    String line = "Result #"+count+": ["+srvg+":"+type+"] "+name+", "+loca+", "+modt+" (srvr), "+size+" bytes, "+clmd+" (clnt)\n";
                    
                    try {
                        this.os.write(line.getBytes());
                    } catch (IOException ioEx) {
                        ioEx.printStackTrace();
                        if (errMessage == null)
                        {
                            errMessage = ioEx.getMessage();
                        }
                    }
                }
                
            } catch (SQLException sqlEx) {
                sqlEx.printStackTrace();
                if (errMessage == null)
                {
                    errMessage = sqlEx.getMessage();
                }
            }
        }
        
        public long getCount()
        {
            return this.count;
        }
            
    }
    
    //=====================================================================
    //=====================================================================
    
    //---------------------------------------------------------------------
    //---------------------------------------------------------------------
    
    public static void main(String[] args)
    {
        if (args.length < 3)
        {
            SlidingWindowFileDatabaseTool.printUsage(null);
            System.exit(1);
        }
        
        String name     = args[0];
        String location = args[1];
        String command  = args[2];
        
        File tmp = new File(location);
        if (!tmp.exists())
        {
            System.err.println("Location "+tmp.getAbsolutePath()+" does not exist.");
            System.exit(1);
        }
        
        //get any command options from remainder args
        int optionSize = args.length - 3;
        String[] options = new String[optionSize];
        for (int i = 0; i < optionSize; ++i)
            options[i] = args[3+i];
        
        
        SlidingWindowFileDatabaseTool tool = null;
        try {            
             tool = new SlidingWindowFileDatabaseTool(name, location);
             
             tool.performCommand(command, options);
             
             tool.close();
             
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }    
    }    
    
    //---------------------------------------------------------------------
}
