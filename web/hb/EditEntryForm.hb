<div class = "EditEntryForm" id="editId" data-value="{{this.mId}}">
    <div class = "edit-title-block">
        Edit Title
        <textarea class="text-input" id="editTitle">
            {{this.mSubject}}
        </textarea>
        </input>
    </div>
    <br/><br/>
    <div class="edit-message-block">
        Edit Message
        <textarea class="text-input" id="editMessage">
            {{this.mMessage}}
        </textarea>
        </input>
    </div>
    <br/><br/>
    <button class="update-btn" id="EditEntryForm-Update">
        <span class="glyphicon glyphicon-floppy-saved"></span>
        Save Change
    </button>
    <button class="edit-cancel" id="EditEntryForm-Cancel">
        <span class="glyphicon glyphicon-floppy-remove"></span>
        Cancel
    </button>
</div>