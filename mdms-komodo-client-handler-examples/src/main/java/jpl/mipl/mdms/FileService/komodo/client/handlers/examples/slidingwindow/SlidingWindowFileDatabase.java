package jpl.mipl.mdms.FileService.komodo.client.handlers.examples.slidingwindow;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

/**
 * Interface for Sliding Window database classes.  Abstracts the underlying database
 * implementation for handler.
 * 
 * @author Nicholas Toole   (Nicholas.T.Toole@jpl.nasa.gov)
 * @version $Id: SlidingWindowFileDatabase.java,v 1.1 2011/05/25 22:18:46 ntt Exp $
 *
 */

public interface SlidingWindowFileDatabase
{
    
    //---------------------------------------------------------------------
    
    /** 
     * Initialize the database instance
     */
    
    public void initialize(String name, String location, Properties props) throws Exception;

    
    //---------------------------------------------------------------------
    
    /** 
     * Get a numeric value stored in the database.
     * @param name Property name
     * @return Value associated with name, 0 if not found.
     */
    
    public long getValue(String name) throws SQLException;

    //---------------------------------------------------------------------
    
    /** 
     * Set a numeric value stored in the database.
     * @param name Property name
     * @param value Value associated with name.
     */
    
    public void setValue(String name, long value) throws SQLException;
    
    //---------------------------------------------------------------------
    
    /** 
     * Update a numeric value stored in the database by an changed amount
     * @param name Property name
     * @param deltaValue Difference by which the property will be 
     *        incremented/decremented.
     */
    
    public void updateValueByDelta(String name, long deltaValue) throws SQLException;
    
    //---------------------------------------------------------------------
    
    
    /**
     * Store file record to database based on state of file record
     * @param fileRecord File metadata information
     * @return True if file was added, false otherwise
     */
    
    public boolean addRecord(FileRecord fileRecord) throws SQLException;
    
    //---------------------------------------------------------------------
    
    
    /**
     * Clears the database of records.  This should only be called
     * by an admin.
     * @return True if file was added, false otherwise
     */
    
    public boolean clearRecords() throws SQLException;
    
    //---------------------------------------------------------------------
    
    /**
     * Returns a list of records where list is composed of the oldest
     * <i>count</i> records sorted in ascending order by modification time.
     * @param sgftRecord FileRecord containing values for servergroup and 
     *        filetype
     * @param count Maximum size of list
     * @return List of FileRecords
     */
    
    public List<FileRecord> getOldestRecords(FileRecord sgftRecord, int count) throws SQLException;
    
    //---------------------------------------------------------------------
    
    /**
     * Remove file record from database
     * @param fileRecord File metadata information
     * @return True if file was removed, false otherwise
     */
    
    public boolean removeRecord(FileRecord fileRecord) throws SQLException;

    //---------------------------------------------------------------------
    
    public void printRecords(OutputStream os) throws SQLException, IOException;
    
    //---------------------------------------------------------------------
    
    public void close() throws SQLException;

    //---------------------------------------------------------------------
    
    public boolean doForAllRecords(FileRecord sgftRecord, QueryResultHandler handler) throws SQLException;
    
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
    
    public long getRecordCount(String servergroup, String filetype) throws SQLException;
    
    //---------------------------------------------------------------------
    
    /**
     * Returns the total filesize of records in database matching parameters.  If
     * both servergroup and filetype are specified, returns size for number
     * of rows in filetype.  If only servergroup is returned, returns size
     * of rows in servergroup, across filetypes.  If neither parameter is
     * specified, then size for all rows.  If only filetype is specified
     * then 0 is returned.
     * @param servergroup Servergroup name, can be null
     * @param filetype Filetype name, can be null
     * @return Total filesize of records matching parameters
     */
    
    public long getTotalFilesize(String servergroup, String filetype) throws SQLException;
    
    //---------------------------------------------------------------------
}
