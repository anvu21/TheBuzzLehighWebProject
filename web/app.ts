/// <reference path="ts/AppFrontPage.ts"/>
/// <reference path="ts/EditEntryForm.ts"/>
/// <reference path="ts/NewEntryForm.ts"/>
/// <reference path="ts/ElementList.ts"/>

// Prevent compiler errors when using jQuery.  "$" will be given a type of 
// "any", so that we can use it anywhere, and assume it has any fields or
// methods, without the compiler producing an error.
var $: any;
let Handlebars: any;


// a global for the NewEntryForm of the program.
var newEntryForm: NewEntryForm;

/// This constant indicates the path to our backend server
const backendUrl = "https://runtimes.herokuapp.com";


// a global for the main ElementList of the program.
var mainList: ElementList;


// Run some configuration code when the web page loads
$(document).ready(function () {
    AppFrontPage.refresh();
});