# Back-End Server

Phase 1 Backend Log (Anh Vu)
App.java
POST route for incrementing upvote
        Spark.post("/messages/:id/upvote", (request, response)

POST route for incrementing downvote
        Spark.post("/messages/:id/downvote", (request, response)

DELETE route for decrementing upvote
        Spark.delete("/messages/:id/upvote",(request, response) 

DELETE route for decrementing downvote
        Spark.delete("/messages/:id/upvote",(request, response) 

Database.java 
    int addUpvote(int id) {
    int addDownvote(int id) {
    int removeUpvote(int id) {
    int removeDownvote(int id) {
    
    testRowData()

DatabaseTest.java

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

Test true



