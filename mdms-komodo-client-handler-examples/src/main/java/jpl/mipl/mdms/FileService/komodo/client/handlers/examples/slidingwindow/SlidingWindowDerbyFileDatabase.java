package jpl.mipl.mdms.FileService.komodo.client.handlers.examples.slidingwindow;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SlidingWindowDerbyFileDatabase implements SlidingWindowFileDatabase
{
    String driver = "org.apache.derby.jdbc.EmbeddedDriver";
    String connectionUrlPre  = "jdbc:derby:";
    String connectionUrlPost = ";create=true";
    
    /**
     * Synchronization lock
     */
    protected final Object _lock = new Object();
    
    /**
     * In-memory total of cache size
     */
    protected Long _cachedTotalSize = null;
    
//    String createFilesTableString = "CREATE TABLE KOMODO_FILES "+
//                  "(FILE_ID INT NOT NULL GENERATED ALWAYS AS IDENTITY "+
//                  "              CONSTRAINT FILE_PK PRIMARY KEY, " +
//                  " ENTRY_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
//                  " SPECIAL_FIELD TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
//                  " FILENAME VARCHAR(128)  NOT NULL, " +
//                  " LOCATION VARCHAR(256) NOT NULL, " +
//                  " SERVERGROUP VARCHAR(32)  NOT NULL, " +
//                  " FILETYPE VARCHAR(128)  NOT NULL, " +
//                  " MODIFIED BIGINT NOT NULL, " +
//                  " FILESIZE BIGINT NOT NULL"+
//                  " CLIENTMODIFIED BIGINT NOT NULL) ";
//    
//    String createPropsTableString = "CREATE TABLE KOMODO_PROPERTIES "+
//                  "(PROPERTY_ID INT NOT NULL GENERATED ALWAYS AS IDENTITY "+
//                  "             CONSTRAINT PROPERTY_PK PRIMARY KEY, " +
//                  " PROPERTY_NAME VARCHAR(128)  NOT NULL, " +
//                  " PROPERTY_VALUE BIGINT NOT NULL) ";

    //creates a new file entry
    final String insertFileString = "insert into KOMODO_FILES(FILENAME, " +
                 "LOCATION, SERVERGROUP, FILETYPE, MODIFIED, FILESIZE, CLIENTMODIFIED) values" +
                 " (?,?,?,?,?,?,?)";
    
    //removes a file entry    
    final String removeFileString = "delete from KOMODO_FILES WHERE "+
                 "FILENAME = ? AND LOCATION = ? AND SERVERGROUP = ? AND "+
                 "FILETYPE = ?";
    
    //selects a list of the oldest N entries, where N is a positive int
    final String selectNFileString = "select * from KOMODO_FILES WHERE "+
                 "SERVERGROUP like ? AND FILETYPE like ? ORDER BY MODIFIED";
    
    //checks if entry already exists, returns filesize
    final String checkFileString = "select FILESIZE from KOMODO_FILES WHERE "+
                "FILENAME = ? AND LOCATION = ? AND SERVERGROUP = ? " +
                "AND FILETYPE = ?";
    
    //updates existing entry with new data
    final String updateFileString = "update KOMODO_FILES SET "+
                "MODIFIED = ?, FILESIZE = ?, CLIENTMODIFIED = ? WHERE " +
                "FILENAME = ? AND LOCATION = ? AND SERVERGROUP = ? " +
                "AND FILETYPE = ?";

//    //upsert handles update if exists, else insert
//    final String upsertFileString = "if exists (" + checkFileString + ") " + 
//    		    "update KOMODO_FILES SET MODIFIED = ?, FILESIZE = ?, CLIENTMODIFIED = ? " +
//    		 "else" +
//    		     insertFileString;
    		 
    //count record entries
    final String countFileString = "select COUNT(*) from KOMODO_FILES WHERE "+
                "SERVERGROUP like ? AND FILETYPE like ?";
    
    //total filesize of record entries
    final String sumFilesizeString = "select SUM(FILESIZE) from KOMODO_FILES WHERE "+
                "SERVERGROUP like ? AND FILETYPE like ?";
    
    //total filesize of record entries regardless of filetype
    final String sumAllFilesizeString = "select SUM(FILESIZE) from KOMODO_FILES";

    //remove all records 
    final String removeAllString = "delete from KOMODO_FILES";
    
    PreparedStatement prepInsert, prepDelete, prepSelectN, 
                      prepCheck,  prepUpdate, prepCount,
                      prepTotalSize, prepDeleteAll, prepUpsert,
                      prepAllTotalSize;
                      
    protected String databaseLocation;
    protected String databaseName;
    protected Properties databaseProperties;
    
    protected String fullDatabaseLocation;    
    protected String connectionUrl;
    
    protected Connection conn;
    
    //---------------------------------------------------------------------   
    
    public void initialize(String name, String location, Properties props) 
                                                          throws Exception
    {
        this.databaseName       = name;
        this.databaseLocation   = location;
        this.databaseProperties = props;
        
        if (name == null)
            throw new IllegalArgumentException("Required argument 'name' " +
            		                           "cannot be null");
        
        //lookup database file
        getDatabaseProperties();
        
        //load database to memory
        loadDatabase();
    }
    
    
    //---------------------------------------------------------------------
    
    protected void getDatabaseProperties()
    {        
        if (databaseLocation != null)
        {
            this.fullDatabaseLocation = databaseLocation + File.separator + 
                                         databaseName;
        }
        else
        {
            this.fullDatabaseLocation = databaseName;
        }
        
        //no need to inspect properties field because nothing is required from it
    }
    
    //---------------------------------------------------------------------
    
    protected void loadDatabase() throws SQLException, ClassNotFoundException
    {
        //Load the Database Driver
        try {
            Class.forName(driver);            
        } catch (ClassNotFoundException cnfEx) {
            throw cnfEx;
        }
     
        //build the full connection URL
        this.connectionUrl = connectionUrlPre + fullDatabaseLocation + 
                             connectionUrlPost;        

        testDatabase();               
    }
    
    //---------------------------------------------------------------------
    
    protected Connection _getConnection() throws SQLException
    {
        try {
            
            if (this.conn == null || this.conn.isClosed())
            {            
                this.conn = DriverManager.getConnection(connectionUrl);
                
                this.prepInsert       = conn.prepareStatement(insertFileString);
                this.prepDelete       = conn.prepareStatement(removeFileString);
                this.prepSelectN      = conn.prepareStatement(selectNFileString);
                this.prepCheck        = conn.prepareStatement(checkFileString);
                this.prepUpdate       = conn.prepareStatement(updateFileString);
                this.prepCount        = conn.prepareStatement(countFileString);
                this.prepTotalSize    = conn.prepareStatement(sumFilesizeString);
                this.prepDeleteAll    = conn.prepareStatement(removeAllString);
//                this.prepUpsert    = conn.prepareStatement(upsertFileString);
                this.prepAllTotalSize = conn.prepareStatement(this.sumAllFilesizeString);
            }
            
        } catch (SQLException sqlEx) {
            //TODO HANDLE ME
//            ex.printStackTrace();
//            this.conn = null;
            throw sqlEx;
        }
        
        return this.conn;
    }
    
    //---------------------------------------------------------------------
    
    protected void _closeConnection()
    {
        try {
            
            if (this.conn != null && this.conn.isClosed())
            {            
                if (this.prepInsert != null)
                    this.prepInsert.close();
                if (this.prepDelete != null)
                    this.prepDelete.close();
                if (this.prepSelectN != null)
                    this.prepSelectN.close();
                if (this.prepCheck != null)
                    this.prepCheck.close();
                if (this.prepUpdate != null)
                    this.prepUpdate.close();
                if (this.prepCount != null)
                    this.prepCount.close();
                if (this.prepTotalSize != null)
                    this.prepTotalSize.close();
                if (this.prepDeleteAll != null)
                    this.prepDeleteAll.close();
//                if (this.prepUpsert != null)
//                    this.prepUpsert.close();
                if (this.prepAllTotalSize != null)
                    this.prepAllTotalSize.close();
                
                
                this.conn.close();
            }
            
        } catch (Exception ex) {
            //TODO HANDLE ME
            ex.printStackTrace();
            this.conn = null;
        }
        
    }
    
    //---------------------------------------------------------------------
    
    
    public boolean addRecordOld(FileRecord record) throws SQLException
    {
        boolean exists  = false;
        boolean success = false;
        //run a check to see if entry exists
        exists = _checkRecord(record);
        
        if (exists)
        {            
            //run update                    
            success = _updateRecord(record);
            if (success)
            {
                //clear tracked size since its invalid
                this.resetTrackedSize(); 
            }
        }
        else
        {
            //run insert
            success = _insertRecord(record);
            if (success)
            {                
                this.updateTrackedSize(record.filesize);           
            }
        }
        
        return success;
    }
    
    public boolean addRecord(FileRecord record) throws SQLException
    {
        boolean exists  = false;
        boolean success = false;
        
        try {
            success = _insertRecord(record);
            
            if (success)
            {                
                this.updateTrackedSize(record.filesize);           
            }
            
        } catch (SQLException sqlEx) {
            
            if (sqlEx.getSQLState().equals("23505"))
            {
                success = false;
                exists  = true;
            }           
        }
        
        //-------------------------
        
        if (exists)
        {
            long originalSize = _checkRecordSize(record);
            long sizeDiff = record.getFilesize() - originalSize;
            
            success = _updateRecord(record);
            
            this.updateTrackedSize(sizeDiff); 
        }
    
//        _upsertRecord(record);
//        //run a check to see if entry exists
//        exists = _checkRecord(record);
//        
//        if (exists)
//        {            
//            //run update                    
//            success = _updateRecord(record);
//            if (success)
//            {
//                //clear tracked size since its invalid
//                this.resetTrackedSize(); 
//            }
//        }
//        else
//        {
//            //run insert
//            success = _insertRecord(record);
//            if (success)
//            {                
//                this.updateTrackedSize(record.filesize);           
//            }
//        }
//        
        return success;
    }
    
    //---------------------------------------------------------------------
    
    public boolean removeRecord(FileRecord record) throws SQLException
    {        
        boolean success = false;
        
        success = _removeRecord(record);
        if (success)
        {
            updateTrackedSize(-1L * record.filesize);
        }
        
        return success;        
    }
    
    //---------------------------------------------------------------------
    
    /**
     * Returns the total filesize of records in database matching parameters. 
     * both servergroup and filetype are specified, returns size for number
     * of rows in filetype.  If only servergroup is returned, returns size
     * of rows in servergroup, across filetypes.  If neither parameter is
     * specified, then size for all rows.  If only filetype is specified
     * then 0 is returned.
     * @param servergroup Servergroup name, can be null
     * @param filetype Filetype name, can be null
     * @return Total filesize of records matching parameters
     */
    
    public long getTotalFilesize(String servergroup, String filetype) 
                                                   throws SQLException
    {
        long totalSize = 0L;
        if (servergroup == null && filetype == null)
        {
            synchronized(this._lock)
            {
                Long cachedSize = this.getTrackedSize();
                if (cachedSize != null)
                    totalSize = cachedSize;
                else
                {
                    totalSize = _getTotalFilesize(null,null);
                    this.resetTrackedSize();                    
                }
            }
        }
        else
        {
            totalSize = _getTotalFilesize(servergroup, filetype); 
        }
        return totalSize;
    }
    
    //---------------------------------------------------------------------
    
    protected void updateTrackedSizeOld(Long value)
    {
        synchronized(this._lock)
        {
            if (value == null)
                this._cachedTotalSize = null;
            else if (this._cachedTotalSize == null)                
                this._cachedTotalSize = value;
            else
                this._cachedTotalSize += value;
            
            if (this._cachedTotalSize != null && this._cachedTotalSize < 0)
                this._cachedTotalSize = null;
        }       
    }
    
    protected void updateTrackedSize(Long value) throws SQLException
    {
        synchronized(this._lock)
        {
            if (value != null && this._cachedTotalSize != null)
            {
                this._cachedTotalSize += value;
                if (this._cachedTotalSize < 0) 
                    resetTrackedSize();
            }
            else 
            {
                resetTrackedSize();               
            }            
        }       
    }
    
    protected void resetTrackedSize() throws SQLException
    {
        this._cachedTotalSize = _getTotalFilesize(null,null);
    }
    
    //---------------------------------------------------------------------
    
    protected Long getTrackedSize() throws SQLException
    {
        synchronized(this._lock)
        {
            return this._cachedTotalSize;
        }         
    }
    
    //---------------------------------------------------------------------
    
    /**
     * Returns the number of records in database matching parameters.  If
     * both servergroup and filetype are specified, returns count for number
     * of rows in filetype.  If only servergroup is returned, returns number
     * of rows in servergroup, across filetypes.  If neither parameter is
     * specified, then row count for all rows.  If only filetype is specified
     * then 0 is returned.
     * @param servergroup Servergroup name, can be null
     * @param filetype Filetype name, can be null
     * @return Count of rows matching parameters
     */
    
    public long getRecordCount(String servergroup, String filetype) throws SQLException
    {        
        return _countRecords(servergroup, filetype);        
    }
    
    //---------------------------------------------------------------------
    
    protected boolean _checkRecord(FileRecord record) throws SQLException
    {
        boolean exists = false;
        
        _checkConnection();
        
        //run a check to see if entry exists
        
        this.prepCheck.setString(1, record.getFilename());
        this.prepCheck.setString(2, record.getLocation());
        this.prepCheck.setString(3, record.getServerGroup());
        this.prepCheck.setString(4, record.getFiletype());
        
        //we only care about the first
        //this.prepCheck.setMaxRows(1);
        
        ResultSet rs = this.prepCheck.executeQuery();
        
        while (rs.next() && !exists)
        {
            exists = true;            
        }
        rs.close();
        this.prepCheck.clearWarnings();
        //this.prepCheck.setMaxRows(0);
        
        return exists;
    }

    
    protected long _checkRecordSize(FileRecord record) throws SQLException
    {
        Long filesize = 0L;
        
        _checkConnection();
        
        //run a check to see if entry exists
        
        this.prepCheck.setString(1, record.getFilename());
        this.prepCheck.setString(2, record.getLocation());
        this.prepCheck.setString(3, record.getServerGroup());
        this.prepCheck.setString(4, record.getFiletype());
        
        //we only care about the first
        //this.prepCheck.setMaxRows(1);
        
        ResultSet rs = this.prepCheck.executeQuery();
        
        while (rs.next())
        {
            filesize  = rs.getLong(1);            
        }
        rs.close();
        this.prepCheck.clearWarnings();
        //this.prepCheck.setMaxRows(0);
        
        return filesize;
    }
    
    //---------------------------------------------------------------------
    
    protected boolean _updateRecord(FileRecord record) throws SQLException
    {
        int updateCount = 0;
        
        _checkConnection();
        
        //run a check to see if entry exists

        this.prepUpdate.setLong(  1, record.getModified());
        this.prepUpdate.setLong(  2, record.getFilesize());
        this.prepUpdate.setLong(  3, record.getClientModified());
        this.prepUpdate.setString(4, record.getFilename());
        this.prepUpdate.setString(5, record.getLocation());
        this.prepUpdate.setString(6, record.getServerGroup());
        this.prepUpdate.setString(7, record.getFiletype());
        
        updateCount = this.prepUpdate.executeUpdate();
        this.prepUpdate.clearWarnings();
        
        return updateCount > 0;
    }

    //---------------------------------------------------------------------
    
    protected boolean _insertRecord(FileRecord record) throws SQLException
    {
        int updateCount = 0;
        
        _checkConnection();
        
        //run a check to see if entry exists? already done by caller

        this.prepInsert.setString(1, record.getFilename());
        this.prepInsert.setString(2, record.getLocation());
        this.prepInsert.setString(3, record.getServerGroup());
        this.prepInsert.setString(4, record.getFiletype());
        this.prepInsert.setLong(  5, record.getModified());
        this.prepInsert.setLong(  6, record.getFilesize());
        this.prepInsert.setLong(  7, record.getClientModified());
        
        updateCount = this.prepInsert.executeUpdate();
        this.prepInsert.clearWarnings();
            
        return updateCount > 0;
        
    }

    //---------------------------------------------------------------------
    
    protected boolean _removeRecord(FileRecord record) throws SQLException
    {
        int updateCount = 0;
        
        _checkConnection();
        
        this.prepDelete.setString(1, record.getFilename());
        this.prepDelete.setString(2, record.getLocation());
        this.prepDelete.setString(3, record.getServerGroup());
        this.prepDelete.setString(4, record.getFiletype());
        
        updateCount = this.prepDelete.executeUpdate();
        this.prepDelete.clearWarnings();
        
        return updateCount > 0;        
    }

    //---------------------------------------------------------------------
    
    protected boolean _removeAllRecords() throws SQLException
    {
        int updateCount = 0;
        
        _checkConnection();
        
        updateCount = this.prepDeleteAll.executeUpdate();
        this.prepDeleteAll.clearWarnings();
        
        return updateCount >= 0;        
    }
    
    //---------------------------------------------------------------------
    
    protected List<FileRecord> _listNFiles(FileRecord sgftRecord, int n) 
                                           throws SQLException
    {
        List<FileRecord> records = new ArrayList<FileRecord>();

        _checkConnection();
                
        if (n <= 0)
            return records;
        
        String servergroup = sgftRecord.getServerGroup();
        String filetype    = sgftRecord.getFiletype();
        
        if (servergroup == null)
        {           
            servergroup = "%";
            filetype    = "%"; 
        }
        else if (filetype == null)
        {
            filetype = "%";
        }
        
        //run a check to see if entry exists
        
        this.prepSelectN.setString(1, servergroup);
        this.prepSelectN.setString(2, filetype);
        this.prepSelectN.setMaxRows(n);
      
        
        ResultSet rs = this.prepSelectN.executeQuery();
        
        String type, srvg, loca, name;
        long modt, size, clmd;
        FileRecord record;
        
        while (rs.next())
        {
            name = rs.getString("FILENAME");
            loca = rs.getString("LOCATION");
            srvg = rs.getString("SERVERGROUP");
            type = rs.getString("FILETYPE");
            modt = rs.getLong(  "MODIFIED");
            size = rs.getLong(  "FILESIZE");
            clmd = rs.getLong(  "CLIENTMODIFIED");
            
            if (name != null && loca != null && srvg != null && type != null)
            {
                record = new FileRecord(name, srvg, type, loca, size, modt, clmd);
                records.add(record);                
            }
        }
        rs.close();
        this.prepSelectN.clearWarnings();
        
        return records;
    }
    
    //---------------------------------------------------------------------
    
    protected boolean _forAllMatchingFiles(FileRecord sgftRecord, 
                                        QueryResultHandler handler) 
                                        throws SQLException
    {
        _checkConnection();
                
        //run a check to see if entry exists
        String servergroup = sgftRecord.getServerGroup();
        String filetype    = sgftRecord.getFiletype();
       
        if (servergroup == null)
        {           
            servergroup = "%";
            filetype    = "%"; 
        }
        else if (filetype == null)
        {
            filetype = "%";
        }
        
        
        this.prepSelectN.setString(1, servergroup);
        this.prepSelectN.setString(2, filetype);
        this.prepSelectN.setMaxRows(0);              
      
        ResultSet rs = this.prepSelectN.executeQuery();

        while (rs.next())
        {
            handler.handleResultSet(rs);            
        }
        rs.close();
        this.prepSelectN.clearWarnings();    
        
        return true; //by default
    }
    
    //---------------------------------------------------------------------
    
    public long _countRecords(String servergroup, String filetype) throws SQLException
    {
        long count = 0L;
        
        _checkConnection();
        
        if (servergroup == null && filetype != null)
            return count;
        
        
        if (servergroup == null)
        {           
            servergroup = "%";
            filetype    = "%"; 
        }
        else if (filetype == null)
        {
            filetype = "%";
        }
        
        this.prepCount.setString(1, servergroup);
        this.prepCount.setString(2, filetype);
        
        ResultSet rs = this.prepCount.executeQuery();
        while (rs.next())
        {
            count = rs.getInt(1);
        }
        
        return count;
    }
    
    //---------------------------------------------------------------------
    
    /**
     * Returns the total filesize of records in database matching parameters. 
     * If both servergroup and filetype are specified, returns size for number
     * of rows in filetype.  If only servergroup is returned, returns size
     * of rows in servergroup, across filetypes.  If neither parameter is
     * specified, then size for all rows.  If only filetype is specified
     * then 0 is returned.
     * @param servergroup Servergroup name, can be null
     * @param filetype Filetype name, can be null
     * @return Total filesize of records matching parameters
     */
    
    protected long _getTotalFilesize(String servergroup, String filetype) 
                                                      throws SQLException
    {
        long size = 0L;
        
        _checkConnection();
        
        if (servergroup == null && filetype != null)
            return size;
        
        ResultSet rs = null;
        
        if (servergroup == null)
        {
            rs = this.prepAllTotalSize.executeQuery();
        }
        else
        {
//            if (servergroup == null)
//            {           
//                servergroup = "%";
//                filetype    = "%"; 
//            }
//            else if (filetype == null)
//            {
//                filetype = "%";
//            }
    
            if (filetype == null)
            {
                filetype = "%";
            }
            
            this.prepTotalSize.setString(1, servergroup);
            this.prepTotalSize.setString(2, filetype);
            
            rs = this.prepTotalSize.executeQuery();
        }
        
        //ResultSet rs = this.prepTotalSize.executeQuery();
        
        while (rs.next())
        {
            size = rs.getLong(1);
        }
        
        return size;
    }
    
    
    //---------------------------------------------------------------------
    
    protected long _getFilesize(String servergroup, String filetype, String name)
                                throws SQLException
    {
        long size = 0L;

        _checkConnection();

        if (servergroup == null && filetype != null)
            return size;

        ResultSet rs = null;
        
        if (servergroup == null)
        {
            this.prepAllTotalSize.setMaxRows(1);
            rs = this.prepAllTotalSize.executeQuery();
            this.prepAllTotalSize.setMaxRows(0);
        }
        else
        {
            // if (servergroup == null)
            // {
            // servergroup = "%";
            // filetype = "%";
            // }
            // else if (filetype == null)
            // {
            // filetype = "%";
            // }

            if (filetype == null)
            {
                filetype = "%";
            }

            this.prepTotalSize.setString(1, servergroup);
            this.prepTotalSize.setString(2, filetype);

            rs = this.prepTotalSize.executeQuery();
        }

        // ResultSet rs = this.prepTotalSize.executeQuery();

        while (rs.next())
        {
            size = rs.getLong(1);
        }

        return size;
    }
    
    //---------------------------------------------------------------------
    
    public long getValue(String name) throws SQLException
    {
        long returnValue = 0L;

        _checkConnection();
        
        Statement s = conn.createStatement();  
        ResultSet rs = s.executeQuery("select PROPERTY_VALUE from KOMODO_PROPERTIES where ");
        while (rs.next())
        {
            returnValue = rs.getLong(1);
        }
        s.clearWarnings();
        return returnValue;
    }
    
    //---------------------------------------------------------------------
    
    public void setValue(String name, long value)  throws SQLException
    {

        _checkConnection();
        
    }
    
    //---------------------------------------------------------------------
    
    public void updateValueByDelta(String name, long valueDelta)  throws SQLException
    {

        _checkConnection();
        
    }

    //---------------------------------------------------------------------
    
    public void printRecords(OutputStream os) throws SQLException, IOException
    {
        _checkConnection();
        
        //run a check to see if entry exists
        
        String servergroup = "%";
        String filetype    = "%";
        
        this.prepSelectN.setString(1, servergroup);
        this.prepSelectN.setString(2, filetype);
        this.prepSelectN.setMaxRows(0);
      
        
        ResultSet rs = this.prepSelectN.executeQuery();
        
        String type, srvg, loca, name;
        long modt, size, clmd;
        
        int count = 0;
        while (rs.next())
        {
            count++;
            name = rs.getString("FILENAME");
            loca = rs.getString("LOCATION");
            srvg = rs.getString("SERVERGROUP");
            type = rs.getString("FILETYPE");
            modt = rs.getLong(  "MODIFIED");
            size = rs.getLong(  "FILESIZE");
            clmd = rs.getLong(  "CLIENTMODIFIED");
            
            if (name != null && loca != null && srvg != null && type != null)
            {
                
                String line = "Result #"+count+": "+name+","+loca+","+srvg+","+type+","+modt+","+size+","+clmd+"\n";
                os.write(line.getBytes());
            }
        }
        rs.close();
        this.prepSelectN.clearWarnings();
    }
    
    //---------------------------------------------------------------------
    
    protected void _checkConnection() throws SQLException
    {
        _getConnection();        
    }
    
    
    //---------------------------------------------------------------------

    
    //---------------------------------------------------------------------
    //---------------------------------------------------------------------

    protected void testDatabase() throws SQLException
    {
        KomodoTableUtil dbtester = new KomodoTableUtil();
        //Connection c = _getConnection();
        Connection c = DriverManager.getConnection(connectionUrl);
        
        dbtester.checkTables(c);        
        c.clearWarnings();
        c.close();
    }
    
    
    public void close()
    {
        _closeConnection();
    }
    //---------------------------------------------------------------------    
    
    
    class KomodoTableUtil
    {
        String createKomodoFilesTableCmd = "CREATE TABLE KOMODO_FILES "+
            "(FILE_ID INT NOT NULL GENERATED ALWAYS AS IDENTITY "+
            //"              CONSTRAINT FILE_PK PRIMARY KEY, " +
            ", " +
            " ENTRY_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            " SPECIAL_FIELD TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            " FILENAME VARCHAR(128)  NOT NULL, " +
            " LOCATION VARCHAR(256) NOT NULL, " +
            " SERVERGROUP VARCHAR(32)  NOT NULL, " +
            " FILETYPE VARCHAR(128)  NOT NULL, " +
            " MODIFIED BIGINT NOT NULL, " +
            " FILESIZE BIGINT NOT NULL, " +
            " CLIENTMODIFIED BIGINT NOT NULL," +
            " PRIMARY KEY (FILENAME, SERVERGROUP, FILETYPE)) ";
        
//        String createIndexTypeCmd = "CREATE INDEX FILETYPE_IDX ON KOMODO_FILES(FILETYPE)";
//        String createIndexSGrpCmd = "CREATE INDEX SERVERGROUP_IDX ON KOMODO_FILES(SERVERGROUP)";
        
        String createKomodoPropsTableCmd = "CREATE TABLE KOMODO_PROPERTIES "+
            "(PROPERTY_ID INT NOT NULL GENERATED ALWAYS AS IDENTITY "+
            "             CONSTRAINT PROPERTY_PK PRIMARY KEY, " +
            " PROPERTY_NAME VARCHAR(128)  NOT NULL, " +
            " PROPERTY_VALUE BIGINT NOT NULL) ";
        
        String checkKomodoFilesTableCmd = "update KOMODO_FILES set ENTRY_DATE = CURRENT_TIMESTAMP, " +
            "SPECIAL_FIELD = 'Test', FILENAME = 'Test', LOCATION = 'Test', "+
            "SERVERGROUP = 'Test', FILETYPE = 'Test', MODIFIED = 0, FILESIZE = 0, CLIENTMODIFIED = 0 where 1=3";
        
        String checkKomodoPropsTableCmd = "update KOMODO_PROPERTIES set  " +
            "PROPERTY_NAME = 'Test', PROPERTY_VALUE = 0 where 1=3";
        
        //-----------------------------------------------------------------
        
        public void checkTables(Connection c) throws SQLException
        {
            boolean filesTableExists = false, propsTableExists = false;
            
            filesTableExists = tableExists(c, checkKomodoFilesTableCmd);
            propsTableExists = tableExists(c, checkKomodoPropsTableCmd);
         
            if (!filesTableExists)
            {
                //create file entry table
                createTable(c, createKomodoFilesTableCmd);
                
//                //create indices on fields
//                createTable(c, createIndexTypeCmd);
//                createTable(c, createIndexSGrpCmd);
            }        
            
            if (!propsTableExists)
            {
                createTable(c, createKomodoPropsTableCmd);
            }            
        }
        
        //-----------------------------------------------------------------
        
        protected boolean createTable(Connection conn, String createCommand) 
                                                         throws SQLException 
        {        
            try {
               
               Statement s = conn.createStatement();
               s.execute(createCommand);
               
            }  catch (SQLException sqle) {
                
               //String theError = (sqle).getSQLState();
               throw sqle;               
            }
            return true;
         }
        
        //-----------------------------------------------------------------
        
        protected boolean tableExists(Connection conn, String testCommand) 
                                                       throws SQLException 
        {        
            try {
               
               Statement s = conn.createStatement();
               s.execute(testCommand);
               
            }  catch (SQLException sqle) {
                
               String theError = (sqle).getSQLState();

               /** If table exists will get -  WARNING 02000: No row was found **/
               if (theError.equals("42X05"))   // Table does not exist
               {  
                   return false;
               }  
               else if (theError.equals("42X14") || theError.equals("42821"))  
               {
                   //System.out.println("WwdChk4Table: Incorrect table definition.");
                   throw sqle;   
               } 
               else 
               { 
                   //System.out.println("WwdChk4Table: Unhandled SQLException" );
                   throw sqle; 
               }
            }
            return true;
         }
        
        //-----------------------------------------------------------------
    }

    //---------------------------------------------------------------------
    
    /**
     * Clears the database of records.  This should only be called
     * by an admin.
     * @return True if file was added, false otherwise
     */
    
    public boolean clearRecords() throws SQLException
    {
        return _removeAllRecords();
    }
    
    //---------------------------------------------------------------------
    
    
    /**
     * Returns a list of records where list is composed of the oldest
     * <i>count</i> records sorted in ascending order by modification time.
     * @param sgftRecord FileRecord containing values for servergroup and 
     *        filetype
     * @param count Maximum size of list
     * @return List of FileRecords
     */
    
    public List<FileRecord> getOldestRecords(FileRecord sgftRecord, int count) throws SQLException
    {        
        return _listNFiles(sgftRecord,count);        
    }
    
    //---------------------------------------------------------------------
    
    public boolean doForAllRecords(FileRecord sgftRecord, QueryResultHandler handler) throws SQLException
    {        
        return _forAllMatchingFiles(sgftRecord, handler);        
    }
    
    //---------------------------------------------------------------------
}
