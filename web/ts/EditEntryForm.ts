/**
 * EditEntryForm encapsulates all of the code for the form for editing an entry
 */

 class EditEntryForm {

    //track if edit entry form is initialized
    private static isInit = false;


    /**
     * refresh() is the public method used by clickEditPost() from ElementList
     * issue a GET with a specific row id
     * 
     * @param msgId the row if from $(this).data("value") which is mId from mData
     */
    public static refresh(msgId?: Number){
        this.isInit = false;
        //query a row using its row id and pass the result into init()
        $.ajax({
            type: "GET",
            url: backendUrl + "/messages/" + msgId,
            dataType: "json",
            success: function (res: any) {
                console.log("[ajax] Edit entry click response: " + JSON.stringify(res));
                EditEntryForm.init(res);
            }
        });
    }

    /**
     * init() is called from an AJAX GET, and should populate the form if and 
     * only if the GET did not have an error
     */
    private static init(data: any) {
        console.log("edit entry init() function called");
        if (!EditEntryForm.isInit) {
            console.log("inside if");
            data = data || {};
            console.log("parameter: " + JSON.stringify(data));
            $("#left-design").append(Handlebars.templates["EditEntryForm.hb"](data));
            $("#editTitle").val(data.mData.mSubject);
            $("#editMessage").val(data.mData.mMessage);
            $("#editId").val(data.mData.mId);
            $("#EditEntryForm-Update").click(EditEntryForm.submitForm);
            $("#EditEntryForm-Cancel").click(EditEntryForm.clearForm);
            //hide the edit form
            $("#editId").show();
            $("#new-entry-block").remove();
            EditEntryForm.isInit = true;
        }
    }

    /**
     * Clear the form's input fields
     */
    private static clearForm() {
        $("#editTitle").val("");
        $("#editMessage").val("");
        // reset the UI
        $("#editId").remove();
    }

    /**
     * Check if the input fields are both valid, and if so, do an AJAX call.
     */
    private static submitForm() {
        // get the values of the two fields, force them to be strings, and check 
        // that neither is empty
        let title:any = $("#editTitle").val();
        let msg:any = $("#editMessage").val();
        // NB: we assume that the user didn't modify the value of #editId
        let id:any = $("#editId").val();
        // check entry length
        if(title === "" || msg === ""){
            window.alert("Cannot submit empty title or message");
            return;
        }
        if(title.length > 100){
            window.alert("Title too long!");
            return;
        }
        if(msg.length > 500){
            window.alert("Message exceeds 500 words!");
            return;
        }
        // set up an AJAX post.  When the server replies, the result will go to
        // onSubmitResponse
        $.ajax({
            type: "PUT",
            url: backendUrl + "/messages/" + id,
            dataType: "json",
            data: JSON.stringify({
                mTitle: title,
                mMessage: msg
            }),
            success: function (res: any) {
                console.log("[ajax] Update click response: " + JSON.stringify(res));
                EditEntryForm.clearForm();
                ElementList.refresh();
            }
        });
    }
} // end class EditEntryForm