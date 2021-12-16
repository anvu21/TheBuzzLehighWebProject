/**
 * This function will be called when the html is initialized.
 */

 class AppFrontPage {
    /**
     * Track if the Singleton has been initialized
     */
    private static isInit = false;
    
    private static init(){
        console.log("App front Page is initialized")
        if (!AppFrontPage.isInit) {
            console.log("App front page is loaded");
            // add the basic structure to the html
            //$(".container").append(Handlebars.templates["AppFrontPage.hb"]());
            $(".new-post-btn").click(AppFrontPage.showNewEntry); 
            ElementList.refresh();
            console.log("Element list is refreshed");
            AppFrontPage.isInit = true;
        }
    }

    public static refresh(){
        AppFrontPage.init();
    }

    public static showNewEntry(){
        console.log("Show new entry running");
        $("#new-entry-block").show();
        NewEntryForm.refresh();
    }
}// end class AppFrontPage