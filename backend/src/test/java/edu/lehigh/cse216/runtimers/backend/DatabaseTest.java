package edu.lehigh.cse216.runtimers.backend;

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
        //create a random rowdata to be tested
        Database.RowData test = new Database.RowData(1, "Hello", "HelloAgain", 8, 66);
        //check id
        assertTrue(test.mId == 1);
        //check subject
        assertTrue(test.mSubject.equals("Hello"));
        //check message
        assertTrue(test.mMessage.equals("HelloAgain"));
        //check upvotes
        assertTrue(test.mUpvotes == 8);
        //check downvotes
        assertTrue(test.mDownvotes == 66);
    }
}