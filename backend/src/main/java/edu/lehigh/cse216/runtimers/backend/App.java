package edu.lehigh.cse216.runtimers.backend;
// Import the Spark package, so that we can make use of the "get" function to 
// create an HTTP GET route
import spark.Spark;

// Import Google's JSON library
import com.google.gson.*;
import java.util.Map;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * For now, our app creates an HTTP server that can only get and add data.
 */
public class App {
    public static void main(String[] args) {

        // gson provides us with a way to turn JSON into objects, and objects
        // into JSON.
        //
        // NB: it must be final, so that it can be accessed from our lambdas
        //
        // NB: Gson is thread-safe.  See 
        // https://stackoverflow.com/questions/10380835/is-it-ok-to-use-gson-instance-as-a-static-field-in-a-model-bean-reuse
        final Gson gson = new Gson();

        //instead of initiating a datastore object, we uses database now
        Map<String, String> env = System.getenv();
        String db_url = "postgres://tsfckvjlfejcen:5881f854074caa7a310678a384311d9deddf589662af9198142c9708aae4619e@ec2-54-146-84-101.compute-1.amazonaws.com:5432/dapbocqmelis1n";
        // String ip = env.get("POSTGRES_IP");
        // String port = env.get("POSTGRES_PORT");
        // String database = env.get("POSTGRES_DATABASE");
        // String user = env.get("POSTGRES_USER");
        // String pass = env.get("POSTGRES_PASS");

        // Get the port on which to listen for requests
        Spark.port(getIntFromEnv("PORT", 5432)); 

        final Database db = Database.getDatabase(db_url);

        // Set up the location for serving static files.  If the STATIC_LOCATION
        // environment variable is set, we will serve from it.  Otherwise, serve
        // from "/web"
        String static_location_override = System.getenv("STATIC_LOCATION");
        if (static_location_override == null) {
            Spark.staticFileLocation("/web");
        } else {
            Spark.staticFiles.externalLocation(static_location_override);
        }

        String cors_enabled = env.get("CORS_ENABLED");
        if (cors_enabled != null &&  cors_enabled.equals("True")) {
            final String acceptCrossOriginRequestsFrom = "*";
            final String acceptedCrossOriginRoutes = "GET,PUT,POST,DELETE,OPTIONS";
            final String supportedRequestHeaders = "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin";
            enableCORS(acceptCrossOriginRequestsFrom, acceptedCrossOriginRoutes, supportedRequestHeaders);
        }
        
        // Set up a route for serving the main page
        // Spark.get("/", (req, res) -> {
        //     res.redirect("/index.html");
        //     return "";
        // });

        // GET route that returns all message titles and Ids.  All we do is get 
        // the data, embed it in a StructuredResponse, turn it into JSON, and 
        // return it.  If there's no data, we return "[]", so there's no need 
        // for error handling.
        Spark.get("/messages", (request, response) -> {
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");
            return gson.toJson(new StructuredResponse("ok", null, db.selectAll()));
        });

        // GET route that returns everything for a single row in the DataStore.
        // The ":id" suffix in the first parameter to get() becomes 
        // request.params("id"), so that we can get the requested row ID.  If 
        // ":id" isn't a number, Spark will reply with a status 500 Internal
        // Server Error.  Otherwise, we have an integer, and the only possible 
        // error is that it doesn't correspond to a row with data.
        Spark.get("/messages/:id", (request, response) -> {
            int idx = Integer.parseInt(request.params("id"));
            response.type("application/json");
            Database.RowData data = db.selectOne(idx);
            if (data == null) {
                response.status(404);
                return gson.toJson(new StructuredResponse("error", idx + " not found", null));
            } else {
                // ensure status 200 OK, with a MIME type of JSON
                response.status(200);
                return gson.toJson(new StructuredResponse("ok", null, data));
            }
        });

        // POST route for adding a new element to the DataStore.  This will read
        // JSON from the body of the request, turn it into a SimpleRequest 
        // object, extract the title and message, insert them, and return the 
        // ID of the newly created row.
        Spark.post("/messages", (request, response) -> {
            // NB: if gson.Json fails, Spark will reply with status 500 Internal 
            // Server Error
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            response.type("application/json");
            // NB: createEntry checks for null title and message
            int newId = db.insertRow(req.mTitle, req.mMessage, 0, 0);
            if (newId == -1) {
                response.status(500);
                return gson.toJson(new StructuredResponse("error", "error performing insertion", null));
            } else if (newId == 0){
                response.status(404);
                return gson.toJson(new StructuredResponse("error", "cannot find table for insertion", null));
            }else {
                response.status(200);
                return gson.toJson(new StructuredResponse("ok", "inserted id: " + newId, null));
            }
        });

        // PUT route for updating a row in the DataBase.  This is almost exactly the same as POST
        Spark.put("/messages/:id", (request, response) -> {
            // If we can't get an ID or can't parse the JSON, Spark will send
            // a status 500
            int idx = Integer.parseInt(request.params("id"));
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");
            int result = db.updateOne(idx, req.mTitle, req.mMessage);
            if (result == -1) {
                // If we can't get an ID or can't parse the JSON, Spark will send a status 500
                response.status(500);
                return gson.toJson(new StructuredResponse("error", "unable to update subject and messsage to row " + idx, null));
            } else {
                response.status(200);
                return gson.toJson(new StructuredResponse("ok", "updated row " + idx, null));
            }
        });

        // DELETE route for removing a row from the DataStore
        Spark.delete("/messages/:id", (request, response) -> {
            // If we can't get an ID, Spark will send a status 500
            int idx = Integer.parseInt(request.params("id"));
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");
            // NB: we won't concern ourselves too much with the quality of the 
            //     message sent on a successful delete
            int result = db.deleteRow(idx);
            if (result == -1) {
                //If we can't get an ID, Spark will send a status 500
                response.status(500);//500: cannot delete
                return gson.toJson(new StructuredResponse("error", "unable to delete row " + idx, null));
            }
            else if(result == 0){
                //couldn't find the row need to be deleted
                response.status(404);//404: object not found
                return gson.toJson(new StructuredResponse("error", "unable to find row " + idx, null));
            }
            else {
                response.status(200); //200:ok
                return gson.toJson(new StructuredResponse("ok", "deleted row "+ idx, null));
            }
        });

        // POST route for incrementing upvote
        Spark.post("/messages/:id/upvote", (request, response) -> {
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            response.type("application/json");
    
            int idx = Integer.parseInt(request.params("id"));
            int result = db.addUpvote(idx);
    
            if (result == -1) {
                response.status(500); //internal server error
                return gson.toJson(new StructuredResponse("error", "unable to add upvote to row " + idx, null));
            } 
            else if (result == 0){
                response.status(404);
                return gson.toJson(new StructuredResponse("error", "unable to find table", null));
            }
            else {
                response.status(200);
                return gson.toJson(new StructuredResponse("ok", "upvote added to row "+ idx, null));
            }
        });
    
    
        // POST route for incrementing downvote
        Spark.post("/messages/:id/downvote", (request, response) -> {
            // NB: if gson.Json fails, Spark will reply with status 500 Internal
            // Server Error
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            response.type("application/json");
    
            int idx = Integer.parseInt(request.params("id"));
            int result = db.addDownvote(idx);
    
            if (result == -1) {
                response.status(500);
                return gson.toJson(new StructuredResponse("error", "unable to add downvote to row " + idx, null));
            } 
            else if (result == 0){
                response.status(404);
                return gson.toJson(new StructuredResponse("error", "unable to find table", null));
            }
            else {
                response.status(200);
                return gson.toJson(new StructuredResponse("ok", "downvote added to row "+ idx, null));
            }
        });

        // DELETE route for decrementing upvote
        Spark.delete("/messages/:id/upvote",(request, response) -> {
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            response.type("application/json");
    
            int idx = Integer.parseInt(request.params("id"));
            int result = db.removeUpvote(idx);
    
            if (result == -1) {
                response.status(500);
                return gson.toJson(new StructuredResponse("error", "unable to delete upvote to row " + idx, null));
            }
            else if (result == 0){
                response.status(404);
                return gson.toJson(new StructuredResponse("error", "unable to find message id to delete upvotes", null));
            }
            else {
                response.status(200);
                return gson.toJson(new StructuredResponse("ok", "deleted upvote in row " + idx, null));
            }
        });

        // DELETE route for decrementing downvote
        Spark.delete("/messages/:id/downvote",(request, response) -> {
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            response.type("application/json");
    
            int idx = Integer.parseInt(request.params("id"));
            int result = db.removeDownvote(idx);
    
            if (result == -1) {
                response.status(500);
                return gson.toJson(new StructuredResponse("error", "unable to delete downvote to row " + idx, null));
            }
            else if (result == 0){
                response.status(404);
                return gson.toJson(new StructuredResponse("error", "unable to find message id to delete downvotes", null));
            }
            else {
                response.status(200);
                return gson.toJson(new StructuredResponse("ok", "deleted downvote in row " + idx, null));
            }
        });
    }
    /**
     * Get an integer environment varible if it exists, and otherwise return the
     * default value.
     * 
     * @envar      The name of the environment variable to get.
     * @defaultVal The integer value to use as the default if envar isn't found
     * 
     * @returns The best answer we could come up with for a value for envar
     */
    static int getIntFromEnv(String envar, int defaultVal) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get(envar) != null) {
            return Integer.parseInt(processBuilder.environment().get(envar));
        }
        return defaultVal;
    }
    /**
     * Set up CORS headers for the OPTIONS verb, and for every response that the
     * server sends.  This only needs to be called once.
     * 
     * @param origin The server that is allowed to send requests to this server
     * @param methods The allowed HTTP verbs from the above origin
     * @param headers The headers that can be sent with a request from the above
     *                origin
     */
    private static void enableCORS(String origin, String methods, String headers) {
        // Create an OPTIONS route that reports the allowed CORS headers and methods
        Spark.options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }
            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            return "OK";
        });

        // 'before' is a decorator, which will run before any 
        // get/post/put/delete.  In our case, it will put three extra CORS
        // headers into the response
        Spark.before((request, response) -> {
            response.header("Access-Control-Allow-Origin", origin);
            response.header("Access-Control-Request-Method", methods);
            response.header("Access-Control-Allow-Headers", headers);
        });
    }
}