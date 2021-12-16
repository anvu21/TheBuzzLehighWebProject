package edu.lehigh.cse216.runtimers.admin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Map;

/**
 * App is our basic admin app.  For now, it is a demonstration of the six key 
 * operations on a database: connect, insert, update, query, delete, disconnect
 */
public class App {

    /**
     * Print the menu for our program
     */
    static void menu() {
        System.out.println("Main Menu");

        //messages table
        System.out.println("  [T] Create tblData (create new message table)"); //done
        System.out.println("  [D] Drop tblData (delete message table)"); //done
        System.out.println("  [1] Query for a specific row (get a specific message)"); //done
        System.out.println("  [*] Query for all rows (Get all messages)");//done
        System.out.println("  [-] Delete a row (Delete a message)"); //done
        System.out.println("  [+] Insert a new row (Add a new message)");//done
        System.out.println("  [~] Update a row (Update an existing message)");//done
        System.out.println("  [A] Add a like"); //done
        System.out.println("  [a] Unlike a like"); //done
        System.out.println("  [B] Add a dislike"); //done
        System.out.println("  [b] Take away a dislike"); //done
        System.out.println("  [q] Quit Program");//done
        System.out.println("  [?] Help (this message)");//done

        //user table
        System.out.println("  [C] Create usertable"); //done
        System.out.println("  [c] Drop usertable"); //done
        System.out.println("  [I] Insert a user"); //done
        System.out.println("  [S] Select a user"); //done
        System.out.println("  [s] Select all users"); //done
        // System.out.println("  [d] Update user bio");     // not implemented

        //like table
        System.out.println("  [L] Create liketable"); //done
        System.out.println("  [l] Drop liketable"); //done
        // Likes and dislikes are automatically filled in this table when a user likes
        // a message in the message table so liking/disliking action is not needed here

        //comment table
        System.out.println("  [R] Create commenttable"); //done
        System.out.println("  [r] Drop commenttable"); //done
        System.out.println("  [#] Make a new comment");
        System.out.println("  [@] Delete a comment");
        System.out.println("  [$] Edit a comment");
        System.out.println("  [^] Select and display a comment");
        System.out.println("  [&] Select and display all comments");
        
    }

    /**
     * Ask the user to enter a menu option; repeat until we get a valid option
     * 
     * @param in A BufferedReader, for reading from the keyboard
     * 
     * @return The character corresponding to the chosen menu option
     */
    static char prompt(BufferedReader in) {
        // The valid actions:
        String actions = "TD1*-+~AaBbq?CcISsdLlRr#@$^&";

        // We repeat until a valid single-character option is selected        
        while (true) {
            System.out.print("[" + actions + "] :> ");
            String action;
            try {
                action = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }

            if (action.length() != 1){
                System.out.println("  [ERROR] Invalid Command");
                continue;
            }
                
            if (actions.contains(action)) {
                return action.charAt(0);
            } else{
                System.out.println("  [ERROR] Invalid Command");
            }
            
        }
    }

    /**
     * Ask the user to enter a String message
     * 
     * @param in A BufferedReader, for reading from the keyboard
     * @param message A message to display when asking for input
     * 
     * @return The string that the user provided.  May be "".
     */
    static String getString(BufferedReader in, String message) {
        String s;
        try {
            System.out.print(message + " :> ");
            s = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return s;
    }

    /**
     * Ask the user to enter an integer
     * 
     * @param in A BufferedReader, for reading from the keyboard
     * @param message A message to display when asking for input
     * 
     * @return The integer that the user provided.  On error, it will be -1
     */
    static int getInt(BufferedReader in, String message) {
        int i = -1;
        try {
            System.out.print(message + " :> ");
            i = Integer.parseInt(in.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return i;
    }

    /**
     * The main routine runs a loop that gets a request from the user and
     * processes it
     * 
     * @param argv Command-line options.  Ignored by this program.
     */
    public static void main(String[] argv) {
        // get the Postgres configuration from the environment
        String dbName = "postgres://tsfckvjlfejcen:5881f854074caa7a310678a384311d9deddf589662af9198142c9708aae4619e@ec2-54-146-84-101.compute-1.amazonaws.com:5432/dapbocqmelis1n";

        // Get a fully-configured connection to the database, or exit 
        // immediately
        Database db = Database.getDatabase(dbName);
        if (db == null)
            return;

        // Start our basic command-line interpreter:
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        menu();
        // boolean go=true;
        while (true) {
            // Get the user's request, and do it
            //
            // NB: for better testability, each action should be a separate
            //     function call
            char action = prompt(in);

            if (action == '?') { 
                menu();
            } else if (action == 'q') { //quit program
                break;
            } else if (action == 'T') { //create message table
                db.createTable();
                System.out.println("  Created: Message table");
            } else if (action == 'D') { //delete message table
                db.dropTable();
                System.out.println("  Dropped: Message table");
            } else if (action == '1') { //select and display one message
                int id = getInt(in, "Enter the message row ID");
                if (id == -1){
                    System.out.println("[ERROR] Invalid message ID");
                    continue;
                }    
                Database.RowData res = db.selectOne(id);
                if (res != null) {
                    System.out.println("  [" + res.mId + "] " + res.mSubject);
                    System.out.println("  --> " + res.mMessage);
                    System.out.println("  Author: " + res.mAuthor);
                }
            } else if (action == '*') { //select and display all messages
                ArrayList<Database.RowData> res = db.selectAll();
                if (res == null)
                    continue;
                System.out.println("  Current Database Contents");
                System.out.println("  -------------------------");
                for (Database.RowData rd : res) {
                    System.out.println("  [" + rd.mId + "] " + rd.mSubject);
                    System.out.println("      Upvotes: " + rd.mUpvotes);
                    System.out.println("      Upvotes: " + rd.mDownvotes);
                }
            } else if (action == '-') { //delete a message
                int id = getInt(in, "Enter the message row ID");
                if (id == -1){
                    System.out.println("[ERROR] Invalid message ID");
                    continue;
                }
                int res = db.deleteRow(id);
                if (res == -1){
                    System.out.println("[ERROR] Unable to delete message row");
                    continue;
                } 
                System.out.println("  " + res + " rows deleted");
            } else if (action == '+') { //add a message
                String subject = getString(in, "Enter the subject");
                String message = getString(in, "Enter the message");
                String author = getString(in, "Enter your email");
                if (subject.equals("") || message.equals("") || author.equals(""))
                    continue;
                int res = db.insertRow(subject, message, author, 0, 0); 
                System.out.println("  "+res + " message added");
            } else if (action == '~') { //Edit a message
                int id = getInt(in, "Enter the message row ID ");
                if (id == -1){
                    System.out.println("[ERROR] Invalid message ID");
                    continue;
                }
                String newSubject = getString(in, "Enter the message subject");
                String newMessage = getString(in, "Enter the new message");
                int res = db.updateOne(id, newSubject, newMessage);
                if (res == -1){
                    System.out.println("[ERROR] Unable to update message");
                    continue;
                }
                    
                System.out.println("  " + res + " message updated");
            } else if (action == 'A') { //add a like
                int id = getInt(in, "Enter the message row ID number");
                if (id == -1) {
                    System.out.println("[ERROR] Invalid message ID");
                    continue;
                }
                String user_id = getString(in, "Enter your user ID");
                if (db.addUpvote(id, user_id) == -1) {
                    System.out.println("[ERROR] Unable to add like");
                    continue;
                }
                System.out.println("Message [" + id +"]: Upvoted");
            } else if (action == 'a') { //remove a like
                int id = getInt(in, "Enter the message row ID number");
                if (id == -1) {
                    System.out.println("[ERROR] Invalid message ID");
                    continue;
                }
                String user_id = getString(in, "Enter your user ID");
                if (db.removeUpvote(id, user_id) == -1) {
                    System.out.println("[ERROR] Unable to remove like");
                    continue;
                }
                System.out.println("Message [" + id +"]: Upvote removed");
            } else if (action == 'B') { //add a dislike
                int id = getInt(in, "Enter the message row ID");
                String user_id = getString(in, "Enter your user ID");
                if (id == -1) {
                    System.out.println("[ERROR] Invalid message ID");
                    continue;
                }
                if (db.addDownvote(id, user_id) == -1) {
                    System.out.println("[ERROR] Unable to add dislike");
                    continue;
                }
                System.out.println("Message [" + id +"]: Downvoted");
            } else if (action == 'b') { //remove a dislike
                int id = getInt(in, "Enter the message row ID");
                String user_id = getString(in, "Enter your user ID");
                if (id == -1) {
                    System.out.println("[ERROR] Invalid message ID");
                    continue;
                }
                if (db.removeDownvote(id, user_id) == -1) {
                    System.out.println("[ERROR] Unable to remove dislike");
                    continue;
                }
                System.out.println("Message [" + id +"]: Downvote removed");
            } else if (action == 'C'){ //create user table
                db.createUsersTable();
                System.out.println("  Created: User table");
            } else if (action == 'c'){ //remove user table
                db.dropUsersTable();
                System.out.println("  Dropped: User table");
            } else if (action == 'I'){ //add a new user
                String user_id = getString(in, "Choose an user ID");
                String name = getString(in, "Enter your first and last name");
                String bio = getString(in, "Enter your bio");
                if (user_id.equals("") || name.equals(""))
                    continue;
                int res = db.addUser(user_id, name, bio);
                System.out.println("  "+res + " user added");
            } else if (action == 'S'){ //select and display one user
                String user_id = getString(in, "Enter user ID");
                Database.UserRow res = db.selectOneUser(user_id);
                if (res != null) {
                    System.out.println("  Name: " + res.mName);
                    System.out.println("  Bio: " + res.mBio);
                }
            } else if (action == 's'){ //select and display all users
                ArrayList<Database.UserRow> res = db.selectAllUsers();
                if (res == null)
                    continue;
                System.out.println("  Current User Database Contents");
                System.out.println("  -------------------------");
                for (Database.UserRow r : res) {
                    System.out.println("  Name: " + r.mName);
                    System.out.println("  Bio: " + r.mBio + '\n');
                }
            } else if (action == 'L'){ //create like table
                db.createLikesTable();
                System.out.println("  Created: Vote table");
            } else if (action == 'l'){ //delete comments table
                db.dropLikesTable();
                System.out.println("  Dropped: Vote table]");
            } else if (action == 'R'){
                db.createCommentsTable();
                System.out.println("  Created: Comment table");
            } else if (action == 'r'){
                db.dropCommentsTable();
                System.out.println("  Dropped: Comment table");
            } else if (action == '#'){ //add a comment
                String user_id = getString(in, "Enter your user ID");
                if (user_id.equals("")){
                    System.out.println("[ERROR] Invalid user ID");
                    continue;
                }
                int id = getInt(in, "Enter message row ID");
                if (id == -1){
                    System.out.println("[ERROR] Invalid message ID");
                    continue;
                }                    
                String comment = getString(in, "Enter your comment");
                if (comment.equals(""))
                    continue;
                int res = db.addComment(user_id, id, comment); 
                if (res ==0){
                    System.out.println("[ERROR] Unable to add comment");
                }
                System.out.println("  "+res + " comment added");
            } else if (action == '@') { //delete a comment
                int cid = getInt(in, "Enter the comment row ID");
                if (cid == -1){
                    System.out.println("[ERROR] Invalid comment ID");
                    continue;
                }
                int id = getInt(in, "Enter message row ID");
                if (cid == -1){
                    System.out.println("[ERROR] Invalid message ID");
                    continue;
                }
                int res = db.deleteOneComment(cid, id);
                if (res == -1){
                    System.out.println("[ERROR] Unable to delete comment");
                    continue;
                }
                System.out.println("  " + res + " comment deleted");
            } else if (action == '$'){ //edit a comment
                String user_id = getString(in, "Enter your user ID");
                if (user_id.equals("")){
                    System.out.println("[ERROR] Invalid user ID");
                    continue;
                }
                int id = getInt(in, "Enter message row ID");
                int cid = getInt(in, "Enter the comment row ID");
                if (id == -1 || cid == -1){
                    System.out.println("[ERROR] Invalid parameters");
                    continue;
                }                    
                String comment = getString(in, "Enter your comment");
                if (comment.equals(""))
                    continue;
                int res = db.UpdateOneComment(comment, cid, id, user_id);
                if (res == -1){
                    System.out.println("[ERROR] Unable to update comment");
                    continue;
                }
                System.out.println("  " + res + " comment updated");
            } else if (action == '^'){ //select and display a comment
                int id = getInt(in, "Enter the message row ID");
                int cid = getInt(in, "Enter the comment row ID");
                if (id == -1 || cid == -1){
                    System.out.println("[ERROR] Invalid parameters");
                    continue;
                }
                Database.CommentRow res = db.selectOneComment(cid, id);
                if (res != null) {
                    System.out.println("User: " + res.mUserId);
                    System.out.println("["+res.mMsgId+"] " + res.mBody);
                }
            } else if (action == '&'){ //get and display all comments
                int id = getInt(in, "Enter the message row ID");
                if (id == -1 ){
                    System.out.println("[ERROR] Invalid message ID");
                    continue;
                }
                ArrayList<Database.CommentRow> res = db.selectAllComments(id);
                if (res == null)
                    continue;
                System.out.println("  Current Comments");
                System.out.println("  ------------------");
                for (Database.CommentRow rd : res) {
                    System.out.println("User: "+ rd.mUserId);
                    System.out.println("["+rd.mId+"] " + rd.mBody + "\n");
                }
            }
        }

        // Always remember to disconnect from the database when the program exits
        db.disconnect();
    }
}