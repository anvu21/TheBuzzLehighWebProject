var describe: any;
var it: any;
var expect: any;

describe("AppFrontPage tests", function() {
    /**
     * When new post button is clicked, 
     *     1) new post block should be shown
     *     2) all other blocks should be hidden
     */
    it("Click new post button", function() {
        $('.new-post-btn').trigger("click");
        expectHidden($("#new-entry-block"), false);
        expectHidden($(".edit-entry-block"), true);
        expectHidden($(".ElementList"), true);
        // no need to reset the UI
    });

});
describe("ElementList tests", function() {
    /**
     * When new post button is clicked, 
     *     1) new post block should be shown
     *     2) all other blocks should be hidden
     */
    it("Click edit post button", function() {
        let msgId = $(".detailed-post-view").first().data("value");
        $('.edit-btn').trigger("click");
        expectHidden($("#new-entry-block"), false);
        expectHidden($(".EditEntryForm"), true);
        expectHidden($(".ElementList"), false);
        // no need to reset the UI
    });

});