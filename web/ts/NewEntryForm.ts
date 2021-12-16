/**
 * NewEntryForm encapsulates all of the code for the form for adding an entry
 */
 class NewEntryForm {
    /**
     * The name of the DOM entry associated with NewEntryForm
     */
    private static readonly NAME = "NewEntryForm";

    /**
     * Track if the Singleton has been initialized
     */
    private static isInit = false;

    /**
     * Initialize the NewEntryForm by creating its element in the DOM and 
     * configuring its buttons.  This needs to be called from any public static 
     * method, to ensure that the Singleton is initialized before use
     */
    private static init() {
        console.log("New Entry Form is initialized")
        if (!NewEntryForm.isInit) {
            console.log("inside if");
            $("#new-entry-block").hide();
            $("#NewEntryForm-title").val("");
            $("#NewEntryForm-message").val("");
            $("#left-design").append(Handlebars.templates["NewEntryForm.hb"]());
            $("#NewEntryForm-OK").click(NewEntryForm.submitForm);
            $("#NewEntryForm-Close").click(NewEntryForm.clearForm);
            NewEntryForm.isInit = true;
        }
    }

    /**
     * Refresh() doesn't really have much meaning, but just like in sNavbar, we
     * have a refresh() method so that we don't have front-end code calling
     * init().
     */
    public static refresh() {
        this.isInit = false;
        this.init();
    }

    /**
     * Hide the NewEntryForm.  Be sure to clear its fields first
     */
    private static clearForm() {
        console.log("clearForm() called");
        $("#NewEntryForm-title").val("");
        $("#NewEntryForm-message").val("");
        $("#new-entry-block").remove();
    }

    // /**
    //  * Show the NewEntryForm.  Be sure to clear its fields, because there are
    //  * ways of making a Bootstrap modal disapper without clicking Close, and
    //  * we haven't set up the hooks to clear the fields on the events associated
    //  * with those ways of making the modal disappear.
    //  */
    // public static show() {
    //     $("#NewEntryForm-title").val("");
    //     $("#NewEntryForm-message").val("");
    //     $("#new-entry-block").modal("show");
    // }


    /**
     * Send data to submit the form only if the fields are both valid.  
     * Immediately hide the form when we send data, so that the user knows that 
     * their click was received.
     */
    private static submitForm() {
        console.log("Submiting a new entry");
        // get the values of the two fields, force them to be strings, and check 
        // that neither is empty
        let title:any = $("#NewEntryForm-title").val();
        let msg:any = $("#NewEntryForm-message").val();
        //check new entry
        if(title === "" || msg === ""){
            window.alert("Cannot leave title or message empty!");
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
        // NewEntryForm.hide();
        //send request to backend
        console.log("Title:" + title + ", Message: "+ msg);
        $.ajax({
            type: "POST",
            url: backendUrl + "/messages",
            dataType: "json",
            data: JSON.stringify({ mTitle: title, mMessage: msg}),
            success: function (res: any) {
                console.log("[ajax] New Post Response: " + JSON.stringify(res));
                NewEntryForm.clearForm();
                ElementList.refresh();
            }
        });
    }
}
