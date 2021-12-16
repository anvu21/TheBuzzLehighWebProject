package edu.lehigh.cse216.runtimers.admin;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

//unit tests for Database.java
public class DatabaseTest extends TestCase {
    /**
     * Create the test case
     * @param testName name of the test case
     */
    public DatabaseTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(DatabaseTest.class);
    }

    /**
     * so far there are only RowData that need to be unit tested
     * for the constructor there's nothing to be tested since it is private and has no function in it
     * for getDatabase I would need connection to Heroku which is not a good practice for UnitTesting
     * String subject, String message, String author, int id, int likes, int dislikes
    */

    public void testRowData(){
        // create a random rowdata to be tested
        Database.RowData test = new Database.RowData(1, "Testing", "123", "muy223@lehigh.edu", 12, 5);
        // check id
        assertTrue(test.mId == 1);
        // check subject
        assertTrue(test.mSubject.equals("Testing"));
        // check message
        assertTrue(test.mMessage.equals("123"));
        // check author
        assertTrue(test.mAuthor.equals("muy223@lehigh.edu"));
        // check upvotes
        assertTrue(test.mUpvotes == 12);
        // check downvotes
        assertTrue(test.mDownvotes == 5);
    }
    public void testUserRow(){
        //create a random UserRow to be tested
        Database.UserRow test = new Database.UserRow("cool_user", "cool", "I'm the coolest");
        //check user id
        assertTrue(test.mUserId.equals("cool_user"));
        //check name
        assertTrue(test.mName.equals("cool"));
        //check bio
        assertTrue(test.mBio.equals( "I'm the coolest"));
    }

    public void testCommentRow(){
        //create a random commentRow to be tested
        Database.CommentRow test = new Database.CommentRow(1, "cooler_user", 4, "I'm cooler than the coolest");
        //check comment id
        assertTrue(test.mId == 1);
        //check user id
        assertTrue(test.mUserId.equals("cooler_user"));
        //check message id
        assertTrue(test.mMsgId == 4);
        //check comment
        assertTrue(test.mBody.equals("I'm cooler than the coolest"));
    }
}