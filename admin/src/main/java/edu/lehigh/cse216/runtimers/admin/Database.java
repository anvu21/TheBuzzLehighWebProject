package edu.lehigh.cse216.runtimers.admin;

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

    // prepared statements for creating and dropping tables: Users, Comments, and likes
    private PreparedStatement mCreateTable;
    private PreparedStatement mDropTable;
    private PreparedStatement mCreateUsersTable;
    private PreparedStatement mDropUsersTable;
    private PreparedStatement mCreateCommentTable;
    private PreparedStatement mDropCommentTable;
    private PreparedStatement mCreateLikesTable;
    private PreparedStatement mDropLikesTable;

    // prepared statements for Users table
    private PreparedStatement mInsertOneUser;
    private PreparedStatement mSelectOneUser;
    private PreparedStatement mSelectAllUsers;
    private PreparedStatement mUpdateUserBio;

    // prepared statements for Comment table
    private PreparedStatement mInsertOneComment;
    private PreparedStatement mUpdateOneComment;
    private PreparedStatement mSelectOneComment;
    private PreparedStatement mSelectAllComments;
    private PreparedStatement mDeleteOneComment;

    // prepared statements for Likes table
    private PreparedStatement mInsertLikes;
    private PreparedStatement mDeleteLikes;
    private PreparedStatement mSelectLikedPost;

    
    public static class RowData {
        //The ID of this row of the database
        int mId;
        //The subject stored in this row
        String mSubject;
        //The message stored in this row
        String mMessage;
        //The user id of the author
        String mAuthor;
        //The number of likes
        int mUpvotes;
        //The number of dislikes
        int mDownvotes;

        //Construct a RowData object by providing values for its fields
        public RowData(int id, String subject, String message, String author, int upvotes, int downvotes) {
            mId = id;
            mSubject = subject;
            mMessage = message;
            mAuthor = author;
	        mUpvotes = upvotes;
	        mDownvotes = downvotes;
        }
    }

    //Row member for User table
    public static class UserRow {
        //the ID of the user
        String mUserId;
        //the name of the user
        String mName;
        //the bio of the user
        String mBio;
        //Construct a UserRow object by providing values for its fields
        public UserRow(String UserId, String Name, String Bio){
            mUserId = UserId;
            mName = Name;
            mBio = Bio;
        }
    }
   
    //Row member for comment table
    public static class CommentRow {
        //the ID of the comment
        int mId;
        //the ID of the user
        String mUserId;
        //the ID of the message
        int mMsgId;
        //the body of the comment
        String mBody;
        //Construct a CommentRow object by providing values for its fields
        public CommentRow(int id, String UserId, int MsgId, String body){
            mId = id;
            mUserId = UserId;
            mMsgId = MsgId;
            mBody = body;
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
                    + "subject VARCHAR(100) NOT NULL, "
                    + "message VARCHAR(500) NOT NULL, "
                    + "author VARCHAR(200) NOT NULL,"
                    + "upvotes int NOT NULL CHECK (upvotes >= 0),"
                    + "downvotes int NOT NULL CHECK (downvotes >= 0))");
            db.mDropTable = db.mConnection.prepareStatement("DROP TABLE tblData");

            // Standard CRUD operations for tblData
            db.mDeleteOne = db.mConnection.prepareStatement("DELETE FROM tblData WHERE id = ?");
            db.mInsertOne = db.mConnection.prepareStatement("INSERT INTO tblData VALUES (default, ?, ?, ?, ?, ?)");
            db.mSelectAll = db.mConnection.prepareStatement("SELECT * FROM tblData ORDER BY id ASC");
            db.mSelectOne = db.mConnection.prepareStatement("SELECT * from tblData WHERE id = ?");
            db.mUpdateOne = db.mConnection.prepareStatement("UPDATE tblData SET subject = ?, message = ? WHERE id = ?");
	        db.mUpvote = db.mConnection.prepareStatement("UPDATE tblData SET upvotes = upvotes + 1 WHERE id = ?");
	        db.mDownvote = db.mConnection.prepareStatement("UPDATE tblData SET downvotes = downvotes + 1 WHERE id = ?");
            db.mRemoveUpvote = db.mConnection.prepareStatement("UPDATE tblData SET upvotes = upvotes - 1 WHERE id = ?");
	        db.mRemoveDownvote = db.mConnection.prepareStatement("UPDATE tblData SET downvotes = downvotes - 1 WHERE id = ?");

            // Phase 2 tables:
            /**
             * Users table to store user_id, name, and bio
             * primary key: user_id (the email of user)
             */
            db.mCreateUsersTable = db.mConnection.prepareStatement(
                "CREATE TABLE Users (user_id VARCHAR(200) PRIMARY KEY, "
                + "name VARCHAR(30) NOT NULL, " 
                + "bio VARCHAR(500))");
            db.mDropUsersTable = db.mConnection.prepareStatement("DROP TABLE Users");

            /**
             * Comments table to store id, user_id, msg_id, and body
             * primary key: c_id
             * foreign key: msg_id, user_id
             */ 
            db.mCreateCommentTable = db.mConnection.prepareStatement(
                "CREATE TABLE Comments (c_id SERIAL PRIMARY KEY,"
                + "user_id VARCHAR(200) NOT NULL, "
                + "msg_id int NOT NULL, "
                + "body VARCHAR(1000) NOT NULL, "
                + "FOREIGN KEY(msg_id) REFERENCES tblData on delete cascade, " 
                + "FOREIGN KEY(user_id) REFERENCES Users on delete cascade)");
            db.mDropCommentTable = db.mConnection.prepareStatement("DROP TABLE Comments");
            
            /**
             * Likes table to store user_id, msg_id, and is_like
             * primary key: msg_id, user_id
             * foreign key: msg_id, user_id
             */
            db.mCreateLikesTable = db.mConnection.prepareStatement(//Only one user can like one comment only once
                "CREATE TABLE Likes (user_id VARCHAR(200),"
                + "msg_id int NOT NULL, " 
                + "is_like Boolean NOT NULL, "
                + "FOREIGN KEY(msg_id) REFERENCES tblData on delete cascade, "
                + "FOREIGN KEY(user_id) REFERENCES Users on delete cascade, "
                + "PRIMARY KEY(user_id, msg_id))"); 
            db.mDropLikesTable = db.mConnection.prepareStatement("DROP TABLE Likes");

            // Standard CRUD operations for Users table
            db.mInsertOneUser = db.mConnection.prepareStatement("INSERT INTO Users VALUES (?, ?, ?)");
            db.mSelectOneUser = db.mConnection.prepareStatement("SELECT * from Users WHERE user_id = ?");
            db.mSelectAllUsers = db.mConnection.prepareStatement("SELECT * from Users");
            db.mUpdateUserBio = db.mConnection.prepareStatement("UPDATE Users SET bio = ? WHERE user_id = ?");
            // Standard CRUD operations for Comments table
            db.mInsertOneComment = db.mConnection.prepareStatement("INSERT INTO Comments VALUES (default, ?, ?, ?)");
            db.mUpdateOneComment = db.mConnection.prepareStatement("UPDATE Comments SET body = ? WHERE c_id = ? and msg_id = ? and user_id = ?"); 
            db.mSelectOneComment = db.mConnection.prepareStatement("SELECT * from Comments WHERE c_id = ? and msg_id = ?");
            db.mSelectAllComments = db.mConnection.prepareStatement("SELECT * from Comments WHERE msg_id = ?");
            db.mDeleteOneComment = db.mConnection.prepareStatement("DELETE FROM Comments WHERE c_id = ? and msg_id = ? and user_id = ?");
            // Standard CRUD operations for Likes table
            db.mInsertLikes = db.mConnection.prepareStatement("INSERT INTO Likes VALUES (?, ?, ?)");
            db.mDeleteLikes = db.mConnection.prepareStatement("DELETE FROM Likes WHERE user_id = ? and msg_id = ? and is_like = ?");
            db.mSelectLikedPost = db.mConnection.prepareStatement("SELECT msg_id from Likes WHERE user_id = ? and is_like = true");


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
     * @param upvotes The number of likes of this message
     * @param downvotes The number of dislikes of this message
     * @return The number of rows that were inserted
     */
    int insertRow(String subject, String message, String author, int upvotes, int downvotes) {
        int count = 0;
        try {
            mInsertOne.setString(1, subject);
            mInsertOne.setString(2, message);
            mInsertOne.setString(3, author);
            mInsertOne.setInt(4, upvotes);
            mInsertOne.setInt(5, downvotes);
            count += mInsertOne.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Query the database for a list of all titles, messages, authors, likes and dislikes
     * 
     * @return All rows, as an ArrayList
     */
    ArrayList<RowData> selectAll() {
        ArrayList<RowData> res = new ArrayList<RowData>();
        try {
            ResultSet rs = mSelectAll.executeQuery();
            while (rs.next()) {
                res.add(new RowData(rs.getInt("id"), 
                rs.getString("subject"), 
                rs.getString("message"), 
                rs.getString("author"),
                rs.getInt("upvotes"),
                rs.getInt("downvotes")));
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
                rs.getString("author"),
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
     * @param subject The new title content
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
     * @return The number of rows that were updated. -1 indicates an error.
     */
    int addUpvote(int id, String user_id) {
        int res = -1;
        try {
	        mUpvote.setInt(1, id);
            //insert likes to Likes table 
            mInsertLikes.setString(1, user_id);
            mInsertLikes.setInt(2, id);
            mInsertLikes.setBoolean(3, true);
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
     * @return The number of rows that were updated. -1 indicates an error.
     */
    int addDownvote(int id, String user_id) {
        int res = -1;
        try {
	        mDownvote.setInt(1, id);
            //insert dislikes to Likes table
            mInsertLikes.setString(1, user_id);
            mInsertLikes.setInt(2, id);
            mInsertLikes.setBoolean(3, false);
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
     * @return The number of rows that were updated. -1 indicates an error.
     */
    int removeUpvote(int id, String user_id) {
        int res = -1;
        try {
	        mRemoveUpvote.setInt(1, id);
            //remove likes from Likes table
            mDeleteLikes.setString(1, user_id);
            mDeleteLikes.setInt(2, id);
            mDeleteLikes.setBoolean(3, true);
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
     * @return The number of rows that were updated. -1 indicates an error.
     */
    int removeDownvote(int id, String user_id) {
        int res = -1;
        try {
	        mRemoveDownvote.setInt(1, id);
            //remove dislikes from Likes table
            mDeleteLikes.setString(1, user_id);
            mDeleteLikes.setInt(2, id);
            mDeleteLikes.setBoolean(3, false);
            res = mRemoveDownvote.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /** Add users and emails function
     * Insert a user into the Users table
     * 
     * @param userId The email id of the user
     * @param name The full name of the user
     * @return The number of rows that were inserted
     */
    int addUser(String userId, String name, String bio) {
        int res = 0;
        try {
            mInsertOneUser.setString(1, userId);
            mInsertOneUser.setString(2, name);
            mInsertOneUser.setString(3, bio);
            res += mInsertOneUser.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        return res;
    }

    ArrayList<UserRow> selectAllUsers() {
        ArrayList<UserRow> res = new ArrayList<UserRow>();
        try {
            ResultSet rs = mSelectAllUsers.executeQuery();
            while (rs.next()) {
                res.add(new UserRow(rs.getString("user_id"), 
                rs.getString("name"), 
                rs.getString("bio")));
            }
            rs.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    UserRow selectOneUser(String user_id) {
        UserRow res = null;
        try {
            mSelectOneUser.setString(1, user_id);
            ResultSet rs = mSelectOneUser.executeQuery();
            if(rs.next()){
                res = new UserRow(rs.getString("user_id"), 
                rs.getString("name"), 
                rs.getString("bio"));
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /* Insert a comment into the comment table
     * 
     * @param user_id The email id of the user
     * @param msg_id The id of the message
     * @param body The body of the comment itself
     * @return The number of rows that were inserted
     */
    int addComment (String user_id, int msg_id, String body){
        int c = 0; //count
        try {
            mInsertOneComment.setString(1, user_id);
            mInsertOneComment.setInt(2, msg_id);
            mInsertOneComment.setString(3, body);
            c += mInsertOneComment.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return c;
    }

    ArrayList<CommentRow> selectAllComments(int msg_id) {
        ArrayList<CommentRow> res = new ArrayList<CommentRow>();
        try {
            mSelectAllComments.setInt(1, msg_id);
            ResultSet rs = mSelectAllComments.executeQuery();
            while (rs.next()) {
                res.add(new CommentRow(rs.getInt("c_id"), 
                rs.getString("user_id"), 
                rs.getInt("msg_id"), 
                rs.getString("body")));
            }
            rs.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    CommentRow selectOneComment(int cid, int mid) {
        CommentRow res = null;
        try {
            mSelectOneComment.setInt(1, cid);
            mSelectOneComment.setInt(2, mid);
            ResultSet rs = mSelectOneComment.executeQuery();
            if(rs.next()){
                res = new CommentRow(rs.getInt("c_id"), 
                rs.getString("user_id"), 
                rs.getInt("msg_id"), 
                rs.getString("body"));
            }
        }catch (SQLException e) {
                e.printStackTrace();
        }
        return res;
    }

    int UpdateOneComment(String body, int c_id, int msg_id, String user_id) {
        int res = 0;
        try{
            mUpdateOneComment.setString(1, body);
            mUpdateOneComment.setInt(2, c_id);
            mUpdateOneComment.setInt(3, msg_id);
            mUpdateOneComment.setString(4, user_id);
            res += mUpdateOneComment.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    int deleteOneComment(int cid, int mid) {
        int res = -1;
        try {
            mDeleteOneComment.setInt(1, cid);
            mDeleteOneComment.setInt(2, mid);
            res = mDeleteOneComment.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return res;
    }

    // Create table named "tblData" to the database
    void createTable() {
        try {
            mCreateTable.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Remove DataTbl from the database.
    void dropTable() {
        try {
            mDropTable.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // Create table named "Users" to the database
    void createUsersTable() {
        try {
            mCreateUsersTable.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Remove Users table from the database
    void dropUsersTable() {
        try {
            mDropUsersTable.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Create table named "Comments" to the database
    void createCommentsTable() {
        try {
            mCreateCommentTable.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Remove Comments table from the database
    void dropCommentsTable() {
        try {
            mDropCommentTable.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Create table named "Likes" to the database
    void createLikesTable() {
        try {
            mCreateLikesTable.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Remove Likes table from the database
    void dropLikesTable() {
        try {
            mDropLikesTable.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}