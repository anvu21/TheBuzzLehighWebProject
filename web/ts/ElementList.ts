/**
 * The ElementList Singleton provides a way of displaying all of the data 
 * stored on the server as an HTML table.
 */
 class ElementList {
    /**
     * The name of the DOM entry associated with ElementList
     */
    private static readonly NAME = "ElementList";


    /**
     * refresh() is the public method for updating the ElementList
     */
    public static refresh() {
        $("#new-entry-block").remove();
        $("#editId").remove();
        // Issue a GET, and then pass the result to update()
        $.ajax({
            type: "GET",
            url: backendUrl + "/messages",
            dataType: "json",
            success: function(res: any){
                console.log("[ajax] All Posts Response: " + JSON.stringify(res));
                ElementList.update(res);
            }
        });
    }

    /**
     * update() is the private method used by refresh() to update the ElementList
     * loads the Element with the data passed by refresh().
     * This function will ONLY be called by refresh(). 
     * 
     * @param data StructuredResponse from backend (String status, String message, Object data)
     */
    private static update(data: any) {
        console.log("update called in ElementList");
        data = data || {};
        console.log("parameter: " + JSON.stringify(data));
        // Remove the table of data, if it exists
        $("#" + ElementList.NAME).remove();
        // Use a template to generate a table from the provided data, and put the table into our messageList element.
        $("#right-design").append(Handlebars.templates["ElementList.hb"](data));
        console.log("handlebar is called");
        // register click events on the list
        $(".edit-btn").click(ElementList.clickEditPost);
        $(".delete-btn").click(ElementList.clickDeletePost);
        $(".my-upvote").click(ElementList.upvotePost);
        $(".my-downvote").click(ElementList.downvotePost);
        ElementList.refresh();
        console.log("Element list is refreshed");
    }

    private static clickEditPost(){
        let id = $(this).data("value");
        console.log("edit post: " + id);
        EditEntryForm.refresh(id);
    }

    // clickDeletePost is the code we run in response to a click of a delete button
    private static clickDeletePost() {
        //print the ID that goes along with the data in the row whose "delete" button was clicked
        let id = $(this).data("value");
        console.log("delete post: " + id);
        $.ajax({
            type: "DELETE",
            url: backendUrl +"/messages/" + id,
            dataType: "json",
            success: function (res:any){
                console.log("[ajax] Delete click response: " + JSON.stringify(res));
                window.alert("Deleted row "+id+" successfully! ");
                ElementList.refresh();
            },
            error: function(){
                console.log("Error deleting row "+id);
                ElementList.refresh();
            }
        });
    }

    // upvotePost is the code we run in response to a click of a upvote button
    private static upvotePost() {
        // Get the ID of the row
        let id = $(this).data("value");
        console.log("upvote post: " + id);
        var upVoteState = $(this).attr("data-upvotestate");
        //user like this post, and haven't clicked like previously
        if (upVoteState === "false") {
            $(this).attr("data-upvotestate", "true");
            $(this).removeClass("my-vote-false").addClass("my-vote-true");
            console.log("adding upvote");
            $.ajax({
                type: "POST",
                url: backendUrl +"/messages/" + id + "/upvote",
                dataType: "json",
                success: function (res:any){
                    console.log("[ajax] Upvote click response: " + JSON.stringify(res));
                    ElementList.refresh();
                },
                error: function(){
                    console.log("Error adding upvote in row "+id);
                    ElementList.refresh();
                }
            });
        }else { //user already clicked upvote
            $(this).attr("data-upvotestate", "false"); //user delete the like when click upvote again
            $(this).removeClass("my-vote-true").addClass("my-vote-false"); //for css file to color change
            console.log("deleting upvote");
            $.ajax({
                type: "DELETE",
                url: backendUrl +"/messages/" + id + "/upvote",
                dataType: "json",
                success: function (res:any){
                    console.log("[ajax] Upvote click response: " + JSON.stringify(res));
                    ElementList.refresh();
                }, 
                error: function(){
                    console.log("Error deleting upvote in row "+id);
                    ElementList.refresh();
                }
            });
        }
    }

    //downvotePost is the code we run in response to a click of a downvote button
    private static downvotePost() {
        // Get the ID of the row
        let id = $(this).data("value");
        console.log("downvote post: " + id);
        var downVoteState = $(this).attr("data-downvotestate");
        if (downVoteState === "true") {
            // user want to cancel the dislike 
            $(this).attr("data-downvotestate", "false");
            $(this).removeClass("my-vote-true").addClass("my-vote-false");
            $.ajax({
                type: "DELETE",
                url: backendUrl +"/messages/" + id + "/downvote",
                dataType: "json",
                success: function (res:any){
                    console.log("[ajax] Downvote click response: " + JSON.stringify(res));
                    ElementList.refresh();
                }, 
                error: function(){
                    console.log("Error deleting downvote in row "+id);
                    ElementList.refresh();
                }
            });
        }
        else {
            // user dislike this post
            $(this).attr("data-downvotestate", "true");
            $(this).removeClass("my-vote-false").addClass("my-vote-true");
            $.ajax({
                type: "POST",
                url: backendUrl +"/messages/" + id + "/downvote",
                dataType: "json",
                success: function (res:any){
                    console.log("[ajax] Downvote click response: " + JSON.stringify(res));
                    ElementList.refresh();
                },
                error: function(){
                    console.log("Error adding downvote in row "+id);
                    ElementList.refresh();
                }
            });
        }
    }
}// end class ElementList