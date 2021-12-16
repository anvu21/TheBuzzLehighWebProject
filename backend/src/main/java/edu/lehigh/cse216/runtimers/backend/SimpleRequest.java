package edu.lehigh.cse216.runtimers.backend;

/**
 * SimpleRequest provides a format for clients to present title and message 
 * strings to the server.
 * 
 * NB: since this will be created from JSON, all fields must be public, and we
 *     do not need a constructor.
 */
public class SimpleRequest {
    //The title being provided by the client.
    public String mTitle;

    //The message being provided by the client.
    public String mMessage;

    //The number of upvotes being provided by the client
    public int mUpvote;

    //The number of downvotes being provided by the client
    public int mDownvote;
}