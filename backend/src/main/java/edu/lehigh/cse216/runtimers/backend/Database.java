package edu.lehigh.cse216.runtimers.backend;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Database{
    private Connection mConnection;

    //A prepared statement for getting all data in the database
    private PreparedStatement mSelectAll;

    //A prepared statement for getting one row from the database 
    private PreparedStatement mSelectOne;

    //A prepared statement for deleting a row from the database
    private PreparedStatement mDeleteOne;

    //A prepared statement for inserting into the database
    private PreparedStatement mInsertOne;

    // A prepared statement for incrementing upvotes for a row in the database
    private PreparedStatement mUpvote;

    // A prepared statement for incrementing downvotes for a row in the database
    private PreparedStatement mDownvote;

    // A prepared statement for decrementing upvotes for a row in the database
    private PreparedStatement mRemoveUpvote;

    // A prepared statement for decrementing downvotes for a row in the database
    private PreparedStatement mRemoveDownvote;

    // A prepared statement for updating a single row in the database
    private PreparedStatement mUpdateOne;

    //A prepared statement for creating the table in our database
    private PreparedStatement mCreateTable;

    //A prepared statement for dropping the table in our database
    private PreparedStatement mDropTable;

    
    public static class RowData {
        //The ID of this row of the database
        int mId;
        //The subject stored in this row
        String mSubject;
        //The message stored in this row
        String mMessage;
        //The number of likes
        int mUpvotes;
        //The number of dislikes
        int mDownvotes;

        //Construct a RowData object by providing values for its fields
        public RowData(int id, String subject, String message, int upvotes, int downvotes) {
            mId = id;
            mSubject = subject;
            mMessage = message;
	        mUpvotes = upvotes;
	        mDownvotes = downvotes;
        }
    }

    private Database() {}
    /**
     * Get a fully-configured connection to the database
     * 
     * @param ip   The IP address of the database server
     * @param port The port on the database server to which connection requests should be sent
     * @param user The user ID to use when connecting
     * @param pass The password to use when connecting
     * 
     * @return A Database object, or null if we cannot connect properly
     */
    static Database getDatabase(String db_url) {
        //String ip, String port, String user, String pass
        // Create an un-configured Database object
        Database db = new Database();

        // Give the Database object a connection, fail if we cannot get one
        try {
            Class.forName("org.postgresql.Driver");
            URI dbUri = new URI(db_url);
            String user = dbUri.getUserInfo().split(":")[0];
            String pass = dbUri.getUserInfo().split(":")[1];
            String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() + "?sslmode=require";
            Connection conn = DriverManager.getConnection(dbUrl, user, pass);
            if (conn == null){
                System.err.println("Error: DriverManager.getConnection() returned a null object");
                return null;
            }
            db.mConnection = conn;
        }catch (SQLException e) {
            System.err.println("Error: DriverManager.getConnection() threw a SQLException");
            e.printStackTrace();
            return null;
        }
        catch(ClassNotFoundException cnfe){
            System.out.println("Unable to find postgresql driver");
            cnfe.printStackTrace();
            return null;
        }
        catch(URISyntaxException s){
            System.out.println("URI Syntax Error");
            return null;
        }
        // Attempt to create all of our prepared statements.  
        // If any of these fail, the whole getDatabase() call should fail
        try {
            System.out.println("running prepared statements");
            db.mCreateTable = db.mConnection.prepareStatement(
                    "CREATE TABLE tblData (id SERIAL PRIMARY KEY,"
                    +"subject VARCHAR(100) NOT NULL, "
                    +"message VARCHAR(500) NOT NULL, "
                    +"upvotes int NOT NULL CHECK (upvotes >= 0),"
                    +"downvotes int NOT NULL CHECK (downvotes >= 0))");
            db.mDropTable = db.mConnection.prepareStatement("DROP TABLE tblData");

            // Standard CRUD operations
            db.mDeleteOne = db.mConnection.prepareStatement("DELETE FROM tblData WHERE id = ?");
            db.mInsertOne = db.mConnection.prepareStatement("INSERT INTO tblData VALUES (default, ?, ?, ?, ?)");
            db.mSelectAll = db.mConnection.prepareStatement("SELECT * FROM tblData ORDER BY id ASC");
            db.mSelectOne = db.mConnection.prepareStatement("SELECT * from tblData WHERE id=?");
            db.mUpdateOne = db.mConnection.prepareStatement("UPDATE tblData SET subject = ?, message = ? WHERE id = ?");
	        db.mUpvote = db.mConnection.prepareStatement("UPDATE tblData SET upvotes = upvotes + 1 WHERE id = ?");
	        db.mDownvote = db.mConnection.prepareStatement("UPDATE tblData SET downvotes = downvotes + 1 WHERE id = ?");
            db.mRemoveUpvote = db.mConnection.prepareStatement("UPDATE tblData SET upvotes = upvotes - 1 WHERE id = ?");
	        db.mRemoveDownvote = db.mConnection.prepareStatement("UPDATE tblData SET downvotes = downvotes - 1 WHERE id = ?");

        } catch (SQLException e) {
            System.err.println("Error creating prepared statement");
            e.printStackTrace();
            db.disconnect();
            return null;
        }
        return db;
    }

    boolean disconnect() {
        if (mConnection == null) {
            System.err.println("Unable to close connection: Connection was null");
            return false;
        }
        try {
            mConnection.close();
        } catch (SQLException e) {
            System.err.println("Error: Connection.close() threw a SQLException");
            e.printStackTrace();
            mConnection = null;
            return false;
        }
        mConnection = null;
        return true;
    }

    /**
     * Insert a row into the database
     * 
     * @param subject The subject for this new row
     * @param message The message body for this new row
     * @param author The user_id who created this new message
     * @param likes The number of likes of this message
     * @param disikes The number of dislikes of this message
     * @return The number of rows that were inserted
     */
    int insertRow(String subject, String message, int upvotes, int downvotes) {
        int count = 0;
        try {
            mInsertOne.setString(1, subject);
            mInsertOne.setString(2, message);
            mInsertOne.setInt(3, upvotes);
            mInsertOne.setInt(4, downvotes);
            count += mInsertOne.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Query the database for a list of all subjects and their IDs
     * 
     * @return All rows, as an ArrayList
     */
    ArrayList<RowData> selectAll() {
        ArrayList<RowData> res = new ArrayList<RowData>();
        try {
            ResultSet rs = mSelectAll.executeQuery();
            while (rs.next()) {
                res.add(new RowData(rs.getInt("id"), rs.getString("subject"), rs.getString("message"), rs.getInt("upvotes"), rs.getInt("downvotes")));
            }
            rs.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get all data for a specific row, by ID
     * 
     * @param id The id of the row being requested
     * 
     * @return The data for the requested row, or null if the ID was invalid
     */
    RowData selectOne(int id) {
        RowData res = null;
        try {
            mSelectOne.setInt(1, id);
            ResultSet rs = mSelectOne.executeQuery();
            if (rs.next()) {
                res = new RowData(rs.getInt("id"),
                rs.getString("subject"),
                rs.getString("message"),
                rs.getInt("upvotes"),
                rs.getInt("downvotes"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Delete a row by ID
     * 
     * @param id The id of the row to delete
     * 
     * @return The number of rows that were deleted.  -1 indicates an error.
     */
    int deleteRow(int id) {
        int res = -1;
        try {
            mDeleteOne.setInt(1, id);
            res = mDeleteOne.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Update the message for a row in the database
     * 
     * @param id The id of the row to update
     * @param message The new message contents
     * 
     * @return The number of rows that were updated.  -1 indicates an error.
     */
    int updateOne(int id, String subject, String message) {
        int res = -1;
        try {
            mUpdateOne.setString(1, subject);
            mUpdateOne.setString(2, message);
            mUpdateOne.setInt(3, id);
            res = mUpdateOne.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Increment upvote for a row.
     *
     * @param id The id of the row to update
     * @return res
     */
    int addUpvote(int id) {
        int res = -1;
        try {
	        mUpvote.setInt(1, id);
	        res = mUpvote.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
	    return res;
    }

    /**
     * Increment downvote for a row.
     *
     * @param id The id of the row to update
     * @return res
     */
    int addDownvote(int id) {
        int res = -1;
        try {
	        mDownvote.setInt(1, id);
	        res = mDownvote.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Decrement upvote for a row.
     *
     * @param id The id of the row to update
     * @return res
     */
    int removeUpvote(int id) {
        int res = -1;
        try {
	        mRemoveUpvote.setInt(1, id);
	        res = mRemoveUpvote.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
	    return res;
    }

    /**
     * Decrement downvote for a row.
     *
     * @param id The id of the row to update
     * @return res
     */
    int removeDownvote(int id) {
        int res = -1;
        try {
	        mRemoveDownvote.setInt(1, id);
	        res = mRemoveDownvote.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Create DataTbl.  If it already exists, this will print an error
     */
    void createTable() {
        try {
            mCreateTable.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove DataTbl from the database.  If it does not exist, this will print
     * an error.
     */
    void dropTable() {
        try {
            mDropTable.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}